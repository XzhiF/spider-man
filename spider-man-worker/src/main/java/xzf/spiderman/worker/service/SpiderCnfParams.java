package xzf.spiderman.worker.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import xzf.spiderman.common.exception.JsonParserException;
import xzf.spiderman.worker.entity.SpiderCnf;

public class SpiderCnfParams extends JSONObject
{
    //---




    public static SpiderCnfParams parse(SpiderCnf cnf)
    {
        try{
            if(StringUtils.isNotBlank(cnf.getParams())){
                SpiderCnfParams params = JSON.parseObject(cnf.getParams(),SpiderCnfParams.class);
                return params;
            }
            return new SpiderCnfParams();
        }catch (Exception e){
            throw new JsonParserException("解析SpiderConfParams失败."+e.getMessage(),e);
        }


    }
}
