package xzf.spiderman.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import xzf.spiderman.common.configuration.EnableSpiderManSession;
import xzf.spiderman.common.configuration.EnableSpiderManExceptionHandler;

@SpringBootApplication
@EnableRedisHttpSession
@EnableSpiderManExceptionHandler
@EnableSpiderManSession
@EnableJpaRepositories( value = "xzf.spiderman.admin.repository")
@EntityScan(basePackages = "xzf.spiderman.admin.entity")
public class AdminApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(AdminApplication.class, args);
    }
}
