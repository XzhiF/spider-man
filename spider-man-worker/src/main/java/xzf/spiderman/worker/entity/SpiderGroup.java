package xzf.spiderman.worker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "spider_group")
public class SpiderGroup implements Serializable
{
    @Id
    @Column(name = "spider_group_id")
    private String id;

    @Column(name = "spider_group_name" , nullable = false)
    private String name;

    @Column(name = "spider_group_desc")
    private String desc;

    @Column(name = "create_time")
    private Date createTime;

    public SpiderGroup(){}

    public SpiderGroup(String id)
    {
        this.id = id;
    }

}
