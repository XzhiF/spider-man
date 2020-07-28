package xzf.spiderman.scheduler.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import xzf.spiderman.common.Ret;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.common.exception.ConfigNotValidException;
import xzf.spiderman.scheduler.configuration.SchedulerConst;
import xzf.spiderman.scheduler.data.JobTaskData;
import xzf.spiderman.scheduler.entity.Task;
import xzf.spiderman.scheduler.repository.TaskRepository;
import xzf.spiderman.worker.data.SubmitSpiderReq;
import xzf.spiderman.worker.feign.SpiderMasterFeignService;

import java.util.Date;

@Slf4j
public class SpiderJob implements Job
{

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private SpiderMasterFeignService spiderMasterFeignService;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        // 1. 找到task
        Task task = getTask(context);

        // 2. 远程请求spider-master启动爬虫
        String spiderId = submitSpider(task);

        // 3. 保存task-running状态
        updateTaskRunning(task);

        // 4. 保存task，跟uuid到redis
        JobTaskData jobTask = createJobTask(task,(String)context.get("UUID"));
        saveDataToRedis(jobTask, spiderId);

        // 5. 准备一个http接口，spider-master会进行回调，返回结果
    }

    private JobTaskData createJobTask(Task task, String uuid)
    {
        JobTaskData jobTask = new JobTaskData();

        jobTask.setTaskId(task.getId());
        jobTask.setTaskGroupId(task.getGroupId());

        //
        jobTask.setUuid(uuid);
        jobTask.setStartTime(new Date());

        return jobTask;
    }

    private void saveDataToRedis(JobTaskData task, String spiderId)
    {
        RMap<Object, Object> map = redisson.getMap(SchedulerConst.REDIS_JOB_TASK_KEY, JsonJacksonCodec.INSTANCE);
        String key = task.getTaskGroupId()+":"+spiderId;
        map.put(key, task);
    }

    private String submitSpider(Task task)
    {
        SubmitSpiderReq req = new SubmitSpiderReq();
        req.setGroupId(task.getGroupId());
        Ret<String> ret = spiderMasterFeignService.submitSpider(req);

        if(ret.isSuccess()){
            return ret.getData();
        }

        throw new BizException(ret.getCode(), ret.getErrorMsg());
    }


    private void updateTaskRunning(Task task)
    {
        task.setStatus(Task.STATUS_RUNNING);
        taskRepository.save(task);
    }

    private Task getTask(JobExecutionContext context)
    {
        String taskId = context.getJobDetail().getKey().getName();
        return taskRepository.findById(taskId).orElseThrow(()->new ConfigNotValidException("TaskId["+taskId+"] ，未找到。"));
    }
}
