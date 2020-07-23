package xzf.spiderman.worker.webmagic;

import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 爬虫需要SpiderParams
 */
public abstract class ContextProcessor implements PageProcessor
{
    protected final ProcessorContext context;

    public ContextProcessor(ProcessorContext context)
    {
        this.context = context;
    }

    public ProcessorContext getContext() {
        return context;
    }
}
