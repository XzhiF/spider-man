package xzf.spiderman.worker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import xzf.spiderman.worker.data.SaveSpiderGroupReq;
import xzf.spiderman.worker.data.SpiderGroupData;

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



    public static SpiderGroup create(SaveSpiderGroupReq req)
    {
        SpiderGroup ret = new SpiderGroup();
        ret.setId(req.getId());
        ret.setName(req.getName());
        ret.setDesc(req.getDesc());
        ret.setCreateTime(new Date());
        return ret;
    }

    public void update(SaveSpiderGroupReq req) {
        this.setName(req.getName());
        this.setDesc(req.getDesc());
    }

    public SpiderGroupData asSpiderGroupData() {
        SpiderGroupData ret = new SpiderGroupData();
        BeanUtils.copyProperties(this,ret);
        return ret;
    }
}
