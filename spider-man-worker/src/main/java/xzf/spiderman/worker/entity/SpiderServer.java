package xzf.spiderman.worker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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


}
