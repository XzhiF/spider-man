package xzf.spiderman.worker.service;

import org.apache.curator.framework.CuratorFramework;
import xzf.spiderman.common.event.Event;
import xzf.spiderman.common.event.EventListener;
import xzf.spiderman.worker.data.SpiderTaskData;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.service.event.SpiderSubmittedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1. 选举出boss
 *
 * 2. 接受到调度任务，从数据中load到spider配置，获取爬虫集群信息
 *
 * 3. 创建任务 start, stop。 发布给每一台爬虫 http
 *
 * 4. 创建zk的工作节点， uuid。 并监控长的变化
 *
 * 5. 假如监听到zk工作目录的所有worker completed = true ，遍历发布stop消息给爬虫(http)
 *
 */
public class SpiderMaster implements EventListener
{
    private SpiderTaskStore store ;
    private CuratorFramework curator;

    public SpiderMaster(SpiderTaskStore store, CuratorFramework curator)
    {
        this.store = store;
        this.curator = curator;
    }

    public void submit(SpiderKey key, List<SpiderCnf> cnfs)
    {
        // 1. 保存Task数据信息到store中
        store.put(key, buildInitSpiderTaskRuntimeData(key, cnfs)); //init

        // 2.创建dispatcher
        SpiderDispatcher dispatcher = new SpiderDispatcher(key, cnfs);

        // 3. zk中创建目录，并监控
        SpiderWatcher.PreCloseCallback preCloseCallback = () -> {
            dispatcher.dispatchClose();
        };
        SpiderWatcher.CloseCallback closeCallback = ()->{
            store.remove(key);
        };
        SpiderWatcher watcher = SpiderWatcher.builder(curator)
                .withStore(store)
                .withKey(key)
                .preCloseCallback(preCloseCallback)
                .closeCallback(closeCallback)
                .build();

        watcher.watchAutoClose();


        // 4. 发送给slave，开始爬虫任务 ->  slave , zkCli -> create_path -> /worker/spider-task/{groupId}/{spiderId}/spider1-(data:ip,port, conf.  running)
        dispatcher.dispatchStart();
    }

    private Map<String,SpiderTaskData> buildInitSpiderTaskRuntimeData(SpiderKey key, List<SpiderCnf> cnfs)
    {
        Map<String,SpiderTaskData> taskData = new HashMap<>();
        for (SpiderCnf cnf : cnfs) {
            SpiderTaskData data = new SpiderTaskData();
            data.setSpiderId(key.getSpiderId());
            data.setGroupId(key.getGroupId());
            data.setCnfId(cnf.getId());
            data.setStatus(SpiderTaskData.STATUS_INIT);
            taskData.put(cnf.getId(), data);
        }
        return taskData;
    }


    //
    @Override
    public boolean supportEventType(Class<? extends Event> clazz) {
        return SpiderSubmittedEvent.class.equals(clazz);
    }

    @Override
    public void onEvent(Event event)
    {
        SpiderSubmittedEvent e = (SpiderSubmittedEvent) event;
        this.submit(e.getKey(), e.getAvailableCnfs());
    }
}
