package xzf.spiderman.scheduler.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import xzf.spiderman.scheduler.data.AddTaskReq;
import xzf.spiderman.scheduler.data.TaskArgData;
import xzf.spiderman.scheduler.data.TaskData;
import xzf.spiderman.scheduler.data.UpdateTaskReq;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "task")
public class Task implements Serializable
{
    public static final int STATUS_STOP = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_WAITING = 2;

    public static final int ACTIVE_FLAG_DISABLE = 0;
    public static final int ACTIVE_FLAG_ENABLE = 1;

    public static final int TASK_RESULT_ERROR = 0;
    public static final int TASK_RESULT_SUCCESS = 1;
    public static final int TASK_RESULT_CANCEL = 2;

    @Id
    @Column(name = "task_id", nullable = false)
    private String id;

    @Column(name = "task_group_id")
    private String groupId;

    @Column(name = "task_name", nullable = false)
    private String name;

    @Column(name = "task_description")
    private String description;

    @Column(name = "status")
    private Integer status;

    @Column(name = "active_flag")
    private Integer activeFlag;

    @Column(name = "last_running_time")
    private Date lastRunningTime;

    @Column(name = "last_running_result")
    private Integer lastRunningResult;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "spider_group_id")
    private String spiderGroupId;

    @Column(name = "job_class")
    private String jobClass;

    @Column(name = "schedule_class")
    private String scheduleClass;

    @Column(name = "schedule_props")
    private String scheduleProps;


    //

    public static Task create(AddTaskReq req)
    {
        Task task = new Task();
        task.setId(req.getId());
        task.setName(req.getName());
        task.setDescription(req.getDescription());
        task.setSpiderGroupId(req.getSpiderGroupId());
        task.setJobClass(req.getJobClass());
        task.setScheduleClass(req.getScheduleClass());
        task.setGroupId(req.getGroupId());

        task.setStatus(STATUS_STOP);
        task.setActiveFlag(ACTIVE_FLAG_ENABLE);
        task.setLastRunningTime(null);
        task.setLastRunningResult(null);

        return task;
    }

    public List<TaskArg> createArgs(List<TaskArgData> args)
    {
        List<TaskArg> results = new ArrayList<>();
        for (TaskArgData arg : args)
        {
            TaskArg each = new TaskArg();
            each.setTaskId(this.getId());
            each.setKey(arg.getKey());
            each.setValue(arg.getValue());
            results.add(each);
        }
        return  results;
    }

    public void update(UpdateTaskReq req)
    {
        this.setGroupId(req.getGroupId());
        this.setName(req.getName());
        this.setDescription(req.getDescription());
        this.setSpiderGroupId(req.getSpiderGroupId());
        this.setJobClass(req.getJobClass());
        this.setScheduleClass(req.getScheduleClass());

    }

    public TaskData asTaskData()
    {
        TaskData ret = new TaskData();
        BeanUtils.copyProperties(this, ret);
        return ret;
    }

    public TaskData asTaskData(List<TaskArg> args)
    {
        TaskData ret = new TaskData();
        BeanUtils.copyProperties(this, ret);

        List<TaskArgData> retArgs = args.stream().map(this::asTaskArgData).collect(Collectors.toList());
        ret.setArgs(retArgs);

        return ret;
    }

    public TaskArgData asTaskArgData(TaskArg arg)
    {
        return new TaskArgData(arg.getKey(), arg.getValue());
    }


}
