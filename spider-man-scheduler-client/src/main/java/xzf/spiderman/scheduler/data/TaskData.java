package xzf.spiderman.scheduler.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TaskData
{
    private String id;

    private String name;

    private String description;

    private Integer status;

    private Integer activeFlag;

    private Date lastRunningTime;

    private Integer lastRunningResult;

    private Date createTime;

    private String spiderGroupId;

    private String className;

    private Integer classType;

    private String cron;

    // only for detail
    private List<TaskArgData> args = new ArrayList<>();
}
