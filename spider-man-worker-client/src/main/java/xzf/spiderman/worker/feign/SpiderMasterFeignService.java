package xzf.spiderman.worker.feign;

import org.springframework.cloud.openfeign.FeignClient;
import xzf.spiderman.common.Ret;
import xzf.spiderman.worker.data.SubmitSpiderReq;

@FeignClient("spider-man-worker")
public interface SpiderMasterFeignService
{
    Ret<String> submitSpider(SubmitSpiderReq req);
}
