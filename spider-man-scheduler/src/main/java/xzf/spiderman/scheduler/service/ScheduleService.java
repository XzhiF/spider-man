package xzf.spiderman.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.event.Event;
import xzf.spiderman.common.event.EventListener;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.scheduler.entity.Task;
import xzf.spiderman.scheduler.entity.TaskArg;
import xzf.spiderman.scheduler.repository.TaskArgRepository;
import xzf.spiderman.scheduler.repository.TaskGroupRepository;
import xzf.spiderman.scheduler.repository.TaskRepository;
import xzf.spiderman.scheduler.service.event.TaskDisabledEvent;
import xzf.spiderman.scheduler.service.event.TaskEnabledEvent;

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
public class ScheduleService implements EventListener
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
    public void scheduleTask(String taskId)
    {
        Task task = getTask(taskId);
        scheduleTask(task);
    }

    public void scheduleTask(Task task)
    {
        try{

            List<TaskArg> args =  taskArgRepository.findAllByTaskId(task.getId());

            JobDetail job = JobBuilder
                    .newJob(  resolveClass(task.getJobClass()) )
                    .withIdentity(task.getId(), task.getGroupId())
                    .withDescription(task.getDescription())
                    .setJobData( new JobDataMap(TaskArg.toMap(args)) )
                    .storeDurably()
                    .build();


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
        catch (Exception e) {
            log.error("Task " + task.getId()+", 启动调度失败。 " + e.getMessage(), e);
            throw new BizException("Task " + task.getId()+", 启动调度失败。 " + e.getMessage(), e);
        }

    }

    @Transactional
    public void unscheduleTask(String taskId)
    {
        Task task = getTask(taskId);
        unscheduleTask(task);
    }

    public void unscheduleTask(Task task)
    {
        try{
            TriggerKey triggerKey = new TriggerKey(task.getId()+".trigger", task.getGroupId());
            boolean success = scheduler.unscheduleJob(triggerKey);
            if(!success){
                throw new BizException("Task " + task.getId() + ", 停止失败：未找到对应的触发器。");
            }
            log.info("unschedule task " + task.getId());

            task.setStatus(Task.STATUS_STOPPED);
            taskRepository.save(task);
        }
        catch (Exception e){
            log.error("Task " + task.getId()+", 停止调度失败。 " + e.getMessage(), e);
            throw new BizException("Task " + task.getId()+", 停止调度失败。 " + e.getMessage(), e);
        }
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
    public void triggerTask(String taskId)
    {
        Task task = getTask(taskId);
        triggerTask(task);
    }

    public void triggerTask(Task task)
    {
        try{
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
        catch (Exception e){
            log.error("Task " + task.getId()+", Triiger调度失败。 " + e.getMessage(), e);
            throw new BizException("Task " + task.getId()+", Trigger调度失败。 " + e.getMessage(), e);
        }
    }



    //// TaskGroup

    @Transactional
    public void scheduleTaskGroup(String taskGroupId)
    {
        List<Task> tasks = taskRepository.findAllByGroupIdAndActiveFlag(taskGroupId, Task.ACTIVE_FLAG_ENABLE);
        for (Task task : tasks)
        {
            scheduleTask(task);
        }
    }

    @Transactional
    public void unscheduleTaskGroup(String taskGroupId)
    {
        List<Task> tasks = taskRepository.findAllByGroupIdAndActiveFlag(taskGroupId, Task.ACTIVE_FLAG_ENABLE);
        for (Task task : tasks)
        {
            unscheduleTask(task);
        }
    }

    @Transactional
    public void triggerTaskGroup(String taskGroupId)
    {
        List<Task> tasks = taskRepository.findAllByGroupIdAndActiveFlag(taskGroupId, Task.ACTIVE_FLAG_ENABLE);
        for (Task task : tasks)
        {
            triggerTask(task);
        }
    }

    ////


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

    @Override
    public boolean supportEventType(Class<? extends Event> clazz)
    {
        return TaskDisabledEvent.class.equals(clazz)
                || TaskEnabledEvent.class.equals(clazz);
    }

    @Override
    @Transactional
    public void onEvent(Event event)
    {
        if(event instanceof TaskEnabledEvent) {
            scheduleTask( ((TaskEnabledEvent)event).getTask() );
        }

        if(event instanceof TaskDisabledEvent) {
            unscheduleTask( ((TaskDisabledEvent)event).getTask() );
        }
    }
}
