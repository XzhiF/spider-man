package xzf.spiderman.worker.webmagic;

import us.codecraft.webmagic.Request;

public interface WorkerSpiderLifeCycleListener
{
    default void onStart(WorkerSpider spider) {}

    default void onRequestPolled(WorkerSpider spider, Request request){}
    //
    default void onCanCloseCondition(WorkerSpider spider) {}

    default void onBeforeClose(WorkerSpider spider){}

    default void onAfterClose(WorkerSpider spider){}

    default void onStop(WorkerSpider spider) {}


    WorkerSpiderLifeCycleListener DEFAULT = new WorkerSpiderLifeCycleListener() {};

}
