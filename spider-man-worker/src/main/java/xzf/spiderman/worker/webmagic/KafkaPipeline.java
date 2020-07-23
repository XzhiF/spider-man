package xzf.spiderman.worker.webmagic;

import com.alibaba.fastjson.JSON;
import org.springframework.kafka.core.KafkaTemplate;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import xzf.spiderman.worker.configuration.WorkerConst;
import xzf.spiderman.worker.data.StoreCnfData;
import xzf.spiderman.worker.data.StoreDataReq;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.entity.SpiderStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class KafkaPipeline implements Pipeline
{
    private final KafkaTemplate kafkaTemplate;
    private final SpiderCnf cnf;
    private final List<SpiderStore> stores;
    

    public KafkaPipeline(KafkaTemplate kafkaTemplate,SpiderCnf cnf, List<SpiderStore> stores)
    {
        this.kafkaTemplate = kafkaTemplate;
        this.cnf = cnf;
        this.stores = stores;
    }

    @Override
    public void process(ResultItems resultItems, Task task)
    {
        resultItems.put("_timestamp", new Date());

        StoreDataReq req = StoreDataReq.builder()
                .data(resultItems.getAll())
                .storeCnfs(getStoreCnfs())
                .timestamp(new Date())
                .build();

        String msg = JSON.toJSONString(req);

        kafkaTemplate.send(WorkerConst.KAFKA_SPIDER_MAN_STORAGE_QUEUE, msg);
    }



    private List<StoreCnfData> getStoreCnfs()
    {
        List<StoreCnfData> rets = new ArrayList<>();
        
        for (SpiderStore store : stores) {

            StoreCnfData ret = new StoreCnfData();
            ret.setCnfId(store.getId());
            ret.setStoreId(store.getId());
            ret.setUrl(store.getUrl());
            ret.setHost(store.getHost());
            ret.setPort(store.getPort());
            ret.setType(store.getType());
            ret.setDatabase(store.getDatabase());
            ret.setTableName(store.getTableName());
            ret.setUsername(store.getUsername());
            ret.setPassword(store.getPassword());

            rets.add(ret);
        }
        
        return rets;
    }

}
