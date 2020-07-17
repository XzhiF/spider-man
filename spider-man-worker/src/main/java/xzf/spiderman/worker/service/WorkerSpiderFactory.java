package xzf.spiderman.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import xzf.spiderman.worker.configuration.WorkerProperties;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.webmagic.BlockingPollRedisScheduler;
import xzf.spiderman.worker.webmagic.SpiderParams;
import xzf.spiderman.worker.webmagic.WorkerSpider;
import xzf.spiderman.worker.webmagic.WorkerSpiderLifeCycleListener;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class WorkerSpiderFactory
{
    private final ThreadGroup threadGroup = new ThreadGroup("WorkerSpider");
    private final ProcessorFactory processorFactory = new ProcessorFactory();

    @Autowired
    private BlockingPollRedisScheduler scheduler;

    @Autowired
    private WorkerProperties properties;

    public WorkerSpider create(SpiderKey key, SpiderCnf cnf, WorkerSpiderLifeCycleListener listener)
    {
        SpiderParams params = new SpiderParams();

        PageProcessor pageProcessor = processorFactory.create(cnf);

        WorkerSpider spider = WorkerSpider.create(pageProcessor);

        // downloader TODO 默认http, cnf—> type相关。
        // httpclient, selenium, jsoup.

        // Pipelines TODO 跟Store相关
        spider.addPipeline(new ConsolePipeline());

        // 不需要设置request或url，由master端设置

        spider.setScheduler(scheduler); //设置任务队列
        spider.setPollTimeoutSeconds(getPollTimeoutSeconds(cnf));
        spider.setMaxPollTimeoutCount(getMaxPollTimeoutCount(cnf));  //设置可关闭爬虫条件
        spider.thread(newWorkerThreadPool(key,cnf), getWorkerThreads(cnf)); //设置爬虫工作线程池

        if(cnf.isSharedMode()) {
            spider.setUUID(key.getSpiderId());      // redis queue (scheduler) key.
        }else{
            spider.setUUID(key.getSpiderId() + "_" + cnf.getId());
        }

        spider.setLifeCycleListener(listener); //设置监听器

        return spider;
    }


    private ExecutorService newWorkerThreadPool(SpiderKey key, SpiderCnf cnf)
    {
        ThreadFactory factory = new ThreadFactory() {
            AtomicInteger workNo = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r)
            {
                String name = threadGroup.getName()+"-"+ key.getGroupId()+"-" + workNo;
                return new Thread(threadGroup, r, name);
            }
        };

        int workerThreads = getWorkerThreads(cnf);

        return new ThreadPoolExecutor(
                workerThreads,
                workerThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(workerThreads),
                factory);
    }

    private int getPollTimeoutSeconds(SpiderCnf cnf)
    {
        if(cnf.getPollTimeoutSeconds() != null){
            return cnf.getPollTimeoutSeconds().intValue();
        }
        return properties.getWorkerSpiderCnf().getDefaultPollTimeoutSeconds();
    }

    private int getMaxPollTimeoutCount(SpiderCnf cnf)
    {
        if(cnf.getMaxPollTimeoutCount() != null){
            return cnf.getMaxPollTimeoutCount().intValue();
        }
        return properties.getWorkerSpiderCnf().getDefaultMaxPollTimeoutCount();
    }


    private int getWorkerThreads(SpiderCnf cnf)
    {
        Integer ret = cnf.getWorkerThreads();
        if(ret != null){
            return ret.intValue();
        }

        //   cpu核数 * cpu使 用率 + (等时间/执行时间)
//        return Runtime.getRuntime().availableProcessors();

        return properties.getWorkerSpiderCnf().getDefaultWorkerThreads();
    }

}
