package xzf.spiderman.worker.service.master;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import xzf.spiderman.starter.curator.leader.DefaultLeaderManager;

import java.io.IOException;

public class MasterLeaderManager extends DefaultLeaderManager implements ApplicationListener<ContextClosedEvent>
{
    public static final String PATH = "/worker/leader-selector";

    public MasterLeaderManager(CuratorFramework client, String id) {
        super(client, PATH, id, true);
    }


    @Override
    public void onApplicationEvent(ContextClosedEvent event)
    {
        try {
            super.close();
        } catch (IOException e) {

        }
    }
}
