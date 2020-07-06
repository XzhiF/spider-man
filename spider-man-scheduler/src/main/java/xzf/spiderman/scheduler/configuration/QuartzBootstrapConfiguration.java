package xzf.spiderman.scheduler.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xzf.spiderman.scheduler.service.ScheduleLeaderListener;
import xzf.spiderman.scheduler.service.ScheduleLeaderManager;
import xzf.spiderman.scheduler.service.ScheduleLeaderManagerImpl;
import xzf.spiderman.scheduler.service.ScheduleService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@Configuration
@AutoConfigureAfter(QuartzAutoConfiguration.class)
@Slf4j
public class QuartzBootstrapConfiguration
{
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private CuratorFramework curator;

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
        ScheduleLeaderManager manager = new ScheduleLeaderManagerImpl(curator, id);
        manager.addListener(defaultScheduleLeaderListener());
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
            log.info("ScheduleLeaderManager is starting..." + LocalDateTime.now().toString());
            scheduleLeaderManager.start();
            log.info("ScheduleLeaderManager is started..."  + LocalDateTime.now().toString());
        }
    }



    public ScheduleLeaderListener defaultScheduleLeaderListener()
    {
        return new ScheduleLeaderListener(){
            @Override
            public void takeLeadership(ScheduleLeaderManager manager) throws Exception {
                log.info("ScheduleLeaderListener takeLeadership...");
                try {
                    scheduleService.startup();
                } catch (SchedulerException e) {
                    // TODO ...  xzf handle exception
                    log.error("ScheduleLeaderListener takeLeadership error. " + e.getMessage(), e);
                    throw e;
                }
            }

            @Override
            public void onReconnect(ScheduleLeaderManager manager) {
                log.info("ScheduleLeaderListener onReconnect...");
                if(manager.hasLeadership()){
                    try {
                        scheduleService.startup();
                    } catch (SchedulerException e) {
                        // TODO ...  xzf handle exception
                        log.error("ScheduleLeaderListener onReconnect error. " + e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onDisconnected(ScheduleLeaderManager manager) {
                log.info("ScheduleLeaderListener onDisconnected...");
                try {
                    scheduleService.unscheduleAllTasks();
                } catch (SchedulerException e) {
                    log.error("ScheduleLeaderListener onDisconnected error. " + e.getMessage(), e);
                }
            }
        };
    }

}
