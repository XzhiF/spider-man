package xzf.spiderman.worker.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;
import xzf.spiderman.common.event.EventPublisher;
import xzf.spiderman.worker.service.event.CloseSpiderEvent;
import xzf.spiderman.worker.service.event.SpiderStatusChangedEvent;
import xzf.spiderman.worker.service.event.StartSpiderEvent;
import xzf.spiderman.worker.service.event.SubmitSpiderEvent;
import xzf.spiderman.worker.service.master.SpiderMaster;
import xzf.spiderman.worker.service.slave.SpiderSlave;

@Repository
public class EventPublisherRegistry implements ApplicationContextAware
{
    private ApplicationContext ctx;

    public EventPublisher spiderMasterEventPublisher()
    {
        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.subscribe(SubmitSpiderEvent.class, ctx.getBean(SpiderMaster.class));

        return eventPublisher;
    }

    public EventPublisher spiderSlaveEventPublisher()
    {
        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.subscribe(StartSpiderEvent.class, ctx.getBean(SpiderSlave.class));
        eventPublisher.subscribe(CloseSpiderEvent.class, ctx.getBean(SpiderSlave.class));

        return eventPublisher;
    }

    public EventPublisher spiderStatusEventPublisher()
    {
        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.subscribe(SpiderStatusChangedEvent.class, ctx.getBean(SpiderSlaveService.class));
        return eventPublisher;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }
}
