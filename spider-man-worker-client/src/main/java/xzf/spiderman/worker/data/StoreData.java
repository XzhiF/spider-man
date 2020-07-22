package xzf.spiderman.worker.data;

import lombok.Data;

@Data
public class StoreData
{
    private String cnfId;

    private String storeId;

    private String host;

    private Integer port;

    private String tableName;

    private Integer type;

    private String username ;

    private String password;

}
