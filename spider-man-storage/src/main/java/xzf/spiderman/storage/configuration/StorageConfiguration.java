package xzf.spiderman.storage.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import xzf.spiderman.storage.mq.MongoConsumer;

import java.util.concurrent.ExecutorService;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfiguration
{
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private StorageProperties properties;

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(value = "xzf.spiderman.storage.mongo.enabled", havingValue = "true")
    public MongoConsumer mongoConsumer()
    {
        System.out.println("---------MongoConsumer=被激活");
        return new MongoConsumer(redisTemplate, properties);
    }


}
