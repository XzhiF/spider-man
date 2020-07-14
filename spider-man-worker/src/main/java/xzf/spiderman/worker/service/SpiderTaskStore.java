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

    // Map{ spiderKey : Map{ cnfId : spiderData }  }
    private final Map<SpiderKey, Map<String,SpiderTaskData>> data = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    // 从redis，同步本地缓存
    public void sync()
    {
        lock.writeLock().lock();
        try {
            data.clear();
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(REDIS_SPIDER_TASK_KEY);
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String spiderId = entry.getKey().toString();
                Map<String, SpiderTaskData> tasks = (Map<String, SpiderTaskData>) entry.getValue();
                if(tasks.isEmpty()){continue;}

                String groupId = groupId(tasks);
                data.put(new SpiderKey(spiderId, groupId), tasks);
            }
        }finally {
            lock.writeLock().unlock();
        }
    }

    private String groupId(Map<String, SpiderTaskData> map){
        return map.values().iterator().next().getGroupId();
    }


    public void put(SpiderKey key, Map<String,SpiderTaskData> tasks)
    {
        lock.writeLock().lock();
        try{
            data.put(key, tasks);
            redisTemplate.opsForHash().put(REDIS_SPIDER_TASK_KEY, key.getSpiderId(), tasks);
        }
        finally {
            lock.writeLock().unlock();
        }
    }


    public void update(SpiderKey key, SpiderTaskData task)
    {
        lock.writeLock().lock();

        try {
            Map<String, SpiderTaskData> taskMap = data.get(key);

            SpiderTaskData src = taskMap.get(task.getCnfId());
            src.setStatus(task.getStatus());

            redisTemplate.opsForHash().put(REDIS_SPIDER_TASK_KEY, key.getSpiderId(), taskMap);
        }finally {
            lock.writeLock().unlock();;
        }
    }

    public void remove(SpiderKey key)
    {
        lock.writeLock().lock();

        try
        {
            data.remove(key);
            redisTemplate.opsForHash().delete(REDIS_SPIDER_TASK_KEY, key.getSpiderId());
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public void remove(SpiderKey key, SpiderTaskData task)
    {
        lock.writeLock().lock();

        try {
            Map<String, SpiderTaskData> taskMap = data.get(key);

            taskMap.remove(task.getCnfId());

            redisTemplate.opsForHash().put(REDIS_SPIDER_TASK_KEY, key.getSpiderId(), taskMap);
        }finally {
            lock.writeLock().unlock();
        }
    }


    public List<SpiderTaskData> getTasks(SpiderKey key)
    {
        lock.readLock().lock();
        try {
            return new ArrayList<>(data.get(key).values());
        }finally {
            lock.readLock().unlock();
        }
    }


}
