package xzf.spiderman.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LogFilter implements GlobalFilter
{
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        if (log.isInfoEnabled()) {
            log.info("request uri= " + exchange.getRequest().getURI());
        }

        return exchange.getSession().flatMap(webSession -> {
            System.out.println("session_id = " + webSession.getId());
            System.out.println("pre get attr v= " + webSession.getAttribute("v"));
            return chain.filter(exchange);
        });

    }
}
