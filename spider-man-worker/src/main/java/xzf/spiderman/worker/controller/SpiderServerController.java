package xzf.spiderman.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import xzf.spiderman.common.Ret;
import xzf.spiderman.worker.data.*;
import xzf.spiderman.worker.service.SpiderServerService;

import javax.validation.Valid;

@RestController
public class SpiderServerController
{
    @Autowired
    private SpiderServerService spiderServerService;

    @PostMapping("/worker/spider-server/add")
    public Ret<Void> add(@Valid @RequestBody SaveSpiderServerReq req)
    {
        spiderServerService.add(req);
        return Ret.success();
    }

    @PostMapping("/worker/spider-server/update")
    public Ret<Void> update(@Valid @RequestBody SaveSpiderServerReq req)
    {
        spiderServerService.update(req);
        return Ret.success();
    }

    @PostMapping("/worker/spider-server/delete/{id}")
    public Ret<Void> del(@PathVariable("id") String id)
    {
        spiderServerService.delete(id);
        return Ret.success();
    }


    @PostMapping("/worker/spider-server/get/{id}")
    public Ret<SpiderServerData> get(@PathVariable("id") String id)
    {
        SpiderServerData data = spiderServerService.get(id);
        return Ret.success(data);
    }

    @GetMapping("/worker/spider-server/list")
    public Ret<Page<SpiderServerData>> listTask(QrySpiderServerReq req, @PageableDefault Pageable pageable)
    {
        Page<SpiderServerData> data = spiderServerService.findAll(req, pageable);
        return Ret.success(data);
    }

}
