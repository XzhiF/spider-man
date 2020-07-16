package xzf.spiderman.worker.webmagic;

import us.codecraft.webmagic.Request;

public interface WorkerSpiderLifeCycleListener
{
    default void onBeforeStart(WorkerSpider spider) {}

    // 这里代表程序已经真正的可以退出了
    default void onAfterRun(WorkerSpider spider) {}

    default void onRequestPolled(WorkerSpider spider, Request request){}
    //
    default void onCanCloseCondition(WorkerSpider spider) {};

    default void onBeforeClose(WorkerSpider spider){};

    default void onAfterClose(WorkerSpider spider){}


    WorkerSpiderLifeCycleListener DEFAULT = new WorkerSpiderLifeCycleListener() {};

}
