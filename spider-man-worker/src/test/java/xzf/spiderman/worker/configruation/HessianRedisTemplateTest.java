package xzf.spiderman.worker.configruation;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import xzf.spiderman.worker.configuration.HessianRedisTemplate;
import xzf.spiderman.worker.entity.SpiderGroup;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HessianRedisTemplateTest
{
    @Autowired
    private HessianRedisTemplate hessianRedisTemplate;


    @Test
    public void testCreate()
    {
        System.out.println(hessianRedisTemplate);
    }

    @Test
    public void setAndGetValue()
    {
        SpiderGroup spiderGroup = new SpiderGroup();
        spiderGroup.setId("test11222");
        spiderGroup.setName("hello");
        spiderGroup.setDesc("hahaha");

        ValueOperations<String ,Object> valueOperations= hessianRedisTemplate.opsForValue();

        valueOperations.set("test:spdiergorup", spiderGroup);

        SpiderGroup load =
                (SpiderGroup) valueOperations.get("test:spdiergorup");

        System.out.println("load="+ JSON.toJSONString(load));
    }

    @Test
    public void testRBPoll() throws Exception
    {
        System.out.println("before...");
        final Thread mainThread = Thread.currentThread();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(8);
                mainThread.interrupt();
            } catch (InterruptedException e) {
            }
        }).start();

        try {
            Object o = hessianRedisTemplate.opsForList().leftPop("test:spiderman.blockinglist", 1, TimeUnit.MINUTES);
            System.out.println("un-interrupted");
        }catch (Exception e )
        {
            // RedisCommandInterruptedException
            System.out.println(e.getMessage());
        }
        System.out.println("interrupted");
    }

}
