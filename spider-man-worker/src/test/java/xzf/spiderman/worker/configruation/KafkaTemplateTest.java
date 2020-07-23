package xzf.spiderman.worker.configruation;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import xzf.spiderman.worker.configuration.WorkerConst;
import xzf.spiderman.worker.data.StoreCnfData;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaTemplateTest
{
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Test
    public void testSend() throws Exception
    {
        for (int i = 0; i < 10; i++) {
            kafkaTemplate.send("test-topic","key"+i,"val"+i);
        }
    }


    @Test
    public void testSend2() throws Exception
    {
        Map<String,Object> data = new HashMap<>();
        data.put("id",1235467);
        data.put("author","zhangshan");
        data.put("title","helloworld");

        StoreCnfData store = new StoreCnfData();
        store.setHost("mongo.spiderman.xzf");
        store.setPort(27017);
        store.setTableName("test1");
        store.setUsername("root");
        store.setPassword("123456");
        store.setType(1);
        store.setDatabase("admin");
        store.setCnfId("spider_1");
        store.setStoreId("store_1");


        Map<String,Object> msg = new HashMap<>();
        msg.put("data",data);
        msg.put("cfg", store);

        kafkaTemplate.send(WorkerConst.KAFKA_SPIDER_MAN_STORAGE, JSON.toJSONString(msg)).get();

        System.out.println("sended------");
    }



}
