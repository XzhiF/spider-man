package xzf.spiderman.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.scheduler.entity.Task;
import xzf.spiderman.scheduler.entity.TaskArg;
import xzf.spiderman.scheduler.repository.TaskArgRepository;
import xzf.spiderman.scheduler.repository.TaskRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 //        scheduler.scheduleJob()
 //        scheduler.pauseJob();
 //        scheduler.addJob();
 //        scheduler.resumeJob();
 //        scheduler.deleteJob()
 //        scheduler.triggerJob();
 //        scheduler.checkExists()
 //        scheduler.unscheduleJob()
 //        scheduler.rescheduleJob()
 */
@Service
@Slf4j
public class ScheduleService
{
    @Autowired
    private  Scheduler scheduler;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskArgRepository taskArgRepository;
    @Autowired
    private ScheduleTaskJobListener scheduleTaskJobListener;

    @Transactional
    public void startup() throws SchedulerException
    {
        log.info("ScheduleService startup.");
        scheduler.getListenerManager().addJobListener(scheduleTaskJobListener);

        int updated = taskRepository.updateAllStatusToStop();
        log.info("update to stop task size = " + updated);

        List<Task> tasks = taskRepository.findAllByActiveFlag(Task.ACTIVE_FLAG_ENABLE);
        log.info("active task size = " + tasks.size());

        for (Task task : tasks)
        {
            scheduleTask(task);
        }
    }

    @Transactional
    public void scheduleTask(String taskId) throws SchedulerException
    {
        Task task = getTask(taskId);
        scheduleTask(task);
    }

    @Transactional
    public void scheduleTask(Task task) throws SchedulerException
    {
        List<TaskArg> args =  taskArgRepository.findAllByTaskId(task.getId());

        JobDetail job = JobBuilder
                .newJob(  resolveClass(task.getJobClass()) )
                .withIdentity(task.getId(), task.getGroupId())
                .withDescription(task.getDescription())
                .setJobData( new JobDataMap(TaskArg.toMap(args)) )
                .storeDurably()
                .build();

        // TASK -> ScheduleClass ->
        // TASK -> ScheduleProps ->
        // ---> SchedulerBuilder

        Trigger trigger =  TriggerBuilder
                .newTrigger()
                .withIdentity(task.getId()+".trigger", task.getGroupId())
                .withSchedule(createScheduleBuilder(task))
                .build();

        scheduler.scheduleJob(job, trigger);

        //
        task.setStatus(Task.STATUS_WAITING);
        taskRepository.save(task);
    }

    @Transactional
    public void unscheduleTask(String taskId) throws SchedulerException
    {
        Task task = getTask(taskId);

        TriggerKey triggerKey = new TriggerKey(task.getId()+".trigger", task.getGroupId());
        boolean success = scheduler.unscheduleJob(triggerKey);
        if(!success){
            throw new BizException("Task " + taskId + ", 停止失败：未找到对应的触发器。");
        }
        log.info("unschedule task " + taskId);

        task.setStatus(Task.STATUS_STOP);
        taskRepository.save(task);
    }

    @Transactional
    public void unscheduleAllTasks() throws SchedulerException
    {
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());

        List<TriggerKey>  triggerKeys = jobKeys.stream()
                .map(j->new TriggerKey(j.getName()+".trigger", j.getGroup()))
                .collect(Collectors.toList());

        scheduler.unscheduleJobs(triggerKeys);

        int updated = taskRepository.updateAllStatusToStop();
        log.info("update to stop task size = " + updated);
    }


    @Transactional
    public void triggerTask(String taskId) throws SchedulerException
    {
        Task task = getTask(taskId);

        JobKey jobKey = new JobKey(task.getId(), task.getGroupId());

        if (!scheduler.checkExists(jobKey)) {

            List<TaskArg> args =  taskArgRepository.findAllByTaskId(task.getId());

            JobDetail job = JobBuilder
                    .newJob(  resolveClass(task.getJobClass()) )
                    .withIdentity(task.getId(), task.getGroupId())
                    .withDescription(task.getDescription())
                    .setJobData( new JobDataMap(TaskArg.toMap(args)) )
                    .storeDurably()
                    .build();

            scheduler.addJob(job, true);
        }

        scheduler.triggerJob(jobKey);
    }


    private Task getTask(String taskId)
    {
        Task task = taskRepository.findById(taskId).orElseThrow(()->new BizException("task "+taskId+",不存在."));
        return task;
    }


    private ScheduleBuilder<?> createScheduleBuilder(Task task)
    {
        return ScheduleBuilderFactory.create(task);
    }

    private Class<? extends Job> resolveClass(String className)
    {
        try {
            return (Class<? extends Job>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BizException("任务类 JobClass" + className + ", 未找到。");
        }
    }
}
