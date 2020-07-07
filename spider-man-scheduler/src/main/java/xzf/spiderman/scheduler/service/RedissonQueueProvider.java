package xzf.spiderman.scheduler.service;

import org.redisson.api.RBoundedBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import xzf.spiderman.scheduler.configuration.SchedulerConst;
import xzf.spiderman.scheduler.data.ScheCmd;

@Repository
public class RedissonQueueProvider
{
    @Autowired
    private RedissonClient redisson;


    public RBoundedBlockingQueue<ScheCmd> scheCmdQueue()
    {
        return redisson.getBoundedBlockingQueue(SchedulerConst.SCHEDULE_QUEUE_NAME);
    }
}
