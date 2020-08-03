package xzf.spiderman.worker.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import xzf.spiderman.common.event.EventPublisher;
import xzf.spiderman.starter.curator.CuratorFacade;
import xzf.spiderman.worker.service.EventPublisherRegistry;
import xzf.spiderman.worker.service.slave.SpiderSlave;
import xzf.spiderman.worker.service.slave.WorkerSpiderFactory;
import xzf.spiderman.worker.service.slave.WorkerSpiderRegistry;
import xzf.spiderman.worker.webmagic.BlockingPollRedisScheduler;

@Configuration
public class SpiderSlaveConfiguration
{
    @Autowired
    private BlockingPollRedisScheduler scheduler;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private WorkerProperties properties;

    @Autowired
    private CuratorFacade curatorFacade;

    @Autowired
    private EventPublisherRegistry eventPublisherRegistry;

    @Bean
    public WorkerSpiderFactory workerSpiderFactory()
    {
        return new WorkerSpiderFactory(scheduler,kafkaTemplate, properties);
    }



    @Bean
    public SpiderSlave spiderSlave(WorkerSpiderFactory factory)
    {
        return new SpiderSlave(curatorFacade, factory,eventPublisherRegistry, properties);
    }


}
