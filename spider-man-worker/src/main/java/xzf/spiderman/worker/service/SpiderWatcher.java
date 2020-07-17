package xzf.spiderman.worker.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import xzf.spiderman.starter.curator.CuratorFacade;
import xzf.spiderman.worker.configuration.WorkerConst;

import java.util.List;

@Slf4j
public class SpiderWatcher
{
    private final SpiderTaskRepository taskRepository;
    private final CuratorFacade curatorFacade;
    private final GroupSpiderKey key;

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

    public SpiderWatcher(CuratorFacade curatorFacade, SpiderTaskRepository taskRepository, GroupSpiderKey key, PreCloseCallback preCloseCallback, CloseCallback closeCallback) {
        this.taskRepository = taskRepository;
        this.curatorFacade = curatorFacade;
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
        curatorCache = CuratorCache.build(curatorFacade.getCurator(), watchingPath);
        curatorCache.listenable().addListener(new CuratorCacheListenerImpl());
        curatorCache.start();
    }

    public void close()
    {

        // 1. close掉 cache
        curatorCache.close();
        log.info("curatorCache closed.");

        // 2.清理zk节点
        deleteWatchingPath();

        if(closeCallback!=null){ closeCallback.call(); }

        log.info("SpiderWatcher closed.");
    }

    private void deleteWatchingPath()
    {
        curatorFacade.execute(curator -> {
            curator.delete().deletingChildrenIfNeeded().forPath(watchingPath);
            log.info("SpiderWatcher delete path : " + watchingPath);
        });
    }

    public void createWatchingPath()
    {
        curatorFacade.execute(curator -> {
            curator.create().withMode(CreateMode.PERSISTENT).forPath(watchingPath, new byte[0]);
            log.info("SpiderWatcher create path : " + watchingPath);
        });
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

        SpiderTask task = JSON.parseObject(data.getData(), SpiderTask.class);
        taskRepository.update(key, task);
    }

    private void onNodeChanged(ChildData data)
    {
        // 1. update 是不是stop
        SpiderTask task = JSON.parseObject(data.getData(), SpiderTask.class);
        taskRepository.update(key, task);

        // 2. 检查是否都达到close条件
        if(isAllStatusCanClose(taskRepository.getTasks(key))) {
            if(preCloseCallback != null){ preCloseCallback.call();}
        }

        // 3. 检查爬虫是否都关闭了
        if(isAllStatusClosed(taskRepository.getTasks(key))){
            log.info("onNodeChanged. It's all closed . ");
            close();
        }
    }

    private void onNodeDelete(ChildData data)
    {
        SpiderTask task = JSON.parseObject(data.getData(), SpiderTask.class);
        taskRepository.remove(key, task);
    }

    private boolean isAllStatusCanClose(List<SpiderTask> tasks)
    {
        return ! (tasks.stream().filter(t->t.getStatus() != SpiderTask.STATUS_CAN_CLOSE).findAny().isPresent());
    }

    private boolean isAllStatusClosed(List<SpiderTask> tasks)
    {
        return ! (tasks.stream().filter(t->t.getStatus() != SpiderTask.STATUS_CLOSED).findAny().isPresent());
    }


    public static Builder builder(CuratorFacade curatorFacade)
    {
        return new Builder(curatorFacade);
    }

    public static class Builder
    {
        private CuratorFacade curatorFacade;
        private SpiderTaskRepository store;
        private GroupSpiderKey key;
        private PreCloseCallback preCloseCallback;
        private CloseCallback closeCallback;

        public Builder(CuratorFacade curatorFacade)
        {
            this.curatorFacade = curatorFacade;
        }

        public Builder withStore(SpiderTaskRepository store)
        {
            this.store = store;
            return this;
        }


        public Builder withKey(GroupSpiderKey key)
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
            return new SpiderWatcher(curatorFacade,store,key,preCloseCallback,closeCallback);
        }

    }

}
