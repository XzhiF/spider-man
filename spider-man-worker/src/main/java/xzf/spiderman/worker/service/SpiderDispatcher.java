package xzf.spiderman.worker.service;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import xzf.spiderman.worker.data.StartSpiderReq;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.entity.SpiderServer;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SpiderDispatcher
{
    private final RestTemplate restTemplate = new RestTemplate();

    private List<SpiderCnf> cnfs;
    private String spiderId;
    private String groupId;


    public SpiderDispatcher(
            String spiderId,
            String groupId,
            List<SpiderCnf> cnfs)
    {
        this.spiderId = spiderId;
        this.groupId = groupId;
        this.cnfs = cnfs;

    }


    public void dispatchStart()
    {
        int availableSize = cnfs.size();

        ExecutorService executorService = new ThreadPoolExecutor(
                availableSize,
                availableSize,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(availableSize),
                new SpiderTaskHttpDispatcherThreadFactory());

        try {

            for (SpiderCnf cnf : cnfs)
            {
                executorService.submit(()->startSpiderRequest(cnf));
            }
        }
        finally {
            executorService.shutdown();
        }
    }

    public void startSpiderRequest(SpiderCnf cnf)
    {
        SpiderServer server = cnf.getServer();
        String host = server.getHost();
        Integer port = server.getPort();

        StartSpiderReq startSpiderReq = new StartSpiderReq();
        startSpiderReq.setCnfId(cnf.getId());
        startSpiderReq.setSpiderId(spiderId);

        String url = "http://"+host+":"+port+"/worker/spider-slave/start-spider";
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type","application/json");
        HttpEntity<StartSpiderReq> postEntity = new HttpEntity<>(startSpiderReq, headers);

        restTemplate.postForEntity(url, postEntity, Void.class);
    }

    public void dispatchStop()
    {
        // TODO
    }


    private class  SpiderTaskHttpDispatcherThreadFactory implements ThreadFactory
    {
        final ThreadGroup threadGroup = new ThreadGroup("SpiderTaskHttpDispatcher");
        final AtomicInteger worker = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r)
        {
            String name = "SpiderTaskHttpDispatcher-"+ groupId +"-worker-"+worker.incrementAndGet();
            return new Thread(threadGroup, r, name);
        }
    }

}
