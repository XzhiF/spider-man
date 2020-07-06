package xzf.spiderman.starter.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.Phased;
import org.springframework.context.SmartLifecycle;

public class CuratorFrameworkFactoryBean implements FactoryBean<CuratorFramework>, SmartLifecycle, Phased
{
    private CuratorProperties properties;
    private CuratorRetryPolicyProvider curatorRetryPolicyProvider;
    private CuratorFramework curatorFramework;

    public CuratorFrameworkFactoryBean(CuratorProperties properties,
                                       CuratorRetryPolicyProvider curatorRetryPolicyProvider)
    {
        this.properties = properties;
        this.curatorRetryPolicyProvider = curatorRetryPolicyProvider;
    }

    public CuratorFramework getObject() throws Exception
    {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        builder.connectString(properties.getConnectionString());
        builder.connectionTimeoutMs(properties.getConnectionTimeoutMs());
        builder.sessionTimeoutMs(properties.getSessionTimeoutMs());

        if(curatorRetryPolicyProvider != null){
            builder.retryPolicy(curatorRetryPolicyProvider.get());
        }else{
            // these are reasonable arguments for the ExponentialBackoffRetry. The first
            // retry will wait 1 second - the second will wait up to 2 seconds - the
            // third will wait up to 4 seconds.
            ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
            builder.retryPolicy(retryPolicy);
        }
        curatorFramework =  builder.build();
        return curatorFramework;
    }


    @Override
    public boolean isAutoStartup() {
        return properties.isAutoStartup();
    }

    public Class<?> getObjectType()
    {
        return CuratorFramework.class;
    }

    public void start() {
        curatorFramework.start();
    }

    public void stop() {
        curatorFramework.close();
    }

    public boolean isRunning() {
        return curatorFramework.getState() == CuratorFrameworkState.STARTED;
    }

    public int getPhase()
    {
        return Integer.MAX_VALUE;
    }
}
