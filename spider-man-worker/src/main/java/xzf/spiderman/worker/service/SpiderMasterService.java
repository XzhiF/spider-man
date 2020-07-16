package xzf.spiderman.worker.service;

import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.data.SubmitSpiderReq;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.entity.SpiderGroup;
import xzf.spiderman.worker.repository.SpiderCnfRepository;
import xzf.spiderman.worker.repository.SpiderGroupRepository;
import xzf.spiderman.worker.service.event.SubmitSpiderEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static xzf.spiderman.worker.configuration.WorkerConst.ZK_SPIDER_TASK_BASE_PATH;

@Service
@Slf4j
public class SpiderMasterService
{
    @Autowired
    private NacosServiceDiscovery nacosServiceDiscovery;

    @Autowired
    private SpiderCnfRepository spiderCnfRepository;

    @Autowired
    private SpiderGroupRepository spiderGroupRepository;

    @Autowired
    private CuratorFramework curator;

    @Autowired
    private SpiderTaskRepository spiderTaskRepository;

    @Autowired
    private EventPublisherRegistry eventPublisherRegistry;


    // 1. init
    public void initSpiderWorkspace()
    {
        // 1. init redis cache
        spiderTaskRepository.sync();

        // 2. init zk paths
        initSpiderTaskBasePath();
        List<SpiderGroup> groups = spiderGroupRepository.findAll();
        for (SpiderGroup group : groups)
        {
            initSpiderTaskPath4Group(group);
        }

        //
    }

    private void initSpiderTaskBasePath()
    {
        try{
            if(curator.checkExists().forPath(ZK_SPIDER_TASK_BASE_PATH) == null) {
                curator.create().creatingParentContainersIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath(ZK_SPIDER_TASK_BASE_PATH);
            }
        }catch (Exception e){
            log.warn("curator create " + ZK_SPIDER_TASK_BASE_PATH + "失败。" + e.getMessage());
        }
    }

    private void initSpiderTaskPath4Group(SpiderGroup group)
    {
        String path = ZK_SPIDER_TASK_BASE_PATH + "/" + group.getId();
        try
        {
            if (curator.checkExists().forPath(path) == null)
            {
                curator.create()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(path);
            }
        }catch (Exception e) {
            log.warn("curator create " + path + "失败。" + e.getMessage());
        }
    }

    //
    public String submitSpider(SubmitSpiderReq req)
    {
        // 1. 准备spiderTaskId
        String spiderId = newSpiderId();

        // 2. 准备所有spider cnf
        List<SpiderCnf> cnfs = spiderCnfRepository.findALlByGroupId(req.getGroupId());

        // 3. 过滤了Server启动状态中的爬虫cnf
        List<SpiderCnf> availableCnfs = getAvailableCnfServers(cnfs);

        if(availableCnfs.isEmpty()){
            throw new BizException("没有可运行的爬虫服务器.group " + req.getGroupId());
        }


        // 4. 发布事件
        SubmitSpiderEvent submitSpiderEvent = SubmitSpiderEvent.builder()
                .key(new GroupSpiderKey(spiderId, req.getGroupId()))
                .allCnfs(cnfs)
                .availableCnfs(availableCnfs)
                .build();

        eventPublisherRegistry.spiderMasterEventPublisher()
                .publish(submitSpiderEvent);

        return spiderId;
    }


    private String newSpiderId()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String datetime = sdf.format(new Date());

        Random r = new Random();
        datetime += r.nextInt(9);
        datetime += r.nextInt(9);

        return datetime;
    }

    private List<SpiderCnf> getAvailableCnfServers(List<SpiderCnf> cnfs)
    {
        try
        {
            List<SpiderCnf> results = new ArrayList<>();
            List<ServiceInstance> allInstances = nacosServiceDiscovery.getInstances("spider-man-worker");

            for (ServiceInstance instance : allInstances) {
                for (SpiderCnf cnf : cnfs) {
                    String host = cnf.getServer().getHost();
                    Integer port = cnf.getServer().getPort();

                    if(host.equals("localhost"))
                    {
                        try {
                            host = InetAddress.getLocalHost().getHostAddress();
                        } catch (UnknownHostException e) {}
                    }


                    if(instance.getHost().equals(host) && instance.getPort()==port.intValue()){
                        results.add(cnf);
                        break;
                    }
                }
            }
            return results;
        } catch (NacosException e) {
            throw new BizException("解析Nacos可用实例失败");
        }
    }

}
