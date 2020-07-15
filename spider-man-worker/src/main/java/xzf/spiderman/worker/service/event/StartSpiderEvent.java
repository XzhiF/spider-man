package xzf.spiderman.worker.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xzf.spiderman.common.event.Event;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.service.SpiderKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartSpiderEvent implements Event
{
    private SpiderKey key;
    private SpiderCnf cnf;
}
