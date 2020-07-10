package xzf.spiderman.worker.webmagic.oschina;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import xzf.spiderman.worker.configuration.HessianRedisTemplate;
import xzf.spiderman.worker.webmagic.BlockingPollRedisScheduler;

import java.util.concurrent.TimeUnit;

public class BlogIndexSpider
{

    private HessianRedisTemplate hessianRedisTemplate;

    public BlogIndexSpider(HessianRedisTemplate hessianRedisTemplate) {
        this.hessianRedisTemplate = hessianRedisTemplate;
    }

    public void run()
    {
        Request request = new Request();
        request.setUrl("https://www.oschina.net/blog");

        Spider.create(new BlogIndexProcessor())
                .addRequest(request)
                .setScheduler(new BlockingPollRedisScheduler(hessianRedisTemplate, 1, TimeUnit.MINUTES))
                .addPipeline(new ConsolePipeline())
                .run();

    }



}
