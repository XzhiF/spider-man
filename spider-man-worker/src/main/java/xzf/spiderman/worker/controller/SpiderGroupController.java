package xzf.spiderman.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import xzf.spiderman.common.Ret;
import xzf.spiderman.worker.data.*;
import xzf.spiderman.worker.service.SpiderGroupService;

import javax.validation.Valid;

@RestController
public class SpiderGroupController
{
    @Autowired
    private SpiderGroupService spiderGroupService;

    @PostMapping("/worker/spider-group/add")
    public Ret<Void> add(@Valid @RequestBody SaveSpiderGroupReq req)
    {
        spiderGroupService.add(req);
        return Ret.success();
    }

    @PostMapping("/worker/spider-group/update")
    public Ret<Void> update(@Valid @RequestBody SaveSpiderGroupReq req)
    {
        spiderGroupService.update(req);
        return Ret.success();
    }

    @PostMapping("/worker/spider-group/delete/{id}")
    public Ret<Void> del(@PathVariable("id") String id)
    {
        spiderGroupService.delete(id);
        return Ret.success();
    }


    @PostMapping("/worker/spider-group/get/{id}")
    public Ret<SpiderGroupData> get(@PathVariable("id") String id)
    {
        SpiderGroupData data = spiderGroupService.get(id);
        return Ret.success(data);
    }

    @GetMapping("/worker/spider-group/list")
    public Ret<Page<SpiderGroupData>> listTask(QrySpiderGroupReq req, @PageableDefault Pageable pageable)
    {
        Page<SpiderGroupData> data = spiderGroupService.findAll(req, pageable);
        return Ret.success(data);
    }

}
