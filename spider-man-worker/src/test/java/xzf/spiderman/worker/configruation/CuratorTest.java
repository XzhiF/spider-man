package xzf.spiderman.worker.configruation;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CuratorTest
{
    @Autowired
    private CuratorFramework curator;

    @Test
    public void testWatch() throws Exception
    {
        String path = "/worker/spider_task/test";


        if(curator.checkExists().forPath(path) == null) {
            curator.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        }


        CuratorCache cache = CuratorCache.builder(curator, path)
                .build();

        cache.listenable()
        .addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData oldData, ChildData data) {
                System.out.println(type+", oldData="+oldData+", newData="+data);
            }
        });

        cache.start();

        System.in.read();

    }


}
