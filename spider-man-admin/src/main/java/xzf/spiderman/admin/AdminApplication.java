package xzf.spiderman.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import xzf.spiderman.common.configuration.web.EnableSpiderManCookie;
import xzf.spiderman.common.configuration.web.EnableSpiderManExceptionHandler;

@SpringBootApplication
@EnableRedisHttpSession
@EnableSpiderManExceptionHandler
@EnableSpiderManCookie
public class AdminApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(AdminApplication.class, args);
    }
}
