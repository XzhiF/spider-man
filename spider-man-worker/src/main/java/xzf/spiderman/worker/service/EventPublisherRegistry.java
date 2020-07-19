package xzf.spiderman.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import xzf.spiderman.common.event.EventPublisher;
import xzf.spiderman.worker.service.event.CloseSpiderEvent;
import xzf.spiderman.worker.service.event.StartSpiderEvent;
import xzf.spiderman.worker.service.event.SubmitSpiderEvent;
import xzf.spiderman.worker.service.master.SpiderMaster;
import xzf.spiderman.worker.service.slave.SpiderSlave;

@Repository
public class EventPublisherRegistry
{
    @Autowired
    private SpiderMaster spiderMaster;

    @Autowired
    private SpiderSlave spiderSlave;

    public EventPublisher spiderMasterEventPublisher()
    {
        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.subscribe(SubmitSpiderEvent.class, spiderMaster);

        return eventPublisher;
    }



    public EventPublisher spiderSlaveEventPublisher()
    {
        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.subscribe(StartSpiderEvent.class, spiderSlave);
        eventPublisher.subscribe(CloseSpiderEvent.class, spiderSlave);

        return eventPublisher;
    }
}
