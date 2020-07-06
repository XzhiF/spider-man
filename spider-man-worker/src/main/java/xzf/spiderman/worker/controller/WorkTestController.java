package xzf.spiderman.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xzf.spiderman.worker.feign.WorkerTestFeignService;
import xzf.spiderman.worker.repository.SpiderGroupRepository;

import java.util.Collections;
import java.util.Map;

@RestController
public class WorkTestController implements WorkerTestFeignService
{

    @Autowired
    private SpiderGroupRepository spiderGroupRepository;

    @GetMapping("/worker/test")
    @Override
    public Map<String, Object> test(@RequestParam("msg") String msg)
    {
        return Collections.singletonMap("msg", "hello " + spiderGroupRepository.findAll());
    }

}
