package xzf.spiderman.worker.data;

import lombok.Data;

import java.util.Date;

@Data
public class SpiderCnfData
{
    private String id ;

    private String name;

    private Integer type;

    private String desc;

    private Integer status;

    private Integer activeFlag;

    private Date createTime;

    private String params;

    private String processor;

    private Integer workerThreads;

    private Integer maxPollTimeoutCount;

    private Integer pollTimeoutSeconds;

    private Integer mode;

    // spider  group
    private String groupId;

    private String groupName;

    private String groupDesc;

    private Date groupCreateTime;

    // spider server
    private String serverId;

    private String serverHost;

    private Integer serverPort;

    private String serverZone;

    private Date serverCreateTime;

}
