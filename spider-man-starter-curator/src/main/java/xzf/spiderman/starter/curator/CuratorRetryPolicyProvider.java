package xzf.spiderman.starter.curator;

import org.apache.curator.RetryPolicy;

public interface CuratorRetryPolicyProvider
{
    RetryPolicy get();
}
