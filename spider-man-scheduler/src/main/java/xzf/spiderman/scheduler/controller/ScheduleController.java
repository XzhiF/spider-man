package xzf.spiderman.scheduler.controller;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xzf.spiderman.common.Ret;
import xzf.spiderman.scheduler.service.ScheduleService;

@RestController
public class ScheduleController
{
    @Autowired
    private ScheduleService scheduleService;


    @PostMapping("/scheduler/schedule/stop/{taskId}")
    public Ret<Void> stop(@PathVariable("taskId") String taskId) throws Exception
    {
        scheduleService.unscheduleTask(taskId);
        return Ret.success();
    }

    @PostMapping("/scheduler/schedule/trigger/{taskId}")
    public Ret<Void> trigger(@PathVariable("taskId") String taskId) throws Exception
    {
        scheduleService.triggerTask(taskId);
        return Ret.success();
    }

}
