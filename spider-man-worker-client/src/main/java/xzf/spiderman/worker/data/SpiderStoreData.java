package xzf.spiderman.worker.data;

import lombok.Data;

@Data
public class SpiderStoreData
{
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
