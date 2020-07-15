package xzf.spiderman.worker.service;

import xzf.spiderman.worker.configuration.HessianRedisTemplate;
import static xzf.spiderman.worker.configuration.WorkerConst.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SpiderTaskRepository
{
    private HessianRedisTemplate redisTemplate;

    public SpiderTaskRepository(HessianRedisTemplate redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    // Map{ spiderKey : Map{ cnfId : spiderData }  }
    private final Map<GroupSpiderKey, Map<String, SpiderTask>> data = new HashMap<>();

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
                Map<String, SpiderTask> tasks = (Map<String, SpiderTask>) entry.getValue();
                if(tasks.isEmpty()){continue;}

                String groupId = groupId(tasks);
                data.put(new GroupSpiderKey(spiderId, groupId), tasks);
            }
        }finally {
            lock.writeLock().unlock();
        }
    }

    private String groupId(Map<String, SpiderTask> map){
        return map.values().iterator().next().getGroupId();
    }


    public void put(GroupSpiderKey key, Map<String, SpiderTask> tasks)
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


    public void update(GroupSpiderKey key, SpiderTask task)
    {
        lock.writeLock().lock();

        try {
            Map<String, SpiderTask> taskMap = data.get(key);

            SpiderTask src = taskMap.get(task.getCnfId());
            src.setStatus(task.getStatus());

            redisTemplate.opsForHash().put(REDIS_SPIDER_TASK_KEY, key.getSpiderId(), taskMap);
        }finally {
            lock.writeLock().unlock();;
        }
    }

    public void remove(GroupSpiderKey key)
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

    public void remove(GroupSpiderKey key, SpiderTask task)
    {
        lock.writeLock().lock();

        try {
            Map<String, SpiderTask> taskMap = data.get(key);

            taskMap.remove(task.getCnfId());

            redisTemplate.opsForHash().put(REDIS_SPIDER_TASK_KEY, key.getSpiderId(), taskMap);
        }finally {
            lock.writeLock().unlock();
        }
    }


    public List<SpiderTask> getTasks(GroupSpiderKey key)
    {
        lock.readLock().lock();
        try {
            return new ArrayList<>(data.get(key).values());
        }finally {
            lock.readLock().unlock();
        }
    }


}
