package xzf.spiderman.worker.configuration;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xzf.spiderman.starter.curator.CuratorAutoConfiguration;
import xzf.spiderman.worker.service.SpiderMaster;
import xzf.spiderman.worker.service.SpiderTaskRepository;

@Configuration
@AutoConfigureBefore({RedisTemplateConfiguration.class, CuratorAutoConfiguration.class})
public class SpiderMasterConfiguration
{
    @Autowired
    private HessianRedisTemplate hessianRedisTemplate;

    @Autowired
    private CuratorFramework curator;

    @Bean
    public SpiderTaskRepository spiderTaskStore()
    {
        return new SpiderTaskRepository(hessianRedisTemplate);
    }

    @Bean
    public SpiderMaster spiderMaster(SpiderTaskRepository store)
    {
        return new SpiderMaster(store, curator);
    }

}
