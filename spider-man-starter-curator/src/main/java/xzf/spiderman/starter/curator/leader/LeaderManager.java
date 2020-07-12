package xzf.spiderman.starter.curator.leader;

import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.Participant;

import java.io.Closeable;
import java.util.Collection;

public interface LeaderManager extends Closeable
{
    void start();

    String getId();

    String getPath();

    boolean hasLeadership();

    void addListener(LeaderManagerListener listener);

    Collection<Participant> getParticipants();

    Participant getLeader();
}
