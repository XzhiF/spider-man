package xzf.spiderman.gateway.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "xzf.spiderman.gateway")
public class GatewayProperties
{
    private List<String> skipUrls = new ArrayList();




}
