package xzf.spiderman.starter.curator.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


public class DefaultLeaderManager implements LeaderSelectorListener, LeaderManager
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String DEFAULT_PATH = "/curator/leader-manager";

    private final Semaphore semaphore = new Semaphore(0);
    private final LeaderSelector leaderSelector;
    private final String path;

    private final List<LeaderManagerListener<LeaderManager>> listeners = new ArrayList<>();

    public DefaultLeaderManager(CuratorFramework client, String path, String id, boolean autoRequeue)
    {
        this.path = path == null ? DEFAULT_PATH : path;
        this.leaderSelector = new LeaderSelector(client, path, this);
        this.leaderSelector.setId(id);
        if(autoRequeue) {
            this.leaderSelector.autoRequeue();
        }
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception
    {
        log.info(this.getClass().getSimpleName() + " - " + getId()+ ": takeLeadership");
        for (LeaderManagerListener listener : listeners) {
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
    public String getPath() {
        return path;
    }

    @Override
    public boolean hasLeadership()
    {
        return leaderSelector.hasLeadership();
    }

    @Override
    public void addListener(LeaderManagerListener listener) {
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

}
