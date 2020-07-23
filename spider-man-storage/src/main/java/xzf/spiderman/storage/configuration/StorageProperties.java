package xzf.spiderman.storage.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = "xzf.spiderman.storage")
public class StorageProperties
{
    private ConsumerThreadPool consumerThreadPool = new ConsumerThreadPool();
    private Integer consumerConcurrency = 2;




    @Data
    public class ConsumerThreadPool
    {
        private int corePoolSize = 2;
        private int maximumPoolSize = 10;
        private long keepAliveTimeSeconds;
        private int workQueue = 0;


    }




}
