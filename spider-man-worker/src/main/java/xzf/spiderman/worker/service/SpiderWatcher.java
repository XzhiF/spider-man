package xzf.spiderman.worker.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.configuration.WorkerConst;
import xzf.spiderman.worker.data.SpiderTaskData;

import java.util.List;

@Slf4j
public class SpiderWatcher
{
    private final SpiderTaskStore store;
    private final CuratorFramework curator;
    private final SpiderKey key;

    private final String basePath;
    private final String watchingPath;

    private final PreCloseCallback preCloseCallback;
    private final CloseCallback closeCallback;

    private CuratorCache curatorCache;

    public interface PreCloseCallback
    {
        void call();
    }

    public interface CloseCallback
    {
        void call();
    }

    public SpiderWatcher(CuratorFramework curator, SpiderTaskStore store, SpiderKey key, PreCloseCallback preCloseCallback,CloseCallback closeCallback) {
        this.store = store;
        this.curator = curator;
        this.key = key;
        this.preCloseCallback = preCloseCallback;
        this.closeCallback = closeCallback;

        this.basePath = WorkerConst.ZK_SPIDER_TASK_BASE_PATH;
        this.watchingPath = basePath+"/"+key.getGroupId()+"/"+key.getSpiderId();
    }

    public void watchAutoClose()
    {
        // 1. create path
        createWatchingPath();

        // 2. create curator cache and start
        curatorCache = CuratorCache.build(curator, watchingPath);
        curatorCache.listenable().addListener(new CuratorCacheListenerImpl());
        curatorCache.start();
    }

    public void close()
    {
        // 1. close掉 cache
        curatorCache.close();

        // 2.清理zk节点
        deleteWatchingPath();

        log.info("SpiderWatcher closed.");
    }

    private void deleteWatchingPath()
    {
        try  {
            curator.delete().deletingChildrenIfNeeded().forPath(watchingPath);
            log.info("SpiderWatcher delete path : " + watchingPath);
        }
        catch (Exception e)  {
            throw new BizException("curator carete "+ watchingPath + " 失败。" + e.getMessage(), e);
        }
    }

    public void createWatchingPath()
    {
        try {
            curator.create().withMode(CreateMode.PERSISTENT).forPath(watchingPath, new byte[0]);
            log.info("SpiderWatcher create path : " + watchingPath);
        }
        catch (Exception e) {
            throw new BizException("curator create "+ watchingPath + " 失败。" + e.getMessage(), e);
        }
    }

    public class CuratorCacheListenerImpl implements CuratorCacheListener
    {
        @Override
        public void event(Type type, ChildData oldData, ChildData data) {

            switch (type){
                case NODE_CREATED:  // slave端来做的create操作
                    // 1. update -> init,  是不是running
                    onNodeCreated(data);
                    break;

                case NODE_CHANGED:
                    // 1. update
                    // 2. 判断canClose
                    // 3. 处理已经close
                    onNodeChanged(data);
                    break;

                case NODE_DELETED:
                    // 节点宕机
                    onNodeDelete(data);
                    break;
                default:break;
            }
        }
    }


    private void onNodeCreated(ChildData data)
    {

        if(data.getPath().equals(watchingPath)){
            System.out.println("on create");
            return;
        }

        SpiderTaskData task = JSON.parseObject(data.getData(),SpiderTaskData.class);
        store.update(key, task);
    }

    private void onNodeChanged(ChildData data)
    {
        // 1. update 是不是stop
        SpiderTaskData task = JSON.parseObject(data.getData(),SpiderTaskData.class);
        store.update(key, task);

        // 2. 检查是否都达到close条件
        if(isAllStatusCanClose(store.getTasks(key))) {
            if(preCloseCallback != null){ preCloseCallback.call();}
        }

        // 3. 检查爬虫是否都关闭了
        if(isAllStatusClosed(store.getTasks(key))){
            close();
            if(closeCallback!=null){ closeCallback.call(); }
        }
    }

    private void onNodeDelete(ChildData data)
    {
        SpiderTaskData task = JSON.parseObject(data.getData(),SpiderTaskData.class);
        store.remove(key, task);
    }

    private boolean isAllStatusCanClose(List<SpiderTaskData> tasks)
    {
        return ! (tasks.stream().filter(t->t.getStatus() != SpiderTaskData.STATUS_CAN_CLOSE).findAny().isPresent());
    }

    private boolean isAllStatusClosed(List<SpiderTaskData> tasks)
    {
        return ! (tasks.stream().filter(t->t.getStatus() != SpiderTaskData.STATUS_CLOSED).findAny().isPresent());
    }


    public static Builder builder(CuratorFramework curator)
    {
        return new Builder(curator);
    }

    public static class Builder
    {
        private CuratorFramework curator;
        private SpiderTaskStore store;
        private SpiderKey key;
        private PreCloseCallback preCloseCallback;
        private CloseCallback closeCallback;

        public Builder(CuratorFramework curator)
        {
            this.curator = curator;
        }

        public Builder withStore(SpiderTaskStore store)
        {
            this.store = store;
            return this;
        }


        public Builder withKey(SpiderKey key)
        {
            this.key = key;
            return this;
        }

        public Builder preCloseCallback(PreCloseCallback preCloseCallback)
        {
            this.preCloseCallback = preCloseCallback;
            return this;
        }

        public Builder closeCallback(CloseCallback closeCallback)
        {
            this.closeCallback = closeCallback;
            return this;
        }


        public SpiderWatcher build()
        {
            return new SpiderWatcher(curator,store,key,preCloseCallback,closeCallback);
        }

    }

}
