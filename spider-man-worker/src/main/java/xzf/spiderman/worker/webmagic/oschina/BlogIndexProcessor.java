package xzf.spiderman.worker.webmagic.oschina;

import com.alibaba.fastjson.JSON;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogIndexProcessor implements PageProcessor
{
    private final Site site = Site.me().setDomain("www.oschina.net");

    @Override
    public void process(Page page)
    {
        Selectable blogItemDiv = page.getHtml().$(".blog-item");
        List<Selectable> nodes = blogItemDiv.nodes();

        int i = 0 ;
        for (Selectable node : nodes) {
            String text = node.$(".header").get();
            String key = String.valueOf(i++);
            page.putField(key, text);
        }

    }

    @Override
    public Site getSite()
    {
        return site;
    }
}
