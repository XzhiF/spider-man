package xzf.spiderman.scheduler.service.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
//@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DebugJob implements Job
{

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {

        log.info("DebugJob start..." + LocalDateTime.now().toString());

        log.info("fireTime="+context.getFireTime());

//        if(!context.getJobDetail().getJobDataMap().containsKey("count")){
//            context.getJobDetail().getJobDataMap().put("count", 0);
//            System.out.println("no contains...");
//        }
//
//        int count = context.getJobDetail().getJobDataMap().getInt("count");
//        System.out.println("count="+count);
//        count++;
//        context.getJobDetail().getJobDataMap().put("count", count);



        context.getJobDetail().getJobDataMap().forEach((k,v)->{
            log.info("jobdatamap -- key="+k+", value="+v);
        });

        log.info("DebugJob end..." + LocalDateTime.now().toString());

        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
