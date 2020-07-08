package xzf.spiderman.worker.configruation;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import xzf.spiderman.worker.configuration.HessianRedisSerializer;
import xzf.spiderman.worker.entity.SpiderGroup;

import java.util.ArrayList;
import java.util.List;

public class HessianRedisSerializerTest
{
    @Test
    public void test()
    {
        //Method threw 'java.lang.ArrayIndexOutOfBoundsException' exception. Cannot evaluate com.caucho.hessian.util.IdentityIntMap.toString()

        HessianRedisSerializer hessianRedisSerializer = new HessianRedisSerializer();

        SpiderGroup spiderGroup = new SpiderGroup();
        spiderGroup.setId("test111");
        spiderGroup.setName("hello");
        spiderGroup.setDesc("hahaha");

        byte[] bytes = hessianRedisSerializer.serialize(spiderGroup);
        System.out.println(bytes.length);


        byte[] jsonbytes = JSON.toJSONBytes(spiderGroup);
        System.out.println(jsonbytes.length);

//        SpiderGroup load = (SpiderGroup) hessianRedisSerializer.deserialize(bytes);
//
//        System.out.println("load="+ JSON.toJSONString(load));

    }

    @Test
    public void testArrays() throws Exception
    {
        List<SpiderGroup> sg = new ArrayList<>();
        for(int i=0; i<100; i++)
        {
            SpiderGroup spiderGroup = new SpiderGroup();
            spiderGroup.setId("test111" + i);
            spiderGroup.setName("hello" + i);
            spiderGroup.setDesc("hahaha" + i);
            sg.add(spiderGroup);
        }

        byte[] bytes = new HessianRedisSerializer().serialize(sg);
        System.out.println(bytes.length);


        byte[] jsonbytes = JSON.toJSONBytes(sg);
        System.out.println(jsonbytes.length);

    }

}
