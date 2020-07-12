package xzf.spiderman.worker.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class StartTaskReq
{
    private String spiderGroupId;
}
