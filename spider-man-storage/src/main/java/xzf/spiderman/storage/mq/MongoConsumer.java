package xzf.spiderman.storage.mq;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import xzf.spiderman.common.cache.DelayCache;
import xzf.spiderman.common.cache.MemDelayCache;
import xzf.spiderman.common.exception.ConfigNotValidException;
import xzf.spiderman.worker.data.StoreCnfData;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static xzf.spiderman.storage.configuration.StorageConst.KAFKA_SPIDER_MAN_STORAGE;

@Slf4j
public class MongoConsumer
{
    private MongoTemplateFactory factory = new MongoTemplateFactory();



    @KafkaListener(topics = KAFKA_SPIDER_MAN_STORAGE, groupId = "mongo",concurrency = "${xzf.spiderman.storage.consumerThreadPool:1}")
    public void handle(ConsumerRecord rec , Consumer consumer)
    {
        JSONObject val = JSON.parseObject( (String) rec.value() );
        Map<String,Object> data = val.getObject("data",Map.class);
        StoreCnfData store = val.getObject("cfg", StoreCnfData.class);

        MongoTemplate mongo = getMongoTemplate(store);
        BSONObject bson = new BasicBSONObject(data);
        mongo.insert(bson, store.getTableName());

        // 保存我的错误消息成功了之后，才做commit -> 异步->

        // commit
        TopicPartition topicPartition = new TopicPartition(rec.topic(), rec.partition());
        OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(rec.offset()+1);
        consumer.commitSync(Collections.singletonMap(topicPartition,offsetAndMetadata));
    }

    private MongoTemplate getMongoTemplate(StoreCnfData store)
    {
        return factory.createAndGet(store);
    }


}
