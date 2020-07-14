package xzf.spiderman.worker.service.event;

import lombok.*;
import xzf.spiderman.common.event.Event;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.service.SpiderKey;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class SpiderSubmittedEvent implements Event
{
    private final SpiderKey key;
    private final List<SpiderCnf> availableCnfs;
    private final List<SpiderCnf> allCnfs;


}
