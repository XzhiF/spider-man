package xzf.spiderman.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xzf.spiderman.common.Ret;
import xzf.spiderman.scheduler.data.QryTaskReq;
import xzf.spiderman.scheduler.data.TaskData;
import xzf.spiderman.worker.data.AddSpiderCnfReq;
import xzf.spiderman.worker.data.QrySpiderCnfReq;
import xzf.spiderman.worker.data.SpiderCnfData;
import xzf.spiderman.worker.service.SpiderCnfService;

import javax.validation.Valid;

@RestController
public class SpiderCnfController
{
    @Autowired
    private SpiderCnfService spiderCnfService;

    @PostMapping("/worker/spider-cnf/add")
    public Ret<Void> add(@Valid @RequestBody AddSpiderCnfReq req)
    {
        spiderCnfService.add(req);
        return Ret.success();
    }

    @GetMapping("/worker/spider-cnf/list")
    public Ret<Page<SpiderCnfData>> listTask(QrySpiderCnfReq req, @PageableDefault Pageable pageable)
    {
        Page<SpiderCnfData> data = spiderCnfService.findAll(req, pageable);
        return Ret.success(data);
    }

}
