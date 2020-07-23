package xzf.spiderman.worker.service.slave;

import us.codecraft.webmagic.processor.PageProcessor;
import xzf.spiderman.common.exception.ConfigNotValidException;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.webmagic.GroovyProcessor;
import xzf.spiderman.worker.webmagic.ContextProcessor;
import xzf.spiderman.worker.webmagic.ProcessorContext;
import xzf.spiderman.worker.webmagic.SpiderParams;

import java.lang.reflect.Constructor;

public class ProcessorFactory
{
    public PageProcessor create(SpiderCnf cnf)
    {
        try
        {
            String processorClassName  = cnf.getProcessor();
            if("groovy".equals(processorClassName)){
                processorClassName = GroovyProcessor.class.getName();
            }

            Class<? extends ContextProcessor> clazz = (Class<? extends ContextProcessor>) Class.forName(processorClassName);
            Constructor<? extends ContextProcessor> constructor = clazz.getConstructor(ProcessorContext.class);

            SpiderParams params = SpiderParams.parse(cnf.getParams());
            ProcessorContext context = new ProcessorContext(params);

            ContextProcessor o = constructor.newInstance(context);

            return o;
        } catch (Exception e) {
            throw new ConfigNotValidException("无法创建"+cnf.getProcessor()+", "+e.getMessage(),e);
        }
    }
}
