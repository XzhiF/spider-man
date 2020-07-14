package xzf.spiderman.worker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import xzf.spiderman.common.Ret;
import xzf.spiderman.worker.data.CloseSpiderReq;
import xzf.spiderman.worker.data.StartSpiderReq;

@RestController
@RequestMapping("/worker/spider-slave")
@Slf4j
public class SpiderSlaveController
{

    @PostMapping("/start-spider")
    public Ret<Void> startSpider(@RequestBody StartSpiderReq req)
    {
        log.info("startSpider: req="+req);
        return Ret.success();
    }


    @PostMapping("/close-spider")
    public Ret<Void> closeSpider(@RequestBody CloseSpiderReq req)
    {
        log.info("CloseSpiderReq: req="+req);
        return Ret.success();
    }



}
