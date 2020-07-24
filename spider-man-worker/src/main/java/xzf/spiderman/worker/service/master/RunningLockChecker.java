package xzf.spiderman.worker.service.master;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import xzf.spiderman.worker.configuration.HessianRedisTemplate;
import xzf.spiderman.worker.configuration.WorkerConst;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static xzf.spiderman.worker.configuration.WorkerConst.REDIS_RUNNING_SPIDER_GROUP_LOCK_PREFIX;

public class RunningLockChecker
{
    private final ExecutorService executor;

    private final AtomicBoolean exit = new AtomicBoolean(false);

    private final HessianRedisTemplate redisTemplate;
    private final SpiderTaskRepository repository;

    public RunningLockChecker(HessianRedisTemplate redisTemplate,SpiderTaskRepository repository) {
        this.redisTemplate = redisTemplate;
        this.repository = repository;
        this.executor = createExecutor();
    }

    private ExecutorService createExecutor()
    {
        ThreadFactory factory = r -> new Thread(new ThreadGroup("RunningLockChecker"), r, "RunningLockCheckerThread");

        return new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1),factory);
    }



    public void start()
    {
        Runnable runnable = ()->{

            while (exit.get() && !Thread.currentThread().isInterrupted() ){

                try {
                    TimeUnit.MINUTES.sleep(2);
                } catch (InterruptedException e) {
                }

                Map<String, String> groupKeys = new HashMap<>(repository.getGroupKeys()) ;

                for (Map.Entry<String, String> entry : groupKeys.entrySet())
                {
                    String groupId = entry.getKey();
                    String spiderId = entry.getValue();

                    String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
                            "then " +
                            "   redis.call(\"expire\",KEYS[1], 180) " +
                            "   return 1 " +
                            "else " +
                            "    return 0 " +
                            "end";

                    Long effect =  redisTemplate.execute(new DefaultRedisScript<Long>(script),
                            Arrays.asList(REDIS_RUNNING_SPIDER_GROUP_LOCK_PREFIX+groupId,
                                    spiderId));

                    if(effect==0){
                        repository.removeGroupKeys(groupId);
                    }
                }
            }
        };

        executor.execute(runnable);
    }


    public void close()
    {
        exit.set(true);
        executor.shutdown();

        try {
            executor.awaitTermination(5L,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }


}
