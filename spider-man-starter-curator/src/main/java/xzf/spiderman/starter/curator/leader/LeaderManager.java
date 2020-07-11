package xzf.spiderman.starter.curator.leader;

import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;

import java.io.Closeable;

public interface LeaderManager extends Closeable
{
    void start();

    String getId();

    String getPath();

    boolean hasLeadership();

    void addListener(LeaderManagerListener listener);
}
