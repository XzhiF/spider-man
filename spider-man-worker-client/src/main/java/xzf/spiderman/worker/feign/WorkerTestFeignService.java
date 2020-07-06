package xzf.spiderman.worker.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("spider-man-worker")
public interface WorkerTestFeignService
{
    @GetMapping("/worker/test")
    Map<String,Object> test(@RequestParam("msg") String msg);
}
