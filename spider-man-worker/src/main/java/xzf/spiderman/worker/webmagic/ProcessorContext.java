package xzf.spiderman.worker.webmagic;


import lombok.Getter;

@Getter
public class ProcessorContext
{
    private final SpiderParams params;

    public ProcessorContext(SpiderParams params)
    {
        this.params = params;
    }
}
