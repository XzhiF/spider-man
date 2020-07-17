package xzf.spiderman.worker.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xzf.spiderman.worker.webmagic.BlockingPollRedisScheduler;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(WorkerProperties.class)
public class WorkerConfiguration
{
    @Autowired
    private WorkerProperties properties;
    @Autowired
    private HessianRedisTemplate redisTemplate;

    @Bean
    public BlockingPollRedisScheduler blockingPollRedisScheduler()
    {
        return new BlockingPollRedisScheduler(redisTemplate,
                properties.getSpiderBlockingScheduler().getDefaultTimeoutSeconds(),
                TimeUnit.SECONDS);
    }

}
