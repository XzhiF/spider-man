package xzf.spiderman.common.event;

public interface EventListener
{
    boolean supportEventType(Class<? extends Event> clazz);

    void onEvent(Event event);
}
