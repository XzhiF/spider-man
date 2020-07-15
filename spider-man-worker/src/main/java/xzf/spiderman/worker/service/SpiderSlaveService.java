package xzf.spiderman.worker.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xzf.spiderman.worker.data.CloseSpiderReq;
import xzf.spiderman.worker.data.StartSpiderReq;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.repository.SpiderCnfRepository;
import xzf.spiderman.worker.service.event.CloseSpiderEvent;
import xzf.spiderman.worker.service.event.StartSpiderEvent;

@Service
@Slf4j
public class SpiderSlaveService
{
    @Autowired
    private CuratorFramework curator;

    @Autowired
    private SpiderCnfRepository spiderCnfRepository;

    @Autowired
    private EventPublisherRegistry eventPublisherRegistry;

    public void startSpider(StartSpiderReq req)
    {
        // 1. prepare data
        SpiderKey spiderKey = new SpiderKey(req.getSpiderId(), req.getGroupId(), req.getCnfId());
        SpiderCnf cnf = spiderCnfRepository.getOne(req.getCnfId());

        // 2.
        eventPublisherRegistry.spiderSlaveEventPublisher()
                .publish(new StartSpiderEvent(spiderKey,cnf));
    }



    public void closeSpider(CloseSpiderReq req)
    {
        // 1. prepare data
        SpiderKey spiderKey = new SpiderKey(req.getSpiderId(), req.getGroupId(), req.getCnfId());
        SpiderCnf cnf = spiderCnfRepository.getOne(req.getCnfId());


        // 2.
        eventPublisherRegistry.spiderSlaveEventPublisher()
                .publish(new CloseSpiderEvent(spiderKey, cnf));
    }
}
