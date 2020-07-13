package xzf.spiderman.worker.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class StartSpiderReq
{
    private String spiderId;
    private String cnfId;
}
