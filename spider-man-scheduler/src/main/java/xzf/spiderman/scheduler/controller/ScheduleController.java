package xzf.spiderman.scheduler.controller;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xzf.spiderman.common.Ret;
import xzf.spiderman.scheduler.data.ScheCmd;
import static xzf.spiderman.scheduler.data.ScheCmd.*;
import xzf.spiderman.scheduler.service.ScheduleProducerService;
import xzf.spiderman.scheduler.service.ScheduleService;

@RestController
public class ScheduleController
{
    @Autowired
    private ScheduleProducerService scheduleProducerService;

    @PostMapping("/scheduler/schedule/stop/{taskId}")
    public Ret<Void> stop(@PathVariable("taskId") String taskId)
    {
        scheduleProducerService.offer(new ScheCmd(UNSCHEDULE, taskId));
        return Ret.success();
    }

    @PostMapping("/scheduler/schedule/start/{taskId}")
    public Ret<Void> start(@PathVariable("taskId") String taskId)
    {
        scheduleProducerService.offer(new ScheCmd(SCHEDULE, taskId));
        return Ret.success();
    }

    @PostMapping("/scheduler/schedule/trigger/{taskId}")
    public Ret<Void> trigger(@PathVariable("taskId") String taskId)
    {
        scheduleProducerService.offer(new ScheCmd(TRIGGER, taskId));
        return Ret.success();
    }

}
