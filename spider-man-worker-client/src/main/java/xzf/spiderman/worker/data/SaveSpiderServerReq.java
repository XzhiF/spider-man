package xzf.spiderman.worker.data;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class SaveSpiderServerReq
{

    @NotBlank(message = "ID不能为空")
    private String id;

    @NotBlank(message = "HOST不能为空")
    private String host;

    @NotBlank(message = "PORT不能为空")
    private Integer port;

    private String zone;

}
