package xzf.spiderman.scheduler.service;

import java.io.Closeable;

public interface ScheduleLeaderManager extends Closeable
{
    void start();

    String getId();

    boolean hasLeadership();

    void addListener(ScheduleLeaderListener listener);
}
