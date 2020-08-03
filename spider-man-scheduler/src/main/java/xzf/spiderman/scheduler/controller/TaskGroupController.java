package xzf.spiderman.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import xzf.spiderman.common.Ret;
import xzf.spiderman.scheduler.data.*;
import xzf.spiderman.scheduler.service.ScheCmdProducerService;
import xzf.spiderman.scheduler.service.TaskGroupService;

import javax.validation.Valid;

@RestController
public class TaskGroupController
{
    @Autowired
    private TaskGroupService taskGroupService;

    @PostMapping("/scheduler/task-group/add")
    public Ret<Void> add(@Valid @RequestBody SaveTaskGroupReq req)
    {
        taskGroupService.add(req);
        return Ret.success();
    }

    @PostMapping("/scheduler/task-group/update")
    public Ret<Void> update(@Valid @RequestBody SaveTaskGroupReq req)
    {
        taskGroupService.update(req);
        return Ret.success();
    }

    @GetMapping("/scheduler/task-group/list")
    public Ret<Page<TaskGroupData>> list(QryTaskGroupReq req, @PageableDefault Pageable pageable)
    {
        Page<TaskGroupData> data = taskGroupService.findAll(req, pageable);
        return Ret.success(data);
    }

    @GetMapping("/scheduler/task-group/get/{id}")
    public Ret<TaskGroupData> get(@PathVariable("id") String id)
    {
        TaskGroupData ret = taskGroupService.get(id);
        return Ret.success(ret);
    }

    @PostMapping("/scheduler/task-group/delete/{id}")
    public Ret<Void> delete(@PathVariable("id") String id)
    {
        taskGroupService.delete(id);
        return Ret.success();
    }

   
}
