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

import java.util.Arrays;
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
        StoreDataReq req = StoreDataReq.builder()
                .data(resultItems.getAll())
                .storeCnfs(Arrays.asList(getStoreData()))
                .build();

        String msg = JSON.toJSONString(req);

        kafkaTemplate.send(WorkerConst.KAFKA_SPIDER_MAN_STORAGE, msg);
    }



    private StoreCnfData getStoreData()
    {
        StoreCnfData ret = new StoreCnfData();

        ret.setCnfId(cnf.getId());
        ret.setStoreId(cnf.getStore().getId());
        ret.setUrl(cnf.getStore().getUrl());
        ret.setHost(cnf.getStore().getHost());
        ret.setPort(cnf.getStore().getPort());
        ret.setType(cnf.getStore().getType());
        ret.setTableName(cnf.getStore().getTableName());
        ret.setUsername(cnf.getStore().getUsername());
        ret.setPassword(cnf.getStore().getPassword());

        return ret;
    }

}
