package xzf.spiderman.worker;

import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

public class OschinaBlogIndexPageProcessor implements PageProcessor
{
    private Site site = Site.me().setDomain("www.oschina.net");

    @Override
    public void process(Page page)
    {
        Html html = page.getHtml();

        Selectable blogItemDivs = html.$(".blog-item");
//        Elements elts = html.getDocument().select(".blog-item");

        List<Selectable> blogItemNodes = blogItemDivs.nodes();
//        List<Map<String,Object>> results = new ArrayList<>();
        List<String> heads = new ArrayList<>();

        for (Selectable itemNode : blogItemNodes) {
            heads.add(itemNode.$(".header").get());
        }
        page.putField("heads", heads);
    }

    @Override
    public Site getSite()
    {
        return site;
    }


    public static void main(String[] args)
    {
        Request request = new Request();

        request.setUrl("https://www.oschina.net/blog");
        request.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        request.addHeader("Accept-Encoding", "gzip, deflate, br");

        Spider.create(new OschinaBlogIndexPageProcessor())
//                .addUrl("https://www.oschina.net/blog")
                .addRequest(request)
                .addPipeline(new ConsolePipeline()).run();
    }
}
