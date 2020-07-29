package xzf.spiderman.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import xzf.spiderman.common.configuration.webflux.EnableSpiderManWebFluxCookie;

@SpringBootApplication
@EnableDiscoveryClient
@EnableSpiderManWebFluxCookie
public class GateWayApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(GateWayApplication.class,args);
    }
}
