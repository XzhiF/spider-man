package xzf.spiderman.worker.service.slave;

import xzf.spiderman.worker.service.SpiderKey;
import xzf.spiderman.worker.webmagic.WorkerSpider;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkerSpiderRepository
{
    private Map<SpiderKey, WorkerSpider> data = new ConcurrentHashMap<>();

    public void put(SpiderKey key, WorkerSpider value)
    {
        data.put(key, value);
    }

    public WorkerSpider remove(SpiderKey key)
    {
        return data.remove(key);
    }

    public Collection<WorkerSpider> all()
    {
        return data.values();
    }
}
