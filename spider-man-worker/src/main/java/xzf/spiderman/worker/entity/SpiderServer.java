package xzf.spiderman.worker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import xzf.spiderman.worker.data.SaveSpiderServerReq;
import xzf.spiderman.worker.data.SpiderServerData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name = "spider_server")
public class SpiderServer
{
    @Id
    @Column(name = "spider_server_id")
    private String id;

    @Column(name = "host")
    private String host;

    @Column(name = "port")
    private Integer port;

    @Column(name = "zone")
    private String zone;

    @Column(name = "create_time")
    private Date createTime;


    public static SpiderServer create(SaveSpiderServerReq req)
    {
        SpiderServer ret = new SpiderServer();
        BeanUtils.copyProperties(req, ret);
        ret.setCreateTime(new Date());
        return ret;
    }

    public void update(SaveSpiderServerReq req) {
        BeanUtils.copyProperties(req, this);
    }

    public SpiderServerData asData() {
        SpiderServerData ret = new SpiderServerData();
        BeanUtils.copyProperties(this,ret);
        return ret;
    }
}
