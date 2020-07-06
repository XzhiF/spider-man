package xzf.spiderman.common.event;

import java.util.Date;

public interface Event
{
     default Date getOccurOn()
     {
         return new Date();
     }
}
