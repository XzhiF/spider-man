package xzf.spiderman.worker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import xzf.spiderman.worker.data.AddSpiderCnfReq;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "spider_cnf")
@Data
@NoArgsConstructor
@ToString
public class SpiderCnf
{
    public static final int STATUS_STOPED = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_WAITING = 2;

    public static final int ACTIVE_FLAG_DISABLE = 0;
    public static final int ACTIVE_FLAG_ENABLE = 1;

    public static final int TYPE_WEB = 1;
    public static final int TYPE_DB = 2;
    public static final int TYPE_FILE = 3;



    @Id
    @Column(name = "spider_cnf_id")
    private String id ;

    @ManyToOne
    @JoinColumn(name = "spider_group_id")
    private SpiderGroup group;

    //
    @ManyToOne
    @JoinColumn(name = "spider_server_id")
    private SpiderServer server;

    @Column(name = "spider_name")
    private String name;

    @Column(name = "spider_type")
    private Integer type;

    @Column(name = "spider_params")
    private String params;
    
    @Column(name = "spider_desc")
    private String desc;

    @Column(name = "status")
    private Integer status;

    @Column(name = "active_flag")
    private Integer activeFlag;

    @Column(name = "create_time")
    private Date createTime;


    public static SpiderCnf create(AddSpiderCnfReq req,
                                   SpiderGroup group,
                                   SpiderServer server)
    {
        SpiderCnf ret = new SpiderCnf();

        ret.setId(req.getId());
        ret.setName(req.getName());
        ret.setType(req.getType());
        ret.setParams(req.getParams());
        ret.setDesc(req.getDesc());

        ret.setServer(server);
        ret.setGroup(group);

        ret.setStatus(STATUS_STOPED);
        ret.setActiveFlag(ACTIVE_FLAG_ENABLE);
        ret.setCreateTime(new Date());

        return ret;
    }
}



