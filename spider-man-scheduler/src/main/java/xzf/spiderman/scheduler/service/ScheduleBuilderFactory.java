package xzf.spiderman.scheduler.service;

import com.alibaba.fastjson.JSONObject;
import org.quartz.CronScheduleBuilder;
import org.quartz.ScheduleBuilder;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.scheduler.entity.Task;

public class ScheduleBuilderFactory
{

    public static ScheduleBuilder<?> create(Task task)
    {
        if(task.getScheduleClass().equals(CronScheduleBuilder.class.getName()))
        {
            return createCronScheduleBuilder(task);
        }

        throw new BizException("创建调度器失败。Task.ScheduleClass :"+task.getScheduleClass()+", 未能解释。");
    }

    private static ScheduleBuilder<?> createCronScheduleBuilder(Task task)
    {
        CronScheduleBuilderProps props = JSONObject.parseObject(task.getScheduleProps(), CronScheduleBuilderProps.class);
        return CronScheduleBuilder.cronSchedule(props.getCron());
    }


}
