package xzf.spiderman.worker.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.data.CloseSpiderReq;
import xzf.spiderman.worker.data.SpiderTaskData;
import xzf.spiderman.worker.data.StartSpiderReq;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.repository.SpiderCnfRepository;

import java.util.concurrent.TimeUnit;

import static xzf.spiderman.worker.configuration.WorkerConst.ZK_SPIDER_TASK_BASE_PATH;

@Service
@Slf4j
public class SpiderSlaveService
{
    @Autowired
    private CuratorFramework curator;
    @Autowired
    private SpiderCnfRepository spiderCnfRepository;

    public void startSpider(StartSpiderReq req)
    {
//        SpiderCnf cnf = spiderCnfRepository.getOne(req.getCnfId());

        String spiderId = req.getSpiderId();
        String cnfId = req.getCnfId();
        String groupId = req.getGroupId();

        SpiderTaskData task = new SpiderTaskData();
        task.setCnfId(cnfId);
        task.setGroupId(groupId);
        task.setSpiderId(spiderId);
        task.setStatus(SpiderTaskData.STATUS_RUNNING);

        String path = ZK_SPIDER_TASK_BASE_PATH + "/" + groupId+"/"+spiderId+"/"+cnfId;

        try {

            //
            curator.create().withMode(CreateMode.EPHEMERAL).forPath(path, JSON.toJSONBytes(task));
            log.info("Slave running. Path= " + path);

            TimeUnit.SECONDS.sleep(30L);

            task.setStatus(SpiderTaskData.STATUS_CAN_CLOSE);
            curator.setData().forPath(path, JSON.toJSONBytes(task));
            log.info("Slave canClose. Path= " + path);

        }catch (Exception e){
            throw new BizException(e.getMessage(),e );
        }

    }

    public void closeSpider(CloseSpiderReq req)
    {
//        SpiderCnf cnf = spiderCnfRepository.getOne(req.getCnfId());

        String spiderId = req.getSpiderId();
        String cnfId = req.getCnfId();
        String groupId = req.getGroupId();

        SpiderTaskData task = new SpiderTaskData();
        task.setCnfId(cnfId);
        task.setGroupId(groupId);
        task.setSpiderId(spiderId);
        task.setStatus(SpiderTaskData.STATUS_CLOSED);

        String path = ZK_SPIDER_TASK_BASE_PATH + "/" + groupId+"/"+spiderId+"/"+cnfId;

        try {
            //
            curator.setData().forPath(path, JSON.toJSONBytes(task));
            log.info("Slave closed. Path= " + path);

        }catch (Exception e){
            throw new BizException(e.getMessage(),e );
        }

    }
}
