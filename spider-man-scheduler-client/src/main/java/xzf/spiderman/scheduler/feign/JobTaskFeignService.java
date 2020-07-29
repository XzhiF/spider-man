package xzf.spiderman.scheduler.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xzf.spiderman.common.Ret;
import xzf.spiderman.scheduler.data.JobTaskCallbackReq;

@FeignClient("spider-man-scheduler")
public interface JobTaskFeignService
{
    @PostMapping("/scheduler/job-task/callback")
    Ret<Void> callback(@RequestBody JobTaskCallbackReq req);
}
