package xzf.spiderman.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


@Slf4j
public class ScheduleLeaderManagerImpl implements LeaderSelectorListener, ScheduleLeaderManager, ApplicationListener<ContextClosedEvent>
{
    public static final String PATH = "/scheduler/leader-selector";
    private final Semaphore semaphore = new Semaphore(0);
    private final LeaderSelector leaderSelector;

    private final List<ScheduleLeaderListener> listeners = new ArrayList<>();

    public ScheduleLeaderManagerImpl(CuratorFramework client, String id)
    {
        this.leaderSelector = new LeaderSelector(client, PATH, this);
        this.leaderSelector.setId(id);
        this.leaderSelector.autoRequeue();
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception
    {
        log.info("ScheduleLeaderManager " + getId()+ ": takeLeadership");
        for (ScheduleLeaderListener listener : listeners) {
            listener.takeLeadership(this);
        }
        semaphore.acquire();
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState)
    {
        switch (newState)
        {
            case CONNECTED:
                listeners.forEach(l->l.onFirstConnected(this));
                break;
            case RECONNECTED:
                listeners.forEach(l->l.onReconnect(this));
                break;
            case LOST:
            case SUSPENDED:
            case READ_ONLY:
                try {
                    listeners.forEach(l -> l.onDisconnected(this));
                }finally {
                    semaphore.release();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void start()
    {
        leaderSelector.start();
    }

    @Override
    public String getId()
    {
        return leaderSelector.getId();
    }

    @Override
    public boolean hasLeadership()
    {
        return leaderSelector.hasLeadership();
    }

    @Override
    public void addListener(ScheduleLeaderListener listener) {
        listeners.add(listener);
    }

    @Override
    public void close() throws IOException
    {
        try {
            listeners.forEach(l->l.onClose(this));
            leaderSelector.close();
        }finally {
            semaphore.release();
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            close();
        } catch (IOException ignore) {
        }
    }
}
