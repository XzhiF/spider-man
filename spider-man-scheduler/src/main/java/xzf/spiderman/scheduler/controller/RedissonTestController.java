package xzf.spiderman.scheduler.controller;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class RedissonTestController {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CuratorFramework curatorFramework;

    @GetMapping("redisson")
    public Map<String,Object> test()
    {
//        redissonClient.getList("/test-list").add("aaa");
        return Collections.singletonMap("redisson", "abc");
    }

    @GetMapping("curator")
    public Map<String,Object> test2() throws Exception
    {
//        curatorFramework.start();
        curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath("/test");
        return Collections.singletonMap("curator", curatorFramework.toString());
    }
}
