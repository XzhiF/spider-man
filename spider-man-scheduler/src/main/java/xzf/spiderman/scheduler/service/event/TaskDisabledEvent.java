package xzf.spiderman.scheduler.service.event;

import lombok.Data;
import xzf.spiderman.common.event.Event;
import xzf.spiderman.scheduler.entity.Task;

@Data
public class TaskDisabledEvent implements Event
{
    private Task task;

    public TaskDisabledEvent(Task task) {
        this.task = task;
    }

    public TaskDisabledEvent() {
    }
}
