package xzf.spiderman.starter.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import xzf.spiderman.common.exception.ThirdPartyInvokeException;

@Slf4j
public class CuratorFacade
{

    private CuratorFramework curator;

    public CuratorFramework getCurator() {
        return curator;
    }

    public CuratorFacade(CuratorFramework curator) {
        this.curator = curator;
    }


    public void execute(CuratorAction action)
    {
        try {
            action.execute(curator);
        } catch (Exception e) {
            log.error("调用Curator方法失败。"+ e.getMessage());
            throw new ThirdPartyInvokeException("调用Curator方法失败。"+ e.getMessage(), e);
        }
    }

    public <T> T submit(CuratorCallback<T> callback)
    {
        try {
            return callback.call(curator);
        } catch (Exception e) {
            log.error("调用Curator方法失败。"+ e.getMessage());
            throw new ThirdPartyInvokeException("调用Curator方法失败。"+ e.getMessage(), e);
        }
    }

}
