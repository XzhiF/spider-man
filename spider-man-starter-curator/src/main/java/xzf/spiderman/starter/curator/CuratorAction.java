package xzf.spiderman.starter.curator;

import org.apache.curator.framework.CuratorFramework;

public interface CuratorAction
{
    void execute(CuratorFramework curator) throws Exception;
}
