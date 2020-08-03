package xzf.spiderman.worker.processor.oschina

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Site
import xzf.spiderman.worker.webmagic.ContextProcessor
import xzf.spiderman.worker.webmagic.ProcessorContext

class BlogProcessor extends ContextProcessor
{
    Logger log = LoggerFactory.getLogger BlogProcessor.class

    private final Site site = Site.me().setDomain("www.oschina.net");

    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

    BlogProcessor(ProcessorContext context) {
        super(context);
    }

    @Override
    public void process(Page page)
    {
        log.info("BlogProcessor -- 3 -- " + Thread.currentThread());

        if(page.getUrl().toString().startsWith("https://www.oschina.net/blog")) {
            List<String> links1 = page.getHtml().links().regex("https://my.oschina.net/\\w+/blog/\\d+").all();
            List<String> links2 = page.getHtml().links().regex("https://my.oschina.net/\\w+/\\d+/blog/\\d+").all();

            // debug
            int size1 = Math.min(links1.size(),10);
            for (int i = 0; i < size1; i++) {
                page.addTargetRequest(links1.get(i));
            }

            int size2 = Math.min(links2.size(),10);
            for (int i = 0; i < size2; i++) {
                page.addTargetRequest(links2.get(i));
            }
        }
        else {


            String id = page.getHtml().$("#mainScreen > div > val:nth-child(3)", "data-value").toString();
            String author = page.getHtml().$("#mainScreen > div > div.ui.internally.grid.blog-detail.bg-wrap > div > div.twelve.wide.computer.sixteen.wide.tablet.sixteen.wide.mobile.column.body-container > div.float-menu-content > div.article-detail > div.extra.ui.horizontal.list.meta-wrap > div:nth-child(1) > a > span").replace(regEx_html,"").get().trim();
            String title = page.getHtml().$("#mainScreen > div > div.ui.internally.grid.blog-detail.bg-wrap > div > div.twelve.wide.computer.sixteen.wide.tablet.sixteen.wide.mobile.column.body-container > div.float-menu-content > div.article-detail > h2").nodes().get(0).replace(regEx_html,"").get();
            title= title.substring(title.indexOf("\n"));
//

//            String author = page.getHtml().xpath("//*[@id=\"mainScreen\"]/div/div[1]/div/div[2]/div[1]/div[3]/div[1]/div[1]/a/span/text()").toString();
//            String title = page.getHtml().xpath("//*[@id=\"mainScreen\"]/div/div[1]/div/div[2]/div[1]/div[3]/h2/text()").toString();

            String content = page.getHtml().$("#js_content > section:nth-child(1)").smartContent().get();

            if (id == null || "".equals(id)) {
                page.setSkip(true);
            }

            page.putField("id", id);
            page.putField("author", author);
            page.putField("title", title);
            page.putField("content", content);

        }
    }

    @Override
    public Site getSite()
    {
        return site;
    }
}
