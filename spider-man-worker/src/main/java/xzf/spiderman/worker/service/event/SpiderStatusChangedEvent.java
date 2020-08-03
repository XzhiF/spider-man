package xzf.spiderman.worker.service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xzf.spiderman.common.event.Event;
import xzf.spiderman.worker.entity.SpiderCnf;

@Getter
@AllArgsConstructor
public class SpiderStatusChangedEvent implements Event
{
    private final String cnfId;
    private int status;
}
