package xzf.spiderman.worker.webmagic;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import xzf.spiderman.common.exception.ConfigNotValidException;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class GroovyProcessor extends ParamProcessor
{
    public static final String KEY_GROOVY_URL = "groovyUrl";

    // URL key 除了?号之前的路径。
    private final static Map<String,GroovyCacheItem> CACHE = new ConcurrentHashMap<>(16);

    public GroovyProcessor(SpiderParams params) {
        super(params);
    }

    @Override
    public void process(Page page)
    {
        if(!params.containsKey(KEY_GROOVY_URL)){
            throw new ConfigNotValidException("类型为groovy的processor必须配置"+KEY_GROOVY_URL);
        }

        try {
            // 1.   ?version=?， 假如跟我本地lcoal 缓存 的,version变了，更新缓存
            String url = params.getString("groovyUrl");

            Class clazz = getGroovyClass(url);
            GroovyObject groovyObject = (GroovyObject) clazz.getConstructor(SpiderParams.class).newInstance(params);
            groovyObject.invokeMethod("process", page);
            log.info("GroovyProcessor执行脚本成功");

        }catch (Exception e){
            log.error("GroovyProcessor执行失败."+e.getMessage(),e);
        }
    }


    private synchronized Class getGroovyClass(String url)
    {
        String key = getUri(url);
        String version = getVersion(url);

        if( CACHE.containsKey(key) )
        {
            GroovyCacheItem groovyCacheItem = CACHE.get(key);

            if(groovyCacheItem.getVersion().equals(version))
            {
                return groovyCacheItem.getClazz();
            }
            else {
                // clear old version
                groovyCacheItem.getGroovyClassLoader().clearCache();

                // cache new version
                GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
                Class clazz = parseGroovyClass(groovyClassLoader, url);
                GroovyCacheItem newCacheItem = new GroovyCacheItem(version, groovyClassLoader, clazz);
                CACHE.put(key, newCacheItem);
                return clazz;
            }
        }

        // cache new version
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
        Class clazz = parseGroovyClass(groovyClassLoader, url);
        GroovyCacheItem newCacheItem = new GroovyCacheItem(version,groovyClassLoader,clazz);
        CACHE.put(key, newCacheItem);
        return clazz;

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


    private String getUri(  String url ){
        return url.substring(0, url.indexOf("?"));
    }

    private String getVersion( String url )
    {
        String substring =  url.substring(url.indexOf("?")+1) ;

        String version = "";

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
        if(params.getString("siteDomain") != null){
            return Site.me().setDomain(params.getString("siteDomain"));
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


    public static void main(String[] args)
    {

        String url = "file:///Users/xzf/Projects/prj2020/spider-man/spider-man-worker/src/main/groovy/xzf/spiderman/worker/processor/oschina/BlogProcessor.groovy?version=1";

        String substring = url.substring(url.indexOf("?")+1);
        System.out.println(substring);


        String version = "";

        String[] split = substring.split("&");
        for (String s : split) {
            String[] keyValue = s.split("=");
            if(keyValue[0].equals("version")){
                version =  keyValue.length>1?keyValue[1]:"";
                break;
            }
        }

        System.out.println(version);

//        org.springframework.web.util.UriComponentsBuilder
    }
}
