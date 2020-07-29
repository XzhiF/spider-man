package xzf.spiderman.common.configuration.webflux;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

@Configuration
public class SpiderManWebFluxCookieConfiguration
{
    @Value("${xzf.spiderman.common.cookie.domain:localhost}")
    private String domain;


    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.setCookieName("SESSION"); // <1>
        resolver.addCookieInitializer((builder) -> builder.path("/")); // <2>
//        resolver.addCookieInitializer((builder) -> builder.sameSite("Strict")); // <3>
        resolver.addCookieInitializer((builder) -> builder.domain(domain)); // <3>
        return resolver;
    }


//    @Bean
//    @Qualifier("springSessionDefaultRedisSerializer")
//    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
//        return new GenericJackson2JsonRedisSerializer(objectMapper());
//    }
//
//    private ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper;
//    }


}
