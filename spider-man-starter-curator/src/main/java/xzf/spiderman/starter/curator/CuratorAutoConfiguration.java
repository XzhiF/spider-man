package xzf.spiderman.starter.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ZooKeeper.class, CuratorFramework.class})
@EnableConfigurationProperties(CuratorProperties.class)
public class CuratorAutoConfiguration
{
    @Autowired(required = false)
    private CuratorRetryPolicyProvider curatorRetryPolicyProvider;

    @Autowired
    private CuratorProperties properties;

    @Bean
    @ConditionalOnMissingBean(CuratorFramework.class)
    public CuratorFrameworkFactoryBean curatorFramework()
    {
        return new CuratorFrameworkFactoryBean(properties, curatorRetryPolicyProvider);
    }

}
