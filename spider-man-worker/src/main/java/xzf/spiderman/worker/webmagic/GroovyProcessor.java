package xzf.spiderman.worker.webmagic;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.scope.ScopeCache;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import xzf.spiderman.common.cache.DelayCache;
import xzf.spiderman.common.cache.MemDelayCache;
import xzf.spiderman.common.exception.ConfigNotValidException;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
public class GroovyProcessor extends ContextProcessor
{
    public static final String KEY_GROOVY_URL = "groovyUrl";

    private static final DelayCache<String, Supplier<GroovyCacheItem>, GroovyCacheItem> cache = new MemDelayCache<>();

    public GroovyProcessor(ProcessorContext context) {
        super(context);
    }

    @Override
    public void process(Page page)
    {
        if(!context.getParams().containsKey(KEY_GROOVY_URL)){
            throw new ConfigNotValidException("类型为groovy的processor必须配置"+KEY_GROOVY_URL);
        }

        try {
            // 1.   ?version=?， 假如跟我本地lcoal 缓存 的,version变了，更新缓存
            String url = context.getParams().getString("groovyUrl");

            Class clazz = getGroovyClass(url);
            GroovyObject groovyObject = (GroovyObject) clazz.getConstructor(ProcessorContext.class).newInstance(context);
            groovyObject.invokeMethod("process", page);
            log.info("GroovyProcessor执行脚本成功");

        }catch (Exception e){
            log.error("GroovyProcessor执行失败."+e.getMessage(),e);
        }
    }


    private Class getGroovyClass(final String url)
    {
        String key = getUri(url);

        Optional<GroovyCacheItem> optional = cache.computeAndGet(key, (uri, old) -> {

            GroovyCacheItem oldItem = old.get();

            String oldVersion = oldItem.getVersion();
            String newVersion = getVersion(url);

            if (StringUtils.equals(newVersion, oldVersion)) {
                return old;
            }

            //close old的类加载信息
            try {
                oldItem.getGroovyClassLoader().close();
            } catch (IOException e) {
            }

            return new GroovyCacheItemSupplier(url);
        });

        return optional.orElseThrow(()->new ConfigNotValidException(KEY_GROOVY_URL+"无法解析。调用失败。")).getClazz();
    }

    private Class parseGroovyClass(GroovyClassLoader groovyClassLoader,String url)
    {
        try {
            GroovyCodeSource source = new GroovyCodeSource(new URL(url));
            Class clazz = groovyClassLoader.parseClass(source, true);
            return clazz;
        }catch (Exception e){
            throw new ConfigNotValidException("解析Groovy Class失败。" + e.getMessage(), e);
        }
    }


    private GroovyCacheItem createCacheItem(String url)
    {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
        Class clazz = parseGroovyClass(groovyClassLoader, url);
        GroovyCacheItem newCacheItem = new GroovyCacheItem(getVersion(url), groovyClassLoader, clazz);
        return newCacheItem;
    }

    private String getUri(  String url ){
        if(url.indexOf("?")==-1){
            return url;
        }

        return url.substring(0, url.indexOf("?"));
    }

    private String getVersion( String url )
    {
        String version = "0";

        if(url.indexOf("?")==-1){
            return version;
        }

        String substring =  url.substring(url.indexOf("?")+1) ;

        String[] split = substring.split("&");
        for (String s : split) {
            String[] keyValue = s.split("=");
            if(keyValue[0].equals("version")){
                version =  keyValue.length>1?keyValue[1]:"";
                break;
            }
        }
        return version;
    }




    @Override
    public Site getSite()
    {
        if(context.getParams().getString("siteDomain") != null){
            return Site.me().setDomain(context.getParams().getString("siteDomain"));
        }

        return Site.me();
    }

    @Data
    @AllArgsConstructor
    public class GroovyCacheItem
    {
        private String version;
        private GroovyClassLoader groovyClassLoader;
        private Class clazz;
    }


    public class GroovyCacheItemSupplier implements Supplier<GroovyCacheItem>
    {
        private volatile String url;

        public GroovyCacheItemSupplier(String url) {
            this.url = url;
        }

        private GroovyCacheItem instance;


        @Override
        public GroovyCacheItem get()
        {
            if(instance == null){
                synchronized (this){
                    if(instance == null){
                        instance =  createCacheItem(url);
                    }
                }
            }
            return instance;
        }
    }


}
