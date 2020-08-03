package xzf.spiderman.worker.data;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SaveSpiderStoreReq
{

    @NotBlank(message = "ID不能为空")
    private String id;

    private String host;

    private Integer port;

    private String database;

    private String tableName;

    private Integer type;

    private String url;

    private String username ;

    private String password;

    private Integer urlType;

}
