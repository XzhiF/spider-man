package xzf.spiderman.worker.service;

import us.codecraft.webmagic.processor.PageProcessor;
import xzf.spiderman.common.exception.ConfigNotValidException;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.webmagic.ParamProcessor;
import xzf.spiderman.worker.webmagic.SpiderParams;

import java.lang.reflect.Constructor;

public class ProcessorFactory
{
    public PageProcessor create(SpiderCnf cnf)
    {
        try
        {
            String processorClassName  = cnf.getProcessor();
            Class<? extends ParamProcessor> clazz = (Class<? extends ParamProcessor>) Class.forName(processorClassName);
            Constructor<? extends ParamProcessor> constructor = clazz.getConstructor(SpiderParams.class);
            SpiderParams params = SpiderParams.parse(cnf.getParams());
            ParamProcessor o = constructor.newInstance(params);
            return o;
        } catch (Exception e) {
            throw new ConfigNotValidException("无法创建"+cnf.getProcessor()+", "+e.getMessage(),e);
        }
    }
}
