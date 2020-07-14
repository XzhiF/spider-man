package xzf.spiderman.worker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xzf.spiderman.common.Ret;
import xzf.spiderman.worker.data.CloseSpiderReq;
import xzf.spiderman.worker.data.StartSpiderReq;
import xzf.spiderman.worker.service.SpiderSlaveService;

@RestController
@RequestMapping("/worker/spider-slave")
@Slf4j
public class SpiderSlaveController
{

    @Autowired
    private SpiderSlaveService spiderSlaveService;

    @PostMapping("/start-spider")
    public Ret<Void> startSpider(@RequestBody StartSpiderReq req)
    {
        log.info("startSpider: req="+req);
        spiderSlaveService.startSpider(req);
        return Ret.success();
    }


    @PostMapping("/close-spider")
    public Ret<Void> closeSpider(@RequestBody CloseSpiderReq req)
    {
        log.info("CloseSpiderReq: req="+req);
        spiderSlaveService.closeSpider(req);
        return Ret.success();
    }



}
