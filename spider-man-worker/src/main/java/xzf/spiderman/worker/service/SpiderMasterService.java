package xzf.spiderman.worker.service;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.exception.BizException;
import static xzf.spiderman.worker.configuration.WorkerConst.*;
import xzf.spiderman.worker.data.SubmitSpiderReq;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.entity.SpiderGroup;
import xzf.spiderman.worker.repository.SpiderCnfRepository;
import xzf.spiderman.worker.repository.SpiderGroupRepository;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class SpiderMasterService
{
    @NacosInjected
    private NacosNamingService nacosNamingService;

    @Autowired
    private SpiderCnfRepository spiderCnfRepository;

    @Autowired
    private SpiderGroupRepository spiderGroupRepository;

    @Autowired
    private CuratorFramework curator;

    private SpiderMaster spiderMaster; // TODO


    // 1. init
    public void initSpiderWorkspace()
    {
        initSpiderTaskBasePath();

        List<SpiderGroup> groups = spiderGroupRepository.findAll();

        for (SpiderGroup group : groups)
        {
            initSpiderTaskPath4Group(group);
        }
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
        String path = ZK_SPIDER_TASK_BASE_PATH + "/" + group;
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
    @Transactional
    public String submitSpider(SubmitSpiderReq req)
    {
        // 1. 准备spiderTaskId
        String spiderId = newSpiderId();

        // 2. 准备所有spider cnf
        List<SpiderCnf> cnfs = spiderCnfRepository.findALlByGroupId(req.getGroupId());

        // 3. 过滤了Server启动状态中的爬虫cnf
        List<SpiderCnf> availableCnfs = getAvailableCnfServers(cnfs);

        // 4. 使用spiderMaster进行分发任务，并监听停止事件
        spiderMaster.submit( new SpiderKey(spiderId, req.getGroupId()), availableCnfs );

        //
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
            List<Instance> allInstances = nacosNamingService.getAllInstances("spider-man-worker");

            for (Instance instance : allInstances) {
                for (SpiderCnf cnf : cnfs) {
                    String host = cnf.getServer().getHost();
                    Integer port = cnf.getServer().getPort();
                    if(instance.getIp().equals(host) && instance.getPort()==port.intValue()){
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

    public static void main(String[] args)
    {
        System.out.println(Long.MAX_VALUE);
        System.out.println("9223372036854775807".length());
    }

}
