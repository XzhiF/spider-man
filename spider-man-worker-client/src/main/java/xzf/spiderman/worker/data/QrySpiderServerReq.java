package xzf.spiderman.worker.data;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class QrySpiderServerReq
{

    private String startWithId;

    private String startWithHost;


}
