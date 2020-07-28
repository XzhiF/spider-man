package xzf.spiderman.worker.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xzf.spiderman.common.Ret;
import xzf.spiderman.worker.data.SubmitSpiderReq;

@FeignClient("spider-man-worker")
public interface SpiderMasterFeignService
{
    @PostMapping("/submit-spider")
    Ret<String> submitSpider(@RequestBody SubmitSpiderReq req);
}
