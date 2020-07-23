package xzf.spiderman.storage.mq;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import xzf.spiderman.storage.configuration.StorageProperties;
import xzf.spiderman.worker.data.StoreCnfData;
import xzf.spiderman.worker.data.StoreDataReq;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static xzf.spiderman.storage.configuration.StorageConst.*;

/**
 *
 // - 数据保存redis.
 // - 临时记录 ->
 // - 记录错误数据 -> 通过key -> 临时记录
 // 当保存数据库完成，把这条临时记录删除

 */
@Slf4j
public class MongoConsumer implements Closeable
{
    private final StringRedisTemplate template;
    private final MongoTemplateFactory factory;
    private final ExecutorService executor;

    public MongoConsumer(StringRedisTemplate template, StorageProperties properties) {
        this.template = template;
        this.factory = new MongoTemplateFactory();
        this.executor = createExecutor(properties);
    }


    @KafkaListener(topics = KAFKA_SPIDER_MAN_STORAGE_QUEUE, groupId = "mongo",concurrency = "${xzf.spiderman.storage.consumerThreadPool:1}")
    public void handle(ConsumerRecord rec , Consumer consumer)
    {
        log.info("begin consume rec....." + KAFKA_SPIDER_MAN_STORAGE_QUEUE + ":mongo" + ":"  + rec.partition() + ":"  + rec.offset());
//        topic,mongo,partition,offset

        // 1. 保存到redis 临时保存
        final String hashKey = KAFKA_SPIDER_MAN_STORAGE_QUEUE + ":mongo" + ":"  + rec.partition() + ":"  + rec.offset();
        template.opsForHash().put(REDIS_SPIDER_MAN_STORAGE_MAP_KEY, hashKey, rec.value());

        // 2. commit
        TopicPartition topicPartition = new TopicPartition(rec.topic(), rec.partition());
        OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(rec.offset()+1);
        consumer.commitSync(Collections.singletonMap(topicPartition,offsetAndMetadata));

        // 3. 异步执行保存数据逻辑
        executor.execute(()->{
            try {
                StoreDataReq req = JSON.parseObject((String) rec.value(), StoreDataReq.class);

                Map<String, Object> data = req.getData();
                StoreCnfData cnf = req.findCnfByType(StoreDataReq.TYPE_MONGO);

                if(cnf != null){
                    MongoTemplate mongo = getMongoTemplate(cnf);
                    BSONObject bson = new BasicBSONObject(data);
                    mongo.insert(bson, cnf.getTableName());
                }

                template.opsForHash().delete(REDIS_SPIDER_MAN_STORAGE_MAP_KEY, hashKey);

            }catch (Exception e){
                log.error("保存数据失败"+e.getMessage() , e );
                template.opsForSet().add(REDIS_SPIDER_MAN_STORAGE_ERROR_SET_KEY, hashKey);
            }
        });

        log.info("end consume rec....." + KAFKA_SPIDER_MAN_STORAGE_QUEUE + ":mongo" + ":"  + rec.partition() + ":"  + rec.offset());
    }

    private MongoTemplate getMongoTemplate(StoreCnfData store)
    {
        return factory.createAndGet(store);
    }

    private ExecutorService createExecutor(StorageProperties properties)
    {
        ThreadGroup threadGroup = new ThreadGroup("MongoConsumer");
        StorageProperties.ConsumerThreadPool poolCnf = properties.getConsumerThreadPool();

        ThreadFactory factory = new ThreadFactory() {
            AtomicInteger workNo=new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r)
            {
                String name = threadGroup.getName()+"-worker"+"-" +workNo.incrementAndGet()+"-max-"+poolCnf.getMaximumPoolSize();
                return new Thread(threadGroup, r, name);
            }
        };

        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy(){
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                log.warn("MongoConsumer-线程池已满。数量是 "+poolCnf.getWorkQueue()+"。目前在Consumer线程执行， 请检查程序。");
                super.rejectedExecution(r, e);
            }
        };

        return new ThreadPoolExecutor(
                poolCnf.getCorePoolSize(),
                poolCnf.getMaximumPoolSize(),
                poolCnf.getKeepAliveTimeSeconds(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(poolCnf.getWorkQueue()),
                factory,
                handler);
    }


    @Override
    public void close() throws IOException
    {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("executor尝试正常停止失败。");
            executor.shutdownNow();
        }
    }

}
