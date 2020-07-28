package xzf.spiderman.worker.service.master;

import xzf.spiderman.scheduler.data.JobTaskCallbackReq;
import xzf.spiderman.worker.service.GroupSpiderKey;
import xzf.spiderman.worker.webmagic.ApplicationServiceRegistry;

public class SpiderNotifier
{
    private ApplicationServiceRegistry registry;

    public SpiderNotifier(ApplicationServiceRegistry registry) {
        this.registry = registry;
    }


    public void notifyClosed(GroupSpiderKey key)
    {
        JobTaskCallbackReq req = new JobTaskCallbackReq();
        req.setGroupId(key.getGroupId());
        req.setSpiderId(key.getSpiderId());
        registry.jobTaskFeignService().callback(req);
    }

}
