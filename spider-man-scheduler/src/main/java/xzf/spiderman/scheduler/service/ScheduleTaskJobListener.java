package xzf.spiderman.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.scheduler.entity.Task;
import xzf.spiderman.scheduler.entity.TaskLog;
import xzf.spiderman.scheduler.repository.TaskLogRepository;
import xzf.spiderman.scheduler.repository.TaskRepository;

import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class ScheduleTaskJobListener implements JobListener
{
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskLogRepository taskLogRepository;

    @Override
    public String getName()
    {
        return ScheduleTaskJobListener.class.getName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context)
    {
        Task task = findTask(context);
        if(task == null) {return ;}

        context.put("UUID", UUID.randomUUID().toString());

        updateTask4JobToBeExecuted(task);
        addTaskLog4JobToBeExecuted(task, context);
    }


    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException)
    {
        Task task = findTask(context);
        if(task == null) {return ;}

        updateTask4JobWasExecuted(task, jobException);
        addTaskLog4JobWasExecuted(task,context, jobException);
    }



    private Task findTask(JobExecutionContext context)
    {
        String taskId = context.getJobDetail().getKey().getName();
        return taskRepository.findById(taskId).orElse(null);
    }

    private void updateTask4JobToBeExecuted(Task task)
    {
        task.setStatus(Task.STATUS_RUNNING);
        taskRepository.save(task);
    }

    private void updateTask4JobWasExecuted(Task task,JobExecutionException jobException)
    {
        if(Task.ACTIVE_FLAG_ENABLE == task.getActiveFlag().intValue()) {
            task.setStatus(Task.STATUS_WAITING);
        } else {
            task.setStatus(Task.STATUS_STOPED);
        }
        task.setLastRunningTime(new Date());
        if(jobException != null){
            task.setLastRunningResult(Task.TASK_RESULT_ERROR);
        }else{
            task.setLastRunningResult(Task.TASK_RESULT_SUCCESS);
        }
        taskRepository.save(task);
    }

    private void addTaskLog4JobToBeExecuted(Task task, JobExecutionContext context)
    {
        TaskLog l = new TaskLog();
        l.setId(TaskLog.nextId());
        l.setTaskId(task.getId());
        l.setEvent("JobToBeExecuted");
        l.setHasError(TaskLog.HAS_ERROR_FALSE);
        l.setContent(task.getId()+","+task.getGroupId()+": JobToBeExecuted" );
        l.setUuid(context.get("UUID").toString());
        l.setCreateTime(new Date());
        taskLogRepository.save(l);
    }

    private void addTaskLog4JobWasExecuted(Task task, JobExecutionContext context, JobExecutionException jobException)
    {
        TaskLog l = new TaskLog();
        l.setId(TaskLog.nextId());
        l.setTaskId(task.getId());
        l.setEvent("JobWasExecuted");
        l.setHasError(jobException!=null?TaskLog.HAS_ERROR_TRUE:TaskLog.HAS_ERROR_FALSE);
        l.setContent(task.getId()+","+task.getGroupId()+": JobWasExecuted. " + (jobException!=null?"error:"+jobException.getMessage():"") );
        l.setUuid(context.get("UUID").toString());
        l.setCreateTime(new Date());
        taskLogRepository.save(l);

    }

}
