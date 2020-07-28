package xzf.spiderman.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.scheduler.configuration.SchedulerConst;
import xzf.spiderman.scheduler.data.JobTaskCallbackReq;
import xzf.spiderman.scheduler.data.JobTaskData;
import xzf.spiderman.scheduler.entity.Task;
import xzf.spiderman.scheduler.entity.TaskLog;
import xzf.spiderman.scheduler.repository.TaskLogRepository;
import xzf.spiderman.scheduler.repository.TaskRepository;

import java.util.Date;

@Slf4j
@Service
public class JobTaskService
{
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskLogRepository taskLogRepository;

    @Autowired
    private RedissonClient ression;

    public void acceptCallback(JobTaskCallbackReq req)
    {
        // 1. 找到jobTask
        JobTaskData jobTask = getJobTask(req);

        if(jobTask == null){
            log.warn("处理SpiderJob返回callback未找到对应key["+getJobTaskKey(req)+"]的JobTask数据。");
            return ;
        }

        // 2. 更新task
        Task task = getTask(jobTask.getTaskId());
        updateTask( task );

        // 3. 写log
        addLog( task, jobTask );
    }



    // private ....


    private String getJobTaskKey(JobTaskCallbackReq req)
    {
        return req.getGroupId()+":"+req.getSpiderId();
    }

    private JobTaskData getJobTask(JobTaskCallbackReq req)
    {
        RMap<Object, Object> map = ression.getMap(SchedulerConst.REDIS_JOB_TASK_KEY, JsonJacksonCodec.INSTANCE);

        String key = getJobTaskKey(req);

        JobTaskData jobTask = (JobTaskData) map.get(key);

        return jobTask;
    }


    private Task getTask(String id)
    {
        Task task = taskRepository.findById(id).orElseThrow(()->new BizException("task "+id+", 不存在"));
        return task;
    }

    private void updateTask(Task task)
    {
        task.completeTask(Task.TASK_RESULT_SUCCESS);
        taskRepository.save(task);
    }

    // 写log

    private void addLog(Task task, JobTaskData jobTask)
    {
        TaskLog l = new TaskLog();
        l.setId(TaskLog.nextId());
        l.setTaskId(task.getId());
        l.setEvent("JobHasCompleted");
        l.setHasError(TaskLog.HAS_ERROR_FALSE);

        Date now = new Date();
        long durationSeconds = (now.getTime() - jobTask.getStartTime().getTime()) /1000;
        l.setContent(task.getId()+","+task.getGroupId()+". 执行完成。耗时："+durationSeconds+"秒."   );

        l.setUuid(jobTask.getUuid());
        l.setCreateTime(now);
        taskLogRepository.save(l);

    }
}
