package xzf.spiderman.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import xzf.spiderman.common.Ret;
import xzf.spiderman.scheduler.data.AddTaskReq;
import xzf.spiderman.scheduler.data.QryTaskReq;
import xzf.spiderman.scheduler.data.TaskData;
import xzf.spiderman.scheduler.data.UpdateTaskReq;
import xzf.spiderman.scheduler.service.TaskService;

import javax.validation.Valid;

@RestController
public class TaskController
{
    @Autowired
    private TaskService taskService;

    @PostMapping("/scheduler/task/add")
    public Ret<Void> addTask(@Valid @RequestBody AddTaskReq req)
    {
        taskService.add(req);
        return Ret.success();
    }

    @PostMapping("/scheduler/task/update")
    public Ret<Void> addTask(@Valid @RequestBody UpdateTaskReq req)
    {
        taskService.update(req);
        return Ret.success();
    }

    @GetMapping("/scheduler/task/list")
    public Ret<Page<TaskData>> listTask(QryTaskReq req, @PageableDefault Pageable pageable)
    {
        Page<TaskData> data = taskService.findAll(req, pageable);
        return Ret.success(data);
    }

    @GetMapping("/scheduler/task/get/{id}")
    public Ret<TaskData> get(@PathVariable("id") String id)
    {
        TaskData ret = taskService.findById(id);
        return Ret.success(ret);
    }


    @PostMapping("/scheduler/task/delete/{id}")
    public Ret<Void> delete(@PathVariable("id") String id)
    {
        taskService.delete(id);
        return Ret.success();
    }

}
