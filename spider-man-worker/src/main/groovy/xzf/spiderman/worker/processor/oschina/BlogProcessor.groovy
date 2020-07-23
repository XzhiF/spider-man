package xzf.spiderman.worker.processor.oschina

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Site
import xzf.spiderman.worker.webmagic.ContextProcessor
import xzf.spiderman.worker.webmagic.SpiderParams

class BlogProcessor extends ContextProcessor
{
    Logger log = LoggerFactory.getLogger BlogProcessor.class

    private final Site site = Site.me().setDomain("www.oschina.net");

    BlogProcessor(SpiderParams params) {
        super(params);
    }

    @Override
    public void process(Page page)
    {
        log.info("BlogProcessor -- 3 -- " + Thread.currentThread());

        if(page.getUrl().toString().startsWith("https://www.oschina.net/blog")) {
            List<String> links1 = page.getHtml().links().regex("https://my.oschina.net/\\w+/blog/\\d+").all();
            List<String> links2 = page.getHtml().links().regex("https://my.oschina.net/\\w+/\\d+/blog/\\d+").all();

            // debug
            int size1 = Math.min(links1.size(),5);
            for (int i = 0; i < size1; i++) {
                page.addTargetRequest(links1.get(i));
            }

            int size2 = Math.min(links2.size(),5);
            for (int i = 0; i < size2; i++) {
                page.addTargetRequest(links2.get(i));
            }
        }
        else {
            String id = page.getHtml().$("#mainScreen > div > val:nth-child(3)", "data-value").toString();
            String author = page.getHtml().xpath("//*[@id=\"mainScreen\"]/div/div[1]/div/div[2]/div[1]/div[2]/div[1]/div[1]/a/span/text()").toString();
            String title = page.getHtml().xpath("//*[@id=\"mainScreen\"]/div/div[1]/div/div[2]/div[1]/div[2]/h2/text()").toString();

            page.putField("id", id);
            page.putField("author", author);
            page.putField("title", title);
            if (id == null) {
                page.setSkip(true);
            }
        }
    }

    @Override
    public Site getSite()
    {
        return site;
    }
}
