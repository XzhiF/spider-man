package xzf.spiderman.scheduler.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xzf.spiderman.common.Ret;
import xzf.spiderman.scheduler.data.JobTaskCallbackReq;
import xzf.spiderman.scheduler.feign.JobTaskFeignService;
import xzf.spiderman.scheduler.service.JobTaskService;

@RestController
public class JobTaskController implements JobTaskFeignService
{
    @Autowired
    private JobTaskService jobTaskService;

    @PostMapping("/scheduler/job-task/callback")
    public Ret<Void> callback(JobTaskCallbackReq req) throws Exception
    {
        jobTaskService.acceptCallback(req);
        return Ret.success();
    }

}
