package xzf.spiderman.worker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name = "spider_store")
public class SpiderStore
{
    public static final int TYPE_MONGO = 1;

    @Id
    @Column(name = "spider_store_id")
    private String id;

    @Column(name = "host")
    private String host;

    @Column(name = "port")
    private Integer port;

    @Column(name = "database")
    private String database;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "type")
    private Integer type;

    @Column(name = "url")
    private String url;

    @Column(name = "username")
    private String username ;

    @Column(name = "password")
    private String password;

}
