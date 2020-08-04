package xzf.spiderman.starter.swagger2.gateway;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ConditionalOnProperty(value = "xzf.spiderman.swagger2.gateway.enabled", havingValue = "true")
@ConditionalOnBean({RouteLocator.class,GatewayProperties.class})
@Configuration
@AutoConfigureAfter(value = GatewayAutoConfiguration.class)
public class Swagger2GatewayAutoConfiguration
{
    @Primary
    @Bean
    public Swagger2Provider swagger2Provider(
            ObjectProvider<RouteLocator> routeLocatorProvider,
            ObjectProvider<GatewayProperties> gatewayPropertiesProvider)
    {
        return new Swagger2Provider(routeLocatorProvider.getIfUnique(),gatewayPropertiesProvider.getIfUnique());
    }

    @Bean
    public Swagger2Handler swagger2Handler(Swagger2Provider swagger2Provider)
    {
        return new Swagger2Handler(swagger2Provider);
    }

}
