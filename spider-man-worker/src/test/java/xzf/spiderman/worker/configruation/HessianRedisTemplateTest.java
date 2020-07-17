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

import java.util.concurrent.*;

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
                System.out.println("other thread.. mianThread interrputed="+mainThread.isInterrupted());
            } catch (InterruptedException e) {
            }
        }).start();

        try {
            Object o = hessianRedisTemplate.opsForList().leftPop("test:spiderman.blockinglist", 1, TimeUnit.MINUTES);
            System.out.println("un-interrupted");
        }catch (Exception e )
        {
            System.out.println("mainThreadInterrput="+Thread.currentThread().isInterrupted());
            e.printStackTrace();
            // RedisCommandInterruptedException
            System.out.println(e.getMessage());
        }
        System.out.println("interrupted");
    }


    @Test
    public void testRBPollInThreadPool_1() throws Exception
    {
        System.out.println("before...");

        CopyOnWriteArrayList<Thread> list = new CopyOnWriteArrayList<>();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    Object o = hessianRedisTemplate.opsForList().leftPop("test:spiderman.blockinglist", 1, TimeUnit.MINUTES);
                    System.out.println("un-interrupted");
                }catch (Exception e){
                    System.out.println("interruped, exception="+e);
                    System.out.println(Thread.currentThread()+ "="+Thread.currentThread().isInterrupted());
                }
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                list.add(t);
                return t;
            }
        });
        executor.execute(task);

        TimeUnit.SECONDS.sleep(5L);

//        executor.shutdown();
//        System.out.println("invoke shutdown");

        executor.shutdownNow();
        System.out.println("invoke shutdownNow");


        TimeUnit.SECONDS.sleep(5L);

//        System.out.println("invoke list interrupted = "+list.size());
//
//        list.forEach(t->t.interrupt());
//
//        TimeUnit.SECONDS.sleep(10L);


    }

}
