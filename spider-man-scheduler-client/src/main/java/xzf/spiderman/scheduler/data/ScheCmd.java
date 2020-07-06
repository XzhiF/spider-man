package xzf.spiderman.scheduler.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScheCmd implements Serializable
{
    // ACTION
    public static final int IDLE = 0;
    public static final int ENABLE = 1;
    public static final int DISABLE = 2;
    public static final int SCHEDULE = 3;
    public static final int UNSCHEDULE = 4;
    public static final int TRIGGER = 5;

    private Integer action = 0;
    private String taskId;

    public ScheCmd() {
    }

    public ScheCmd(Integer action, String taskId) {
        this.action = action;
        this.taskId = taskId;
    }
}
