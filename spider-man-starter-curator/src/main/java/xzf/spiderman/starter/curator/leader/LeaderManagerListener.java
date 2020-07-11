package xzf.spiderman.starter.curator.leader;

public interface LeaderManagerListener<T extends LeaderManager>
{
    void takeLeadership(T manager) throws Exception;

    default void onReconnect(T manager) {}

    default void onFirstConnected(T manager) {}

    default void onDisconnected(T manager) {}

    default void onClose(T manager) {}
}
