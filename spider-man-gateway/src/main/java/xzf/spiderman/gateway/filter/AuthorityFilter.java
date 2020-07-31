package xzf.spiderman.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xzf.spiderman.admin.data.SessionAdminUser;
import xzf.spiderman.common.Ret;
import xzf.spiderman.common.exception.AuthorityException;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.common.exception.UnknownException;
import xzf.spiderman.gateway.configuration.GatewayProperties;

@Component
@Slf4j
public class AuthorityFilter implements GlobalFilter, Ordered
{
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GatewayProperties properties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        return exchange.getSession().flatMap(webSession -> {

            String path = exchange.getRequest().getURI().getPath();
            path = path.substring("/api".length());

            if(properties.getSkipUrls().contains(path))  {
                return chain.filter(exchange);
            }

            if(webSession.getAttribute(SessionAdminUser.SESSION_KEY) == null){
                return errorResponse(exchange, Ret.fail(new AuthorityException("用户未登录。")));
            }

            SessionAdminUser sessionAdminUser = webSession.getAttribute(SessionAdminUser.SESSION_KEY);
            if(sessionAdminUser.getAuthorities().contains("*"))
            {
                return chain.filter(exchange);
            }

            PathMatcher matcher = new AntPathMatcher();
            for (String authority : sessionAdminUser.getAuthorities()) {
                if(matcher.match(authority, path)){
                    return chain.filter(exchange);
                }
            }

            return errorResponse(exchange, Ret.fail(new AuthorityException("用户无访问权限。")));
        });
    }

    @Override
    public int getOrder() {
        return 0;
    }


    private Mono<Void> errorResponse(ServerWebExchange exchange, Ret<?> ret)
    {
        try {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            byte[] bytes = objectMapper.writer().writeValueAsBytes(ret);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        }catch (Exception e){
            log.error("errorResponse失败."+e.getMessage(),e);
            throw new UnknownException("未知异常。"+e.getMessage(),e);
        }
    }
}
