package xzf.spiderman.worker.service;

/**
 * 1.应该zk上创建临时节点，把本爬虫信息设置到data
 *
 * 2.维护一条线程，管理我们的WorkerSpider，他的start/stop方法，由Boss来通过http调用
 *
 * 3.通过WorkerSpider的listener坚挺 canClose事件
 *   当 canClose为真的时候。 更新zk节点，设置为completed
 *
 */
public class Worker
{


    // 操作zk方法
    public void sendCompleted()
    {

    }


    public void start()
    {

    }

    public void stop()
    {

    }

}
