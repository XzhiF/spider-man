package xzf.spiderman.scheduler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationServiceRegistry
{
    @Autowired
    private TaskService taskService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RedissonQueueProvider redissonQueueProvider;


    public TaskService taskService()
    {
        return taskService;
    }

    public ScheduleService scheduleService()
    {
        return scheduleService;
    }

    public RedissonQueueProvider redissonQueueProvider() {
        return redissonQueueProvider;
    }
}
