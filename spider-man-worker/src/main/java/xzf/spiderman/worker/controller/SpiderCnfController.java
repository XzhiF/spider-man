package xzf.spiderman.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import xzf.spiderman.common.Ret;
import xzf.spiderman.worker.data.SaveSpiderCnfReq;
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
    public Ret<Void> add(@Valid @RequestBody SaveSpiderCnfReq req)
    {
        spiderCnfService.add(req);
        return Ret.success();
    }

    @PostMapping("/worker/spider-cnf/update")
    public Ret<Void> update(@Valid @RequestBody SaveSpiderCnfReq req)
    {
        spiderCnfService.update(req);
        return Ret.success();
    }

    @PostMapping("/worker/spider-cnf/delete/{id}")
    public Ret<Void> del(@PathVariable("id") String id)
    {
        spiderCnfService.delete(id);
        return Ret.success();
    }

    @PostMapping("/worker/spider-cnf/enable/{id}")
    public Ret<Void> enable(@PathVariable("id") String id)
    {
        spiderCnfService.enable(id);
        return Ret.success();
    }

    @PostMapping("/worker/spider-cnf/disable/{id}")
    public Ret<Void> disable(@PathVariable("id") String id)
    {
        spiderCnfService.disable(id);
        return Ret.success();
    }


    @PostMapping("/worker/spider-cnf/get/{id}")
    public Ret<SpiderCnfData> get(@PathVariable("id") String id)
    {
        SpiderCnfData data = spiderCnfService.get(id);
        return Ret.success(data);
    }


    @GetMapping("/worker/spider-cnf/list")
    public Ret<Page<SpiderCnfData>> listTask(QrySpiderCnfReq req, @PageableDefault Pageable pageable)
    {
        Page<SpiderCnfData> data = spiderCnfService.findAll(req, pageable);
        return Ret.success(data);
    }

}
