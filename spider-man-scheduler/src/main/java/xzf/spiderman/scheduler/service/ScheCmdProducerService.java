package xzf.spiderman.scheduler.service;

import org.redisson.api.RBoundedBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.common.exception.RequestTimeoutException;
import xzf.spiderman.scheduler.configuration.SchedulerConst;
import xzf.spiderman.scheduler.data.ScheCmd;

import java.util.concurrent.TimeUnit;

@Service
public class ScheCmdProducerService implements InitializingBean
{
    @Autowired
    private RedissonClient redisson;

    private RBoundedBlockingQueue<ScheCmd> queue;


    public void offer(ScheCmd scheCmd)
    {
        try {
            boolean success = queue.offer(scheCmd, 5L, TimeUnit.SECONDS);
            if(!success){
                throw new RequestTimeoutException("任务请求超时，请重新尝试。");
            }
        } catch (InterruptedException e) {
            throw new BizException("请求意外中断，请重新尝试。");
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        queue = redisson.getBoundedBlockingQueue(SchedulerConst.SCHEDULE_QUEUE_NAME);
    }
}
