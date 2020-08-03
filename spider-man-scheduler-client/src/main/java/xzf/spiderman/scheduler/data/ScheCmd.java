package xzf.spiderman.scheduler.data;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ScheCmd implements Serializable
{
    // Task
    public static final int IDLE = 0;
    public static final int ENABLE = 1;
    public static final int DISABLE = 2;
    public static final int SCHEDULE = 3;
    public static final int UNSCHEDULE = 4;
    public static final int TRIGGER = 5;


    // GROUP
    public static final int SCHEDULE_GROUP = 6;
    public static final int UNSCHEDULE_GROUP = 7;
    public static final int TRIGGER_GROUP = 8;

    private Integer action = 0;
    private String taskOrGroupId;

    private String uuid;
    private Integer retryTimes;
    private Integer maxRetires;
    private Integer retryWaitingSeconds;

    public ScheCmd() {
    }

    public ScheCmd(Integer action, String taskOrGroupId) {
        this.action = action;
        this.taskOrGroupId = taskOrGroupId;

        this.uuid = UUID.randomUUID().toString();
        this.retryTimes = 0;
        this.maxRetires = 3;
        this.retryWaitingSeconds = 3;
    }
}
