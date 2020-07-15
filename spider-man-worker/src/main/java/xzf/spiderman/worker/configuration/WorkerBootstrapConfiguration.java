package xzf.spiderman.worker.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xzf.spiderman.starter.curator.leader.LeaderManager;
import xzf.spiderman.starter.curator.leader.LeaderManagerListener;
import xzf.spiderman.worker.service.MasterLeaderManager;
import xzf.spiderman.worker.service.SpiderMasterService;
import xzf.spiderman.worker.service.SpiderTaskRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@Configuration
@AutoConfigureAfter(QuartzAutoConfiguration.class)
@Slf4j
public class WorkerBootstrapConfiguration
{
    @Value("${server.port:8080}")
    private Integer port;

    @Autowired
    private CuratorFramework curator;

    @Autowired
    private SpiderMasterService spiderMasterService;

    @Autowired
    private SpiderTaskRepository spiderTaskStore;


    @Bean
    public MasterLeaderManager bossLeaderManager()
    {
        String id = "";

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            id = inetAddress.getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        MasterLeaderManager manager = new MasterLeaderManager(curator, id);

        manager.addListener(new LeaderManagerListener() {
            @Override
            public void takeLeadership(LeaderManager manager) throws Exception {
                System.out.println("boss" + manager.getId()+", takeLeadership");

                // TODO ...
                spiderMasterService.initSpiderWorkspace();
                spiderTaskStore.sync();
            }
        });

        return manager;
    }

    @Bean
    public BossCommandLineRunner bossCommandLineRunner(MasterLeaderManager masterLeaderManager)
    {
        return new BossCommandLineRunner(masterLeaderManager);
    }


    public class BossCommandLineRunner implements CommandLineRunner
    {
        private MasterLeaderManager masterLeaderManager;

        public BossCommandLineRunner(MasterLeaderManager masterLeaderManager) {
            this.masterLeaderManager = masterLeaderManager;
        }

        @Override
        public void run(String... args) throws Exception {

            log.info("BossLeaderManager is starting..." + LocalDateTime.now().toString());
            masterLeaderManager.start();
            log.info("BossLeaderManager is started..." + LocalDateTime.now().toString());
        }
    }

}
