package xzf.spiderman.worker.configruation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

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


}
