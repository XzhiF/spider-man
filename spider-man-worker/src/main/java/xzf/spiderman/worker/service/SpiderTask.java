package xzf.spiderman.worker.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class SpiderTask implements Serializable
{
    public static final int STATUS_INIT = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_CAN_CLOSE = 2;
    public static final int STATUS_CLOSED = 3;

    private String spiderId;
    private String groupId;
    private String cnfId;
    private int status;

    public static SpiderTask newTask(SpiderKey key, Integer status)
    {
        SpiderTask task = new SpiderTask();
        task.setSpiderId(key.getSpiderId());
        task.setGroupId(key.getGroupId());
        task.setCnfId(key.getCnfId());
        task.setStatus(status);
        return task;
    }

    public static SpiderTask newRunningTask(SpiderKey key)
    {
        return newTask(key, SpiderTask.STATUS_RUNNING);
    }

    public static SpiderTask newCanCloseTask(SpiderKey key)
    {
        return newTask(key, SpiderTask.STATUS_CAN_CLOSE);
    }


    public static SpiderTask newClosedTask(SpiderKey key)
    {
        return newTask(key, SpiderTask.STATUS_CLOSED);
    }


    public static SpiderTask newInitTask(SpiderKey key)
    {
        return newTask(key, SpiderTask.STATUS_INIT);
    }
}
