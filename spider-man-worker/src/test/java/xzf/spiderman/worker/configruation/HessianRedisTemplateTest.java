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

}
