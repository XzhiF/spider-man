package xzf.spiderman.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import xzf.spiderman.common.configuration.EnableSpiderManSession;
import xzf.spiderman.common.configuration.EnableSpiderManExceptionHandler;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"xzf.spiderman.worker.feign"})
@EnableJpaRepositories( value = "xzf.spiderman.scheduler.repository")
@EntityScan(value = "xzf.spiderman.scheduler.entity")
@EnableRedisHttpSession
@EnableSpiderManExceptionHandler
@EnableSpiderManSession
public class SchedulerApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SchedulerApplication.class, args);
    }
}
