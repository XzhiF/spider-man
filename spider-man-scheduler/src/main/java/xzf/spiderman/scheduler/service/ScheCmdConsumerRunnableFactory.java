package xzf.spiderman.scheduler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ScheCmdConsumerRunnableFactory
{
    @Value("${xzf.spiderman.scheduler.sche-cmd.consumer-thread-size:2}")
    private Integer consumerThreadSize = 2;

    @Autowired
    private ApplicationServiceRegistry registry;


    public List<ScheCmdConsumerRunnable> create()
    {
        List<ScheCmdConsumerRunnable> results = new ArrayList<>();

        for(int i=0; i<consumerThreadSize ; i++)
        {
            results.add(new ScheCmdConsumerRunnable(registry));
        }
        return results;
    }

}
