package xzf.spiderman.worker.service;

import org.springframework.stereotype.Repository;
import xzf.spiderman.worker.webmagic.WorkerSpider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
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
}
