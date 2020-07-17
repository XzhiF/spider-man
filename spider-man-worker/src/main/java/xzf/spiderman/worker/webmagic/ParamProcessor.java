package xzf.spiderman.worker.webmagic;

import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 爬虫需要SpiderParams
 */
public abstract class ParamProcessor implements PageProcessor
{
    protected SpiderParams params;

    public ParamProcessor(SpiderParams params)
    {
        this.params = params;
    }

    public SpiderParams getParams() {
        return params;
    }
}
