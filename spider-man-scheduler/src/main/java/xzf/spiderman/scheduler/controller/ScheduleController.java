package xzf.spiderman.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xzf.spiderman.common.Ret;
import xzf.spiderman.scheduler.data.ScheCmd;
import static xzf.spiderman.scheduler.data.ScheCmd.*;
import xzf.spiderman.scheduler.service.ScheCmdProducerService;

@RestController
public class ScheduleController
{
    @Autowired
    private ScheCmdProducerService scheCmdProducerService;

    @PostMapping("/scheduler/schedule/stop/{taskId}")
    public Ret<Void> stop(@PathVariable("taskId") String taskId)
    {
        scheCmdProducerService.offer(new ScheCmd(UNSCHEDULE, taskId));
        return Ret.success();
    }

    @PostMapping("/scheduler/schedule/start/{taskId}")
    public Ret<Void> start(@PathVariable("taskId") String taskId)
    {
        scheCmdProducerService.offer(new ScheCmd(SCHEDULE, taskId));
        return Ret.success();
    }

    @PostMapping("/scheduler/schedule/trigger/{taskId}")
    public Ret<Void> trigger(@PathVariable("taskId") String taskId)
    {
        scheCmdProducerService.offer(new ScheCmd(TRIGGER, taskId));
        return Ret.success();
    }

}
