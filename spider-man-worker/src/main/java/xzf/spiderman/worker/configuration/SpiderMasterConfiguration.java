package xzf.spiderman.worker.configuration;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xzf.spiderman.starter.curator.CuratorAutoConfiguration;
import xzf.spiderman.worker.service.SpiderMaster;
import xzf.spiderman.worker.service.SpiderTaskStore;

@Configuration
@AutoConfigureBefore({RedisTemplateConfiguration.class, CuratorAutoConfiguration.class})
public class SpiderMasterConfiguration
{
    @Autowired
    private HessianRedisTemplate hessianRedisTemplate;

    @Autowired
    private CuratorFramework curator;

    @Bean
    public SpiderTaskStore spiderTaskStore()
    {
        return new SpiderTaskStore(hessianRedisTemplate);
    }

    @Bean
    public SpiderMaster spiderMaster(SpiderTaskStore store)
    {
        return new SpiderMaster(store, curator);
    }

}
