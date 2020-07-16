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
    private final HessianRedisTemplate redisTemplate;

    // Map{ spiderKey : Map{ cnfId : spiderData }  }
    private final Map<GroupSpiderKey, Map<String, SpiderTask>> tasks;

    // Map { groupId , spiderId }
    private final Map<String, String> groupKeys;

    private final ReadWriteLock lock;


    public SpiderTaskRepository(HessianRedisTemplate redisTemplate)
    {
        this.redisTemplate = redisTemplate;
        this.lock = new ReentrantReadWriteLock();
        this.tasks = new HashMap<>();
        this.groupKeys = new HashMap<>();
    }


    public boolean hasRunningGroup(String groupId)
    {
        lock.readLock().lock();
        try{
            return groupKeys.containsKey(groupId);
        }finally {
            lock.readLock().unlock();
        }
    }

    // 从redis，同步本地缓存
    public void sync()
    {
        lock.writeLock().lock();
        try {

            // 1. init tasks
            tasks.clear();
            Map<Object, Object> taskEntries = redisTemplate.opsForHash().entries(REDIS_RUNNING_SPIDER_TASK_KEY);
            for (Map.Entry<Object, Object> entry : taskEntries.entrySet()) {
                String spiderId = entry.getKey().toString();
                Map<String, SpiderTask> tasks = (Map<String, SpiderTask>) entry.getValue();
                if(tasks.isEmpty()){continue;}

                String groupId = groupId(tasks);
                this.tasks.put(new GroupSpiderKey(spiderId, groupId), tasks);
            }


            // 2. init groupKeys
            groupKeys.clear();
            Map<Object, Object> groupEntries = redisTemplate.opsForHash().entries(REDIS_RUNNING_SPIDER_GROUP_KEY);
            for (Map.Entry<Object, Object> entry : groupEntries.entrySet()) {
                groupKeys.put(entry.getKey().toString(), entry.getValue().toString());
            }

        }finally {
            lock.writeLock().unlock();
        }
    }

    private String groupId(Map<String, SpiderTask> map)
    {
        return map.values().iterator().next().getGroupId();
    }


    public void putAll(GroupSpiderKey key, List<SpiderTask> tasks)
    {
        lock.writeLock().lock();
        try
        {
            // 1. 为何task的值
            Map<String, SpiderTask> taskMap = new HashMap<>();
            for (SpiderTask task : tasks) {
                taskMap.put(task.getCnfId(), task);
            }
            this.tasks.put(key, taskMap);
            redisTemplate.opsForHash().put(REDIS_RUNNING_SPIDER_TASK_KEY, key.getSpiderId(), taskMap);

            groupKeys.put(key.getGroupId(), key.getSpiderId());
            redisTemplate.opsForHash().put( REDIS_RUNNING_SPIDER_GROUP_KEY , key.getGroupId(), key.getSpiderId()  );
        }
        finally {
            lock.writeLock().unlock();
        }
    }


    public void update(GroupSpiderKey key, SpiderTask task)
    {
        lock.writeLock().lock();

        try {
            Map<String, SpiderTask> taskMap = tasks.get(key);

            SpiderTask src = taskMap.get(task.getCnfId());
            src.setStatus(task.getStatus());

            redisTemplate.opsForHash().put(REDIS_RUNNING_SPIDER_TASK_KEY, key.getSpiderId(), taskMap);
        }finally {
            lock.writeLock().unlock();;
        }
    }

    public void removeAll(GroupSpiderKey key)
    {
        lock.writeLock().lock();

        try
        {
            tasks.remove(key);
            redisTemplate.opsForHash().delete(REDIS_RUNNING_SPIDER_TASK_KEY, key.getSpiderId());

            groupKeys.remove(key.getGroupId());
            redisTemplate.opsForHash().delete( REDIS_RUNNING_SPIDER_GROUP_KEY , key.getGroupId());
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public void remove(GroupSpiderKey key, SpiderTask task)
    {
        lock.writeLock().lock();

        try {
            Map<String, SpiderTask> taskMap = tasks.get(key);

            taskMap.remove(task.getCnfId());

            redisTemplate.opsForHash().put(REDIS_RUNNING_SPIDER_TASK_KEY, key.getSpiderId(), taskMap);
        }finally {
            lock.writeLock().unlock();
        }
    }


    public List<SpiderTask> getTasks(GroupSpiderKey key)
    {
        lock.readLock().lock();
        try {
            return new ArrayList<>(tasks.get(key).values());
        }finally {
            lock.readLock().unlock();
        }
    }


}
