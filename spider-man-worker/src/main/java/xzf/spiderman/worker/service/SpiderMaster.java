package xzf.spiderman.worker.service;

import org.apache.curator.framework.CuratorFramework;
import xzf.spiderman.worker.data.SpiderTaskData;
import xzf.spiderman.worker.entity.SpiderCnf;

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
public class SpiderMaster
{
    private SpiderTaskStore store ;
    private CuratorFramework curator;

    public SpiderMaster(SpiderTaskStore store, CuratorFramework curator)
    {
        this.store = store;
        this.curator = curator;
    }

    public void submit(String spiderId, String groupId, List<SpiderCnf> cnfs)
    {
        // 1. 保存Task数据信息到store中
        store.put(spiderId, buildInitSpiderTaskRuntimeData(spiderId, groupId, cnfs)); //init

        // 2. 发送给slave，开始爬虫任务 ->  slave , zkCli -> create_path -> /worker/spider-task/{groupId}/{spiderId}/spider1-(data:ip,port, conf.  running)
        SpiderDispatcher dispatcher = new SpiderDispatcher(spiderId, groupId, cnfs);
        dispatcher.dispatchStart();

        // 3. zk中创建目录，并监控
        SpiderWatcher.CloseCallback closeCallback = () -> {
            dispatcher.dispatchStop();
        };
        SpiderWatcher watcher = new SpiderWatcher(curator, store, spiderId, groupId,closeCallback);
        watcher.start();
    }

    private Map<String,SpiderTaskData> buildInitSpiderTaskRuntimeData(String spiderId, String groupId, List<SpiderCnf> cnfs)
    {
        Map<String,SpiderTaskData> taskData = new HashMap<>();
        for (SpiderCnf cnf : cnfs) {
            SpiderTaskData data = new SpiderTaskData();
            data.setSpiderId(spiderId);
            data.setGroupId(groupId);
            data.setCnfId(cnf.getId());
            data.setStatus(SpiderTaskData.STATUS_INIT);
            taskData.put(cnf.getId(), data);
        }
        return taskData;
    }

    // 往zk里面写数据的一个 zkOps, watch ->


 /**
  *
  * 1. 根据groupId -> 查找到所有爬虫
  *
  * 2. nacos discovery -> spider-man-worker-> 服务器
  *
  * 3. filter -> server-host:port. available spider-server
  *
  * 4. log ->
  *
  * 5. startup
  *
  * 6. watch zk path.  stopAll
  *
  * startup ->
  *
 */

}
