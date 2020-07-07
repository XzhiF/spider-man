package xzf.spiderman.common.event;

import xzf.spiderman.common.exception.BizException;

import java.util.ArrayList;
import java.util.List;

public class EventPublisher
{
    private List<EventListener> listeners = new ArrayList<>();

    public EventPublisher subscribe(EventListener listener)
    {
        if(listeners.contains(listener)){
            return this;
        }

        this.listeners.add(listener);
        return this;
    }


    public EventPublisher subscribe(EventListener listener, boolean allowDuplicate)
    {
        if(listeners.contains(listener) && !allowDuplicate){
            return this;
        }

        this.listeners.add(listener);
        return this;
    }

    public EventPublisher subscribe(Class<? extends  Event> clazz, EventListener listener)
    {
        return subscribe(clazz, listener, false);
    }



    public EventPublisher subscribe(Class<? extends  Event> clazz, EventListener listener, boolean allowDuplicate)
    {
        if(!listener.supportEventType(clazz)){
            throw new BizException("Listener"+listener.getClass().getSimpleName()+", 不支持事件类型:"+ clazz.getSimpleName() );
        }

        return subscribe(listener, allowDuplicate);
    }



    public void publish(Event event)
    {
        for (EventListener listener : this.listeners)
        {
            if (listener.supportEventType(event.getClass()))
            {
                listener.onEvent(event);
            }
        }
    }




}
