package xzf.spiderman.storage.mq;

import com.mongodb.MongoClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import xzf.spiderman.common.cache.DelayCache;
import xzf.spiderman.common.cache.MemDelayCache;
import xzf.spiderman.common.exception.ConfigNotValidException;
import xzf.spiderman.worker.data.StoreCnfData;

import java.util.Optional;
import java.util.function.Supplier;

public class MongoTemplateFactory
{
    private DelayCache<StoreCnfData, Supplier<CacheItem>, CacheItem> cache = new MemDelayCache<>();

    public MongoTemplate createAndGet(StoreCnfData key)
    {
        Optional<CacheItem> optional = cache.computeAndGet( key, (cnf, old)->{

            if(old == null){
                return new CacheItemSupplier(cnf);
            }

            StoreCnfData oldCnf = old.get().getCnf();
            if(cnf.hasChanges(oldCnf)){
                return new CacheItemSupplier(cnf);
            }

            return old;
        });

        return optional.orElseThrow(()->new ConfigNotValidException("StoreCnf获取MongoTemplate失败."+key))
                .getTemplate();
    }

    private MongoTemplate createMongoTemplate(StoreCnfData cnf)
    {
        MongoProperties properties = createProperties(cnf);
        MongoClientFactory factory = new MongoClientFactory(properties, null);
        MongoClient client = factory.createMongoClient(null);
        MongoTemplate template = new MongoTemplate(client, cnf.getDatabase());
        return template;
    }

    private MongoProperties createProperties(StoreCnfData store)
    {
        MongoProperties properties = new MongoProperties();
        properties.setDatabase(store.getDatabase());
        properties.setHost(store.getHost());
        properties.setPort(store.getPort());
        properties.setUsername(store.getUsername());
        properties.setPassword(store.getPassword().toCharArray());
        return properties;
    }


    public class CacheItemSupplier implements Supplier<CacheItem>
    {
        private StoreCnfData cnf;
        private volatile MongoTemplate template;

        public CacheItemSupplier(StoreCnfData cnf) {
            this.cnf = cnf;
        }

        @Override
        public CacheItem get()
        {
            if(template == null) {
                synchronized (this) {
                    if (template == null) {
                        template = createMongoTemplate(cnf);
                    }
                }
            }
            return new CacheItem(cnf, template);
        }
    }

    @Getter
    @AllArgsConstructor
    public class CacheItem
    {
        private final StoreCnfData cnf;
        private final MongoTemplate template;
    }


}
