package xzf.spiderman.storage.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xzf.spiderman.storage.mq.MongoConsumer;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfiguration
{

    @Bean
    @ConditionalOnProperty(value = "xzf.spiderman.storage.mongo.enabled", havingValue = "true")
    public MongoConsumer mongoConsumer()
    {
        System.out.println("---------MongoConsumer=被激活");
        return new MongoConsumer();
    }


}
