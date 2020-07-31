package xzf.spiderman.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import xzf.spiderman.common.configuration.EnableSpiderManExceptionHandler;
import xzf.spiderman.gateway.configuration.GatewayProperties;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRedisWebSession
@EnableConfigurationProperties(GatewayProperties.class)
public class GateWayApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(GateWayApplication.class,args);
    }
}
