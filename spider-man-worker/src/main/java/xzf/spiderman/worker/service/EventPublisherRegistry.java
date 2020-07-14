package xzf.spiderman.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import xzf.spiderman.common.event.EventPublisher;
import xzf.spiderman.worker.service.event.SpiderSubmittedEvent;

@Repository
public class EventPublisherRegistry
{
    @Autowired
    private SpiderMaster spiderMaster;


    public EventPublisher spiderMasterEventPublisher()
    {
        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.subscribe(SpiderSubmittedEvent.class, spiderMaster);

        return eventPublisher;
    }
}
