package xzf.spiderman.worker.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WorkerProperties.class)
public class WorkerConfiguration
{
}
