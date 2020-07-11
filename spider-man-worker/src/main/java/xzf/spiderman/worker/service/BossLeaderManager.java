package xzf.spiderman.worker.service;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import xzf.spiderman.starter.curator.leader.DefaultLeaderManager;

import java.io.IOException;

public class BossLeaderManager extends DefaultLeaderManager implements ApplicationListener<ContextClosedEvent>
{
    public static final String PATH = "/workder/leader-selector";

    public BossLeaderManager(CuratorFramework client, String id) {
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
