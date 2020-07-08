package xzf.spiderman.worker.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisTemplateConfiguration
{
    @Bean
    public HessianRedisTemplate hessianRedisTemplate(RedisConnectionFactory connectionFactory)
    {
        return new HessianRedisTemplate(connectionFactory);
    }

}
