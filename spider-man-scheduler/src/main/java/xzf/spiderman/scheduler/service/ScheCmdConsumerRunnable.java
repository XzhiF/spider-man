package xzf.spiderman.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBoundedBlockingQueue;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.common.exception.RetCodeException;
import xzf.spiderman.scheduler.configuration.SchedulerConst;
import xzf.spiderman.scheduler.data.ScheCmd;
import static xzf.spiderman.scheduler.data.ScheCmd.*;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScheCmdConsumerRunnable implements Runnable
{
    private ApplicationServiceRegistry registry;
    private RBoundedBlockingQueue<ScheCmd> queue;

    private Map<Integer, CmdHandler> cmdHandlers = new HashMap<>();

    private interface CmdHandler
    {
        void handle(ScheCmd cmd);
    }

    public ScheCmdConsumerRunnable(ApplicationServiceRegistry registry)
    {
        this.registry = registry;
        this.queue = registry.redissonQueueProvider().scheCmdQueue();
        this.registerCmdHandlers();
    }

    private void registerCmdHandlers()
    {
        cmdHandlers.put(IDLE, new IdleCmdHandler());
        cmdHandlers.put(ENABLE, new EnableCmdHandler());
        cmdHandlers.put(DISABLE, new DisableCmdHandler());
        cmdHandlers.put(SCHEDULE, new ScheduleCmdHandler());
        cmdHandlers.put(UNSCHEDULE, new UnScheduleCmdHandler());
        cmdHandlers.put(TRIGGER, new TriggerCmdHandler());
    }

    @Override
    public void run()
    {
        while (true)
        {
            if(Thread.interrupted()){
                log.info("ScheCmdConsumerRunnable done . currentThread="+Thread.currentThread());
                break;
            }

            try
            {
                ScheCmd cmd = queue.poll(3, TimeUnit.MINUTES);

                if(cmd != null) {
                    execCmd(cmd);
                } else {
                    log.info("ScheCmdConsumer, 5秒钟未有任务消费。"+SchedulerConst.SCHEDULE_QUEUE_NAME);
                }
            }
            catch (InterruptedException e) {

                if(!Thread.interrupted()){
                    log.error("消费队列被中断,请检查Redis或网络是否异常" + e.getMessage(), e);

                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException interruptedException) {}
                }
            }
            catch (Exception e){
                handleUnknownException(e);
            }

        }
    }

    private void handleUnknownException(Exception e) {
        log.error("发生未知异常."+e.getMessage(), e);
        // ...
    }


    private void execCmd(ScheCmd cmd)
    {
        try {
            log.info("exec cmd : taskid=" + cmd.getTaskId()+", action="+cmd.getAction());

            CmdHandler cmdHandler = cmdHandlers.get(cmd.getAction());
            if(cmdHandler == null){
                throw new BizException("消费ScheCmd失败.action="+cmd.getAction()+", 不支持.");
            }

            cmdHandler.handle(cmd);

        }
        catch (Exception e)
        {
            handleExecException(e);
        }
    }

    private void handleExecException(Exception e) {
        log.error("execCmd失败."+e.getMessage(), e);
        // putError Result
    }


    // ---

    private class EnableCmdHandler implements CmdHandler
    {
        @Override
        public void handle(ScheCmd cmd) {
            registry.taskService().enable(cmd.getTaskId());
        }
    }

    private class DisableCmdHandler implements CmdHandler
    {
        @Override
        public void handle(ScheCmd cmd) {
            registry.taskService().disable(cmd.getTaskId());
        }
    }


    private class ScheduleCmdHandler implements CmdHandler
    {
        @Override
        public void handle(ScheCmd cmd) {
            registry.scheduleService().scheduleTask(cmd.getTaskId());
        }
    }

    private class UnScheduleCmdHandler implements CmdHandler
    {
        @Override
        public void handle(ScheCmd cmd) {
            registry.scheduleService().unscheduleTask(cmd.getTaskId());
        }
    }

    private class TriggerCmdHandler implements CmdHandler
    {
        @Override
        public void handle(ScheCmd cmd) {
            registry.scheduleService().triggerTask(cmd.getTaskId());
        }
    }

    private class IdleCmdHandler implements CmdHandler
    {
        @Override
        public void handle(ScheCmd cmd) {
            log.info("IdleCmdHandler... Heartbeat..");
        }
    }


}
