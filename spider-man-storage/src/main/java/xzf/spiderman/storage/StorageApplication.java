package xzf.spiderman.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
@EnableDiscoveryClient
public class StorageApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(StorageApplication.class, args);
    }

}
