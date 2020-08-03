package xzf.spiderman.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import xzf.spiderman.scheduler.feign.JobTaskFeignService;

@Repository
public class ApplicationServiceRegistry
{
    @Autowired
    private JobTaskFeignService jobTaskFeignService;


    public JobTaskFeignService jobTaskFeignService()
    {
        return jobTaskFeignService;
    }


}
