package xzf.spiderman.worker.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.event.Event;
import xzf.spiderman.common.event.EventListener;
import xzf.spiderman.worker.data.CloseSpiderReq;
import xzf.spiderman.worker.data.StartSpiderReq;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.entity.SpiderStore;
import xzf.spiderman.worker.repository.SpiderCnfRepository;
import xzf.spiderman.worker.repository.SpiderStoreRepository;
import xzf.spiderman.worker.service.event.CloseSpiderEvent;
import xzf.spiderman.worker.service.event.SpiderStatusChangedEvent;
import xzf.spiderman.worker.service.event.StartSpiderEvent;

import java.util.List;

@Service
@Slf4j
public class SpiderSlaveService implements EventListener
{

    @Autowired
    private SpiderCnfRepository spiderCnfRepository;

    @Autowired
    private SpiderStoreRepository spiderStoreRepository;

    @Autowired
    private EventPublisherRegistry eventPublisherRegistry;

    public void startSpider(StartSpiderReq req)
    {
        // 1. prepare data
        SpiderKey spiderKey = new SpiderKey(req.getSpiderId(), req.getGroupId(), req.getCnfId());
        SpiderCnf cnf = spiderCnfRepository.getOne(req.getCnfId());
        List<SpiderStore> stores = spiderStoreRepository.findAllByCnfId(req.getCnfId());

        // 2.
        eventPublisherRegistry.spiderSlaveEventPublisher()
                .publish(new StartSpiderEvent(spiderKey,cnf,stores));
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


    public void updateSpiderStatus(String id, int status)
    {
        spiderCnfRepository.updateStatus(id, status);
    }


    @Override
    public boolean supportEventType(Class<? extends Event> clazz)
    {
        return SpiderStatusChangedEvent.class.equals(clazz);
    }

    @Override
    @Transactional
    public void onEvent(Event event)
    {
        if(event instanceof SpiderStatusChangedEvent){
            SpiderStatusChangedEvent e = (SpiderStatusChangedEvent)event;
            updateSpiderStatus(e.getCnfId(), e.getStatus());
//            ApplicationContextAware   ->
//            AopContext.currentProxy() ->
        }
    }
}
