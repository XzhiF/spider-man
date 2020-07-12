package xzf.spiderman.worker.feign;

import org.springframework.cloud.openfeign.FeignClient;
import xzf.spiderman.common.Ret;
import xzf.spiderman.worker.data.StartTaskReq;

@FeignClient("spider-man-worker")
public interface SpiderMasterFeignService
{
    Ret<String> startTask(StartTaskReq req);
}
