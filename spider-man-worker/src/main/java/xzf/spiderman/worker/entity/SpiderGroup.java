package xzf.spiderman.worker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "spider_group")
public class SpiderGroup
{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "spider_group_id")
    private String id;

    @Column(name = "spider_group_name" , nullable = false)
    private String name;

    @Column(name = "spider_group_desc")
    private String desc;
}
