package xzf.spiderman.gateway.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

@Configuration
public class SessionConfiguration
{
    @Value("${xzf.spiderman.common.cookie.domain:localhost}")
    private String domain;

    @Bean
    public WebSessionIdResolver webSessionIdResolver()
    {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.setCookieName("SESSION");
        resolver.addCookieInitializer(initializer->initializer.domain(this.domain));
        resolver.addCookieInitializer(initializer->initializer.path("/"));
        return resolver;
    }

    @Bean
    @Qualifier("springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
        return mapper;
    }



}
