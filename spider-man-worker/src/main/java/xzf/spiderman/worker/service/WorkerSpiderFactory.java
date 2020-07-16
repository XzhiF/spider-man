package xzf.spiderman.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.webmagic.BlockingPollRedisScheduler;
import xzf.spiderman.worker.webmagic.WorkerSpider;
import xzf.spiderman.worker.webmagic.WorkerSpiderLifeCycleListener;

@Repository
public class WorkerSpiderFactory
{
    @Autowired
    private BlockingPollRedisScheduler scheduler;

    private PageProcessorFactory processorFactory = new PageProcessorFactory();

    public WorkerSpider create(SpiderCnf cnf)
    {
        return null;
    }

    public WorkerSpider create(SpiderCnf cnf, WorkerSpiderLifeCycleListener listener)
    {
        return null;
    }
}
