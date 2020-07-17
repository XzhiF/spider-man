package xzf.spiderman.worker.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "xzf.spiderman.worker")
@Data
public class WorkerProperties
{
    private SpiderBlockingScheduler spiderBlockingScheduler = new SpiderBlockingScheduler();
    private SpiderSlavePool spiderSlavePool = new SpiderSlavePool();

    @Data
    public class SpiderBlockingScheduler
    {
        private Integer defaultTimeoutSeconds = 60;
    }

    @Data
    public static class SpiderSlavePool
    {
        private Integer poolThreads = 10;
        private Integer coreThreads = 2;
        private Integer keepAliveTimeSeconds = 60;

    }












}
