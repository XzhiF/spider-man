package xzf.spiderman.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import xzf.spiderman.common.Ret;
import xzf.spiderman.worker.data.*;
import xzf.spiderman.worker.service.SpiderStoreService;

import javax.validation.Valid;

@RestController
public class SpiderStoreController
{
    @Autowired
    private SpiderStoreService spiderStoreService;

    @PostMapping("/worker/spider-store/add")
    public Ret<Void> add(@Valid @RequestBody SaveSpiderStoreReq req)
    {
        spiderStoreService.add(req);
        return Ret.success();
    }

    @PostMapping("/worker/spider-store/update")
    public Ret<Void> update(@Valid @RequestBody SaveSpiderStoreReq req)
    {
        spiderStoreService.update(req);
        return Ret.success();
    }

    @PostMapping("/worker/spider-store/delete/{id}")
    public Ret<Void> del(@PathVariable("id") String id)
    {
        spiderStoreService.delete(id);
        return Ret.success();
    }


    @PostMapping("/worker/spider-store/get/{id}")
    public Ret<SpiderStoreData> get(@PathVariable("id") String id)
    {
        SpiderStoreData data = spiderStoreService.get(id);
        return Ret.success(data);
    }

    @GetMapping("/worker/spider-store/list")
    public Ret<Page<SpiderStoreData>> listTask(QrySpiderStoreReq req, @PageableDefault Pageable pageable)
    {
        Page<SpiderStoreData> data = spiderStoreService.findAll(req, pageable);
        return Ret.success(data);
    }

}
