package xzf.spiderman.worker.webmagic;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import xzf.spiderman.common.exception.JsonParserException;
import xzf.spiderman.worker.entity.SpiderCnf;

import java.util.List;
import java.util.stream.Collectors;

public class SpiderParams extends JSONObject
{
    public static final String KEY_URLS = "urls";

    //

    //---


    public List<String> getUrls()
    {
        return getJSONArray(KEY_URLS).stream().map(s->s.toString()).collect(Collectors.toList());
    }



    public static SpiderParams parse(String json)
    {
        try{
            if(StringUtils.isNotBlank(json)){
                SpiderParams params = JSON.parseObject(json, SpiderParams.class);
                return params;
            }
            return new SpiderParams();
        }catch (Exception e){
            throw new JsonParserException("解析SpiderConfParams失败."+e.getMessage(),e);
        }
    }
}
