package xzf.spiderman.worker.entity;

import javax.persistence.*;

@Entity
@Table(name = "spider_cnf_store")
public class SpiderCnfStore
{

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @Column(name = "spider_cnf_id", nullable = false)
    public String cnfId;

    @Column(name = "spider_store_id",nullable = false)
    private String storeId;


}
