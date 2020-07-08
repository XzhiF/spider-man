package xzf.spiderman.worker.configuration;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

public class HessianRedisTemplate extends RedisTemplate<String , Object>
{
    public HessianRedisTemplate()
    {
        HessianRedisSerializer hassianSerializer = new HessianRedisSerializer();
        setKeySerializer(RedisSerializer.string());
        setValueSerializer(hassianSerializer);
        setHashKeySerializer(RedisSerializer.string());
        setHashValueSerializer(hassianSerializer);
    }

    public HessianRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

}
