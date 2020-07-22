package xzf.spiderman.storage.congiration;

import com.mongodb.MongoClient;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTest
{

    @Autowired
    private Environment environment;

    @Test
    public void testCreateMongoClient() throws Exception
    {
        System.out.println("environment="+environment);

        MongoProperties properties = new MongoProperties();
        properties.setDatabase("admin");
        properties.setHost("mongo.spiderman.xzf");
        properties.setPort(27017);
        properties.setUsername("root");
        properties.setPassword("123456".toCharArray());

        MongoClientFactory factory = new MongoClientFactory(properties, environment);
        MongoClient client = factory.createMongoClient(null);
        System.out.println(client);


        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(client, "admin"));
        System.out.println(mongoTemplate);


        BSONObject obj = new BasicBSONObject();
        obj.put("name","zhangshan");
        obj.put("gender", "famale");

        mongoTemplate.insert(obj, "test1");

    }

}
