package xzf.spiderman.worker.webmagic;

import us.codecraft.webmagic.Request;

public interface WorkerSpiderLifeCycleListener
{
    default void onBeforeStart(WorkerSpider spider) {}

    default void onRequestPolled(WorkerSpider spider, Request request){}
    //
    default void onCanCloseCondition(WorkerSpider spider) {};

    default void onBeforeClose(WorkerSpider spider){};

    default void onAfterClose(WorkerSpider spider){}


    WorkerSpiderLifeCycleListener DEFAULT = new WorkerSpiderLifeCycleListener() {};

}
