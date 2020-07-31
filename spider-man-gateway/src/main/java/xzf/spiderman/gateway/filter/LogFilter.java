package xzf.spiderman.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import xzf.spiderman.admin.data.SessionAdminUser;

@Component
@Slf4j
public class LogFilter implements GlobalFilter , Ordered
{
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        if (log.isInfoEnabled()) {
            log.info("request uri= " + exchange.getRequest().getURI());
        }

        return exchange.getSession().flatMap(webSession -> {
            System.out.println("session_id = " + webSession.getId());
            System.out.println("pre get attr user= " + webSession.getAttribute(SessionAdminUser.SESSION_KEY));
            return chain.filter(exchange);
        });

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
