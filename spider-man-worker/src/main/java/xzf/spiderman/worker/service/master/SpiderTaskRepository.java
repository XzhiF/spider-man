package xzf.spiderman.worker.service.master;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.configuration.HessianRedisTemplate;
import xzf.spiderman.worker.service.GroupSpiderKey;
import xzf.spiderman.worker.service.SpiderTask;

import static xzf.spiderman.worker.configuration.WorkerConst.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
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
        this.groupKeys = new ConcurrentHashMap<>(16);
    }


    public boolean hasRunningGroup(String groupId)
    {
        return redisTemplate.hasKey(REDIS_RUNNING_SPIDER_GROUP_LOCK_PREFIX+groupId);
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


    public void putAllAndLock(GroupSpiderKey key, List<SpiderTask> tasks)
    {
        lock.writeLock().lock();
        try
        {
            boolean success =  redisTemplate.opsForValue().setIfAbsent(
                    REDIS_RUNNING_SPIDER_GROUP_LOCK_PREFIX+key.getGroupId(),
                    key.getSpiderId(),
                    3,
                    TimeUnit.MINUTES
            );
            if(!success){
                throw new BizException("Spider Group ["+key.getGroupId()+"] 已经运行。不可同时运行多个相同group任务。");
            }


            // 1. 为何task的值
            Map<String, SpiderTask> taskMap = new HashMap<>();
            for (SpiderTask task : tasks) {
                taskMap.put(task.getCnfId(), task);
            }
            this.tasks.put(key, taskMap);
            redisTemplate.opsForHash().put(REDIS_RUNNING_SPIDER_TASK_KEY, key.getSpiderId(), taskMap);

            //
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

//            SpiderTask src = taskMap.get(task.getCnfId());
//            src.setStatus(task.getStatus());
            taskMap.put(task.getCnfId(), task);

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

            // 删除锁
            // lua ->  原子读取然后删除
            String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
                    "then " +
                    "    return redis.call(\"del\",KEYS[1]) " +
                    "else " +
                    "    return 0 " +
                    "end";

            redisTemplate.execute(new DefaultRedisScript(script),
                    Arrays.asList(REDIS_RUNNING_SPIDER_GROUP_LOCK_PREFIX+key.getGroupId(),
                    key.getSpiderId()));

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


    //
    public Map<String, String> getGroupKeys()
    {
        return groupKeys;
    }

    public void removeGroupKeys(String groupId)
    {
        groupKeys.remove(groupId);
    }
}
