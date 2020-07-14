package xzf.spiderman.worker.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class SpiderTaskData
{
    public static final int STATUS_INIT = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_CAN_CLOSE = 2;
    public static final int STATUS_CLOSED = 3;

    private String spiderId;
    private String groupId;
    private String cnfId;
    private int status;
}
