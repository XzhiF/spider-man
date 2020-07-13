package xzf.spiderman.worker.service;

import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.configuration.WorkerConst;
import xzf.spiderman.worker.data.SpiderTaskData;

import java.util.List;

public class SpiderWatcher
{
    private SpiderTaskStore store;
    private CuratorFramework curator;
    private String spiderId;
    private String groupId;

    private final String basePath;
    private final String watchingPath;
    private final CloseCallback closeCallback;

    private CuratorCache curatorCache;

    public interface CloseCallback
    {
        void call();
    }


    public SpiderWatcher(CuratorFramework curator, SpiderTaskStore store, String spiderId, String groupId, CloseCallback closeCallback) {
        this.store = store;
        this.curator = curator;
        this.spiderId = spiderId;
        this.groupId = groupId;
        this.closeCallback = closeCallback;

        this.basePath = WorkerConst.ZK_SPIDER_TASK_BASE_PATH;
        this.watchingPath = basePath+"/"+groupId+"/"+spiderId;
    }

    public void start()
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
        curatorCache.close();
        this.closeCallback.call();
    }

    public void createWatchingPath()
    {
        try
        {
            curator.create().withMode(CreateMode.PERSISTENT).forPath(watchingPath);
        }
        catch (Exception e)
        {
            throw new BizException("curator carete "+ watchingPath + " 失败。" + e.getMessage(), e);
        }
    }

    public class CuratorCacheListenerImpl implements CuratorCacheListener
    {
        @Override
        public void event(Type type, ChildData oldData, ChildData data) {

            switch (type){
                case NODE_CREATED:  // slave端来做的create操作
                    // 1. update -> init,  是不是running
                    SpiderTaskData task1 = JSON.parseObject(data.getData(),SpiderTaskData.class);
                    store.update(spiderId, task1);

                    break;

                case NODE_CHANGED:

                    // 1. update 是不是stop
                    SpiderTaskData task2 = JSON.parseObject(data.getData(),SpiderTaskData.class);
                    store.update(spiderId, task2);

                    // 2. 检查伙store,所有task是不是都是stopped了， 如果的话，dispatch-> slave-> stop
                    if(isAllCanStop(store.getTasks(spiderId))){
                        close();
                    }
                    break;

                case NODE_DELETED:
                    // TODO , delete证明宕机了
                    SpiderTaskData task3 = JSON.parseObject(data.getData(),SpiderTaskData.class);
                    store.remove(spiderId, task3);

                    break;
                default:break;
            }
        }
    }

    private boolean isAllCanStop(List<SpiderTaskData> tasks)
    {
        return ! (tasks.stream().filter(t->t.getStatus()<SpiderTaskData.CAN_STOP).findAny().isPresent());
    }

}
