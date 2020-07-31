package xzf.spiderman.common.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@Configuration
public class SpiderManSessionConfiguration implements BeanClassLoaderAware
{
    @Value("${xzf.spiderman.common.cookie.domain:localhost}")
    private String domain;

    @Bean
    public CookieSerializer cookieSerializer()
    {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("SESSION");
        serializer.setCookiePath("/");
        serializer.setDomainName(domain);
        serializer.setUseBase64Encoding(false);
        return serializer;
    }

    private ClassLoader loader;

    @Bean
    @Qualifier("springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//        mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
        return mapper;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.loader = classLoader;
    }


}
