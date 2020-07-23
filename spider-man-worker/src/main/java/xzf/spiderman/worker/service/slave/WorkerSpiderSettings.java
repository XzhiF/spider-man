package xzf.spiderman.worker.service.slave;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.entity.SpiderStore;
import xzf.spiderman.worker.service.SpiderKey;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkerSpiderSettings
{
    private final SpiderKey key;
    private final SpiderCnf cnf;
    private final List<SpiderStore> stores;
}
