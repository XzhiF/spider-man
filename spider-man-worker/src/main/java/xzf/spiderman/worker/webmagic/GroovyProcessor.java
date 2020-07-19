package xzf.spiderman.worker.webmagic;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.net.URL;

@Slf4j
public class GroovyProcessor extends ParamProcessor
{
    public GroovyProcessor(SpiderParams params) {
        super(params);
    }

    @Override
    public void process(Page page)
    {

        try {

            // LRU->
            // 缓存结构 , url, { Classloader, Class }

            //file:///Users/xzf/Projects/prj2020/spider-man/spider-man-worker/src/main/groovy/xzf/spiderman/worker/processor/oschina/BlogProcessor.groovy?v=1

            // 1.   ?version=?， 假如跟我本地lcoal 缓存 的,version变了，更新缓存
            URL url = new URL(params.getString("groovyUrl"));


            GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
            GroovyCodeSource source = new GroovyCodeSource(url);
            Class clazz = groovyClassLoader.parseClass(source, false);
            GroovyObject groovyObject = (GroovyObject) clazz.getConstructor(SpiderParams.class).newInstance(params);
            groovyObject.invokeMethod("process", page);
            log.info("GroovyProcessor执行脚本成功");

        }catch (Exception e){

            log.error("GroovyProcessor执行失败."+e.getMessage(),e);
        }

//        groovyClassLoader.pa



    }

    @Override
    public Site getSite()
    {
        if(params.getString("siteDomain") != null){
            return Site.me().setDomain(params.getString("siteDomain"));
        }

        return Site.me();
    }
}
