package xzf.spiderman.worker.webmagic;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import xzf.spiderman.worker.configuration.WorkerConst;
import xzf.spiderman.worker.data.StoreData;
import xzf.spiderman.worker.entity.SpiderCnf;

import java.util.HashMap;

public class KafkaPipeline implements Pipeline
{
    private final KafkaTemplate kafkaTemplate;
    private final SpiderCnf cnf;

    public KafkaPipeline(KafkaTemplate kafkaTemplate, SpiderCnf cnf)
    {
        this.kafkaTemplate = kafkaTemplate;
        this.cnf = cnf;
    }

    @Override
    public void process(ResultItems resultItems, Task task)
    {
        // 组织数据 ->
        // store ->  storage 可见的数据 。
        HashMap<String, Object> data = new HashMap<>();
        data.putAll(resultItems.getAll());

        HashMap<String, Object> cfg = new HashMap<>();
        cfg.put("storeId", cnf.getStore());

        //
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("data", data);
        ret.put("cfg", cfg);

        String msg = JSON.toJSONString(ret);

        kafkaTemplate.send(WorkerConst.KAFKA_SPIDER_MAN_STORAGE, msg);
    }



}
