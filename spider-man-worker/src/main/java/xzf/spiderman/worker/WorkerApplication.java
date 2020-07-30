package xzf.spiderman.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import xzf.spiderman.common.configuration.EnableSpiderManSession;
import xzf.spiderman.common.configuration.EnableSpiderManExceptionHandler;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(value = "xzf.spiderman.scheduler.feign")
@EnableJpaRepositories( value = "xzf.spiderman.worker.repository")
@EntityScan(value = "xzf.spiderman.worker.entity")
@EnableSpiderManExceptionHandler
@EnableSpiderManSession
public class WorkerApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(WorkerApplication.class, args);
    }
}
