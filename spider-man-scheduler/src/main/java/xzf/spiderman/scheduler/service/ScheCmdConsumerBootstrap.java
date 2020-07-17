package xzf.spiderman.scheduler.service;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ScheCmdConsumerBootstrap
{
    private final ThreadGroup threadGroup = new ThreadGroup("ScheCmdConsumer");
    private final AtomicInteger workerCount = new AtomicInteger(0);
    private final AtomicBoolean started = new AtomicBoolean(false);

    private ExecutorService executorService = null;

    private ScheCmdConsumerRunnableFactory runnableFactory;

    public ScheCmdConsumerBootstrap(ScheCmdConsumerRunnableFactory runnableFactory)
    {
        this.runnableFactory = runnableFactory;
    }

    public void start() // 支持多次start，前提是已经stop
    {
        if( ! started.compareAndSet(false, true) ){
            log.warn("ScheCmdConsumerBootstrap-ScheCmd的消费线程池已经是启动状态，不能再次调用start()");
            return ;
        }

        List<ScheCmdConsumerRunnable> commands = runnableFactory.create();
        executorService = createExecutorService(commands.size());


        for (ScheCmdConsumerRunnable command : commands) {
            executorService.execute(command);
        }
    }

    public void stop()
    {
        if( started.compareAndSet(true, false) ) {

            //因为里面使用redis blocking pop 阻塞线程, worker.tryLock会等于false, 为了能中断阻塞，要使用shutdownNow
            executorService.shutdownNow();

            try {
                boolean terminated = executorService.awaitTermination(5L, TimeUnit.SECONDS);
                if (!terminated) {
                    log.error("警告：ScheCmdConsumerBootstrap-ScheCmd关闭不成功。");
                }
            } catch (InterruptedException e) {
            }

            workerCount.set(0);
            executorService = null;
        }
    }

    private ExecutorService createExecutorService(int threads)
    {
        return Executors.newFixedThreadPool(threads, createConsumerThreadFactory());
    }

    private ThreadFactory createConsumerThreadFactory()
    {
        return r -> {
            int workerNo = workerCount.incrementAndGet();
            return new Thread(threadGroup, r, ScheCmdConsumerRunnable.class.getSimpleName()+"-" + workerNo);
        };
    }


}
