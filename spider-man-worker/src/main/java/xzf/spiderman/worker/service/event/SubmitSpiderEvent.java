package xzf.spiderman.worker.service.event;

import lombok.*;
import xzf.spiderman.common.event.Event;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.service.GroupSpiderKey;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class SubmitSpiderEvent implements Event
{
    private final GroupSpiderKey key;
    private final List<SpiderCnf> availableCnfs;
    private final List<SpiderCnf> allCnfs;


}
