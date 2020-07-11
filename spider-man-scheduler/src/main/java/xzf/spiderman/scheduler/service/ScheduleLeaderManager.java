package xzf.spiderman.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import xzf.spiderman.starter.curator.leader.DefaultLeaderManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


@Slf4j
public class ScheduleLeaderManager extends DefaultLeaderManager implements  ApplicationListener<ContextClosedEvent>
{
    public static final String PATH = "/scheduler/leader-selector";

    public ScheduleLeaderManager(CuratorFramework client, String id)
    {
        super(client, PATH, id, true);
    }


    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            super.close();
        } catch (IOException ignore) {
        }
    }
}
