package xzf.spiderman.starter.curator;

import org.apache.curator.framework.CuratorFramework;

public interface CuratorCallback<T>
{
    T call(CuratorFramework curator) throws Exception;

}
