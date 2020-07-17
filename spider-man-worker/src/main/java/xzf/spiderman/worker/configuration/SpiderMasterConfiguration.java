package xzf.spiderman.worker.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xzf.spiderman.starter.curator.CuratorAutoConfiguration;
import xzf.spiderman.starter.curator.CuratorFacade;
import xzf.spiderman.worker.service.SpiderMaster;
import xzf.spiderman.worker.service.SpiderQueueManager;
import xzf.spiderman.worker.service.SpiderTaskRepository;
import xzf.spiderman.worker.webmagic.BlockingPollRedisScheduler;

@Configuration
@AutoConfigureBefore({RedisTemplateConfiguration.class, CuratorAutoConfiguration.class})
public class SpiderMasterConfiguration
{
    @Autowired
    private HessianRedisTemplate hessianRedisTemplate;

    @Autowired
    private CuratorFacade curatorFacade;

    @Bean
    public SpiderTaskRepository spiderTaskStore()
    {
        return new SpiderTaskRepository(hessianRedisTemplate);
    }

    @Bean
    public SpiderQueueManager spiderQueueSender(BlockingPollRedisScheduler scheduler)
    {
        return new SpiderQueueManager(scheduler);
    }

    @Bean
    public SpiderMaster spiderMaster(SpiderTaskRepository store, SpiderQueueManager sender)
    {
        return new SpiderMaster(store, curatorFacade,sender);
    }

}
