package xzf.spiderman.scheduler.service;

public interface ScheduleLeaderListener
{
    void takeLeadership(ScheduleLeaderManager manager) throws Exception;

    default void onReconnect(ScheduleLeaderManager manager) {}

    default void onFirstConnected(ScheduleLeaderManager manager) {}

    default void onDisconnected(ScheduleLeaderManager manager) {}
}
