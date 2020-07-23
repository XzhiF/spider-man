package xzf.spiderman.worker.service.slave;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import xzf.spiderman.common.event.Event;
import xzf.spiderman.common.event.EventListener;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.starter.curator.CuratorFacade;
import xzf.spiderman.worker.configuration.WorkerProperties;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.entity.SpiderStore;
import xzf.spiderman.worker.service.SpiderKey;
import xzf.spiderman.worker.service.SpiderTask;
import xzf.spiderman.worker.service.event.CloseSpiderEvent;
import xzf.spiderman.worker.service.event.StartSpiderEvent;
import xzf.spiderman.worker.webmagic.WorkerSpider;
import xzf.spiderman.worker.webmagic.WorkerSpiderLifeCycleListener;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static xzf.spiderman.worker.configuration.WorkerConst.ZK_SPIDER_TASK_BASE_PATH;

/**
 * 1.应该zk上创建临时节点，把本爬虫信息设置到data
 *
 * 2.维护一条线程，管理我们的WorkerSpider，他的start/stop方法，由Boss来通过http调用
 *
 * 3.通过WorkerSpider的listener坚挺 canClose事件
 *   当 canClose为真的时候。 更新zk节点，设置为completed
 *
 */
@Slf4j
public class SpiderSlave implements EventListener, ApplicationListener<ContextClosedEvent>
{
    private final CuratorFacade curatorFacade;
    private final WorkerSpiderFactory factory;
    private final WorkerSpiderRepository repository;
    private final ExecutorService executor;

    public SpiderSlave(
            CuratorFacade curatorFacade,
            WorkerSpiderFactory factory,
            WorkerSpiderRepository repository,
            WorkerProperties properties)
    {
        this.curatorFacade = curatorFacade;
        this.factory = factory;
        this.repository = repository;
        this.executor = newExecutor(properties);
    }

    public class StartSpiderHandler
    {
        private final SpiderKey key;
        private final SpiderCnf cnf;
        private final List<SpiderStore> stores;

        public StartSpiderHandler(SpiderKey key, SpiderCnf cnf,List<SpiderStore> stores) {
            this.key = key;
            this.cnf = cnf;
            this.stores = stores;
        }

        public void handle()
        {
            // 1. Build WorkerSpider listener
            WorkerSpiderLifeCycleListener listener = workerSpiderLifeCycleListener();

            // 2. Build WorkerSpider
            WorkerSpider spider = factory.create(new WorkerSpiderSettings(key,cnf,stores), listener);

            // 3. Run WorkerSpider
            CompletableFuture.runAsync( ()->spider.run(), executor )
                    .thenRunAsync( ()->updateClosedTaskToZk(key)  );

            // 保存到本地缓存中
            repository.put(key, spider);
        }

        private WorkerSpiderLifeCycleListener workerSpiderLifeCycleListener()
        {
            return new WorkerSpiderLifeCycleListener(){

                // 1. 通知zk，spider开始进行run方法。 这时候spider running
                @Override
                public void onBeforeStart(WorkerSpider spider) {
                    updateRunningTaskToZk(key);
                }

                // 2. 通知zk, spider已经达到了退出的条件，这时候spider canClose
                @Override
                public void onCanCloseCondition(WorkerSpider spider) {
                    updateCanCloseTaskToZk(key);
                }
            };
        }
    }

    public class CloseSpiderHandler
    {
        private final SpiderKey key;
        private final SpiderCnf cnf;

        public CloseSpiderHandler(SpiderKey key, SpiderCnf cnf) {
            this.key = key;
            this.cnf = cnf;
        }

        public void handle()
        {
            WorkerSpider spider = repository.remove(key);
            if(spider != null) {
                spider.close();
            }
            log.info(key+" closed.");
        }
    }


    private void updateRunningTaskToZk(SpiderKey key)
    {
        String path = taskPath(key);
        byte[] data = JSON.toJSONBytes(SpiderTask.newRunningTask(key));
        curatorFacade.execute(curator -> {
            curator.create().withMode(CreateMode.EPHEMERAL).forPath(path, data);
            log.info("Slave: task created. path="+path);
        });
    }

    private void updateCanCloseTaskToZk(SpiderKey key)
    {
        String path = taskPath(key);
        byte[] closedData = JSON.toJSONBytes(SpiderTask.newCanCloseTask(key));
        curatorFacade.execute(curator -> {
            log.info("Slave: before task can close. path="+path);
            curator.setData().forPath(path, closedData);
            log.info("Slave: task can close. path="+path);
        });
    }

    private void updateClosedTaskToZk(SpiderKey key)
    {
        String path = taskPath(key);
        byte[] data = JSON.toJSONBytes(SpiderTask.newClosedTask(key));

        curatorFacade.execute(curator -> {
            curator.setData().forPath(path, data);
            log.info("Slave: task closed. path="+path);
        });

    }

    private String taskPath(SpiderKey key)
    {
        String path = ZK_SPIDER_TASK_BASE_PATH + "/" + key.getGroupId()+"/"+key.getSpiderId()+"/"+key.getCnfId();
        return path;
    }

    @Override
    public boolean supportEventType(Class<? extends Event> clazz)
    {
        return StartSpiderEvent.class.equals(clazz) || CloseSpiderEvent.class.equals(clazz);
    }

    @Override
    public void onEvent(Event event)
    {
        if(event instanceof StartSpiderEvent){
            onStartSpiderEvent( (StartSpiderEvent) event );
        }

        if(event instanceof CloseSpiderEvent){
            onCloseSpiderEvent( (CloseSpiderEvent) event );
        }
    }

    private void onCloseSpiderEvent(CloseSpiderEvent event)
    {
        CloseSpiderHandler h = new CloseSpiderHandler(event.getKey(), event.getCnf());
        h.handle();
    }

    private void onStartSpiderEvent(StartSpiderEvent event)
    {
        StartSpiderHandler h = new StartSpiderHandler(event.getKey(), event.getCnf(), event.getStores());
        h.handle();
    }


    private ExecutorService newExecutor(WorkerProperties props)
    {
        WorkerProperties.SpiderSlavePool cfg = props.getSpiderSlavePool();

        ThreadGroup threadGroup = new ThreadGroup("SpiderSlave");

        ThreadFactory factory = new ThreadFactory() {
            AtomicInteger workerNo = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r)
            {
                String name = threadGroup.getName()+"-WorkSpiderPool-"+workerNo.incrementAndGet();
                Thread t = new Thread(threadGroup, r, name );
                return t;
            }
        };

        RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler(){
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                throw new BizException("SpiderSalve的WorkerSpider线程池已满["+cfg.getPoolThreads()+"]，请稍后重试");
            }
        };

        return  new ThreadPoolExecutor(
                cfg.getCoreThreads(),
                cfg.getPoolThreads(),
                cfg.getKeepAliveTimeSeconds(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(cfg.getPoolThreads()),
                factory,
                rejectedExecutionHandler);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event)
    {
        executor.shutdown();

        for (WorkerSpider workerSpider : repository.all()) {
            workerSpider.close();
        }

        try {
            boolean isTerminated = executor.awaitTermination(10, TimeUnit.SECONDS);
            if(!isTerminated) {
                log.warn("尝试停止所有线程失败。 尝试调用立刻停止");
                executor.shutdownNow();
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
