package xzf.spiderman.worker.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import xzf.spiderman.common.Ret;
import xzf.spiderman.worker.data.SubmitSpiderReq;

@FeignClient("spider-man-worker")
@RequestMapping("/worker/spider-master")
public interface SpiderMasterFeignService
{
    @PostMapping("/submit-spider")
    Ret<String> submitSpider(@RequestBody SubmitSpiderReq req);
}
