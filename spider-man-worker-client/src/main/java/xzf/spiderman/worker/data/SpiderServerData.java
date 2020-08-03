package xzf.spiderman.worker.data;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class SpiderServerData
{

    private String id;

    private String host;

    private Integer port;

    private String zone;

    private Date createTime;
}
