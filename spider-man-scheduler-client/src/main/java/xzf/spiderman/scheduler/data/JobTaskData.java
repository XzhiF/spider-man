package xzf.spiderman.scheduler.data;

import lombok.Data;

import java.util.Date;

@Data
public class JobTaskData
{
    // Task
    private String taskId;

    private String taskGroupId;


    // Job
    private String uuid;

    private Date startTime;
}
