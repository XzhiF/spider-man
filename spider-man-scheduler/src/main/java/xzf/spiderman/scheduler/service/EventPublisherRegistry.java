package xzf.spiderman.scheduler.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import xzf.spiderman.common.event.EventPublisher;
import xzf.spiderman.scheduler.service.event.TaskDisabledEvent;
import xzf.spiderman.scheduler.service.event.TaskEnabledEvent;

@Repository
public class EventPublisherRegistry
{
    @Autowired
    private ScheduleService scheduleService;

    public EventPublisher taskEventPublisher()
    {
        EventPublisher eventPublisher = new EventPublisher();

        eventPublisher.subscribe(TaskDisabledEvent.class, scheduleService);
        eventPublisher.subscribe(TaskEnabledEvent.class, scheduleService);

        return eventPublisher;
    }


}
