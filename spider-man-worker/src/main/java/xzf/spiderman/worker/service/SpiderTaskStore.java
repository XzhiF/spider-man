package xzf.spiderman.worker.service;

import xzf.spiderman.worker.configuration.HessianRedisTemplate;
import static xzf.spiderman.worker.configuration.WorkerConst.*;
import xzf.spiderman.worker.data.SpiderTaskData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SpiderTaskStore
{
    private HessianRedisTemplate redisTemplate;

    public SpiderTaskStore(HessianRedisTemplate redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    private final Map<String, Map<String,SpiderTaskData>> data = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    public void put(String spiderTaskId, Map<String,SpiderTaskData> tasks)
    {
        lock.writeLock().lock();
        try{
            data.put(spiderTaskId, tasks);
            redisTemplate.opsForHash().put(REDIS_SPIDER_TASK_KEY, spiderTaskId, tasks);
        }
        finally {
            lock.writeLock().unlock();
        }
    }


    public void update(String spiderTaskId, SpiderTaskData task)
    {
        lock.writeLock().lock();

        try {
            Map<String, SpiderTaskData> taskMap = data.get(spiderTaskId);

            SpiderTaskData src = taskMap.get(task.getCnfId());
            src.setStatus(task.getStatus());

            redisTemplate.opsForHash().put(REDIS_SPIDER_TASK_KEY, spiderTaskId, taskMap);
        }finally {
            lock.writeLock().unlock();;
        }
    }

    public void remove(String spiderTaskId, SpiderTaskData task)
    {
        lock.writeLock().lock();

        try {
            Map<String, SpiderTaskData> taskMap = data.get(spiderTaskId);

            taskMap.remove(task.getCnfId());

            redisTemplate.opsForHash().put(REDIS_SPIDER_TASK_KEY, spiderTaskId, taskMap);
        }finally {
            lock.writeLock().unlock();
        }
    }


    public List<SpiderTaskData> getTasks(String spiderTaskId)
    {
        lock.readLock().lock();
        try {
            return new ArrayList<>(data.get(spiderTaskId).values());
        }finally {
            lock.readLock().unlock();
        }
    }


}
