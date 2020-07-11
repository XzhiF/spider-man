package xzf.spiderman.scheduler.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xzf.spiderman.scheduler.service.*;
import xzf.spiderman.starter.curator.leader.LeaderManager;
import xzf.spiderman.starter.curator.leader.LeaderManagerListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@Configuration
@AutoConfigureAfter(QuartzAutoConfiguration.class)
@Slf4j
public class ScheduleBootstrapConfiguration
{
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheCmdConsumerRunnableFactory scheCmdConsumerRunnableFactory;

    @Autowired
    private CuratorFramework curator;

    @Autowired
    private RedissonClient redisson;

    @Value("${server.port}")
    private Integer port;



    @Bean
    public ScheduleLeaderManager scheduleLeaderManager()
    {
        String id = "";

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            id = inetAddress.getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ScheduleLeaderManager manager = new ScheduleLeaderManager(curator, id);

        manager.addListener(quartzScheduleLeaderListener());
        manager.addListener(scheCmdConsumerScheduleLeaderListener());

        return manager;
    }


    @Bean
    public QuartzCommandLineRunner quartzCommandLineRunner(ScheduleLeaderManager scheduleLeaderManager)
    {
        return new QuartzCommandLineRunner(scheduleLeaderManager);
    }

    public class QuartzCommandLineRunner implements CommandLineRunner
    {
        private ScheduleLeaderManager scheduleLeaderManager;

        public QuartzCommandLineRunner(ScheduleLeaderManager scheduleLeaderManager) {
            this.scheduleLeaderManager = scheduleLeaderManager;
        }

        @Override
        public void run(String... args) throws Exception
        {
            log.info("Init " + SchedulerConst.SCHEDULE_QUEUE_NAME + ", capacity = " + SchedulerConst.SCHEDULE_QUEUE_CAPACITY);
            redisson.getBoundedBlockingQueue(SchedulerConst.SCHEDULE_QUEUE_NAME).trySetCapacity(SchedulerConst.SCHEDULE_QUEUE_CAPACITY);

            log.info("ScheduleLeaderManager is starting..." + LocalDateTime.now().toString());
            scheduleLeaderManager.start();
            log.info("ScheduleLeaderManager is started..."  + LocalDateTime.now().toString());
        }
    }



    public LeaderManagerListener<ScheduleLeaderManager> quartzScheduleLeaderListener()
    {
        return new LeaderManagerListener<ScheduleLeaderManager>()
        {
            @Override
            public void takeLeadership(ScheduleLeaderManager manager) throws Exception {
                log.info("Quartz.ScheduleService ScheduleLeaderListener takeLeadership...");
                try {
                    scheduleService.startup();
                } catch (Exception e) {
                    // TODO ...  xzf handle exception
                    log.error("Quartz.ScheduleService ScheduleLeaderListener takeLeadership error. " + e.getMessage(), e);
                    throw e;
                }
            }

            @Override
            public void onReconnect(ScheduleLeaderManager manager) {
                log.info("Quartz.ScheduleService ScheduleLeaderListener onReconnect...");
                if(manager.hasLeadership()){
                    try {
                        scheduleService.startup();
                    } catch (Exception e) {
                        // TODO ...  xzf handle exception
                        log.error("Quartz.ScheduleService ScheduleLeaderListener onReconnect error. " + e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onDisconnected(ScheduleLeaderManager manager) {
                log.info("Quartz.ScheduleService ScheduleLeaderListener onDisconnected...");
                try {
                    scheduleService.unscheduleAllTasks();
                } catch (Exception e) {
                    log.error("Quartz.ScheduleService ScheduleLeaderListener onDisconnected error. " + e.getMessage(), e);
                }
            }
        };
    }


    public LeaderManagerListener<ScheduleLeaderManager> scheCmdConsumerScheduleLeaderListener()
    {
        ScheCmdConsumerBootstrap bootstrap = new ScheCmdConsumerBootstrap(scheCmdConsumerRunnableFactory);
        return new LeaderManagerListener<ScheduleLeaderManager>(){

            @Override
            public void takeLeadership(ScheduleLeaderManager manager) throws Exception {
                log.info("ScheCmdConsumerBootstrap ScheduleLeaderListener takeLeadership...");
                if(manager.hasLeadership()){
                    try {
                        bootstrap.start();
                    } catch (Exception e) {
                        // TODO ...  xzf handle exception
                        log.error("ScheCmdConsumerBootstrap ScheduleLeaderListener takeLeadership error. " + e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onReconnect(ScheduleLeaderManager manager)
            {
                log.info("ScheCmdConsumerBootstrap ScheduleLeaderListener onReconnect...");
                if(manager.hasLeadership()){
                    try {
                        bootstrap.start();
                    } catch (Exception e) {
                        // TODO ...  xzf handle exception
                        log.error("ScheCmdConsumerBootstrap ScheduleLeaderListener onReconnect error. " + e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onDisconnected(ScheduleLeaderManager manager) {
                log.info("ScheCmdConsumerBootstrap ScheduleLeaderListener onDisconnected...");
                try {
                    bootstrap.stop();
                } catch (Exception e) {
                    log.error("ScheCmdConsumerBootstrap ScheduleLeaderListener onDisconnected error. " + e.getMessage(), e);
                }
            }

            @Override
            public void onClose(ScheduleLeaderManager manager) {
                log.info("ScheCmdConsumerBootstrap ScheduleLeaderListener onClose...");
                try {
                    bootstrap.stop();
                } catch (Exception e) {
                    log.error("ScheCmdConsumerBootstrap ScheduleLeaderListener onClose error. " + e.getMessage(), e);
                }
            }
        };
    }
}
