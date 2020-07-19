package xzf.spiderman.worker.service.master;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import xzf.spiderman.worker.data.CloseSpiderReq;
import xzf.spiderman.worker.data.StartSpiderReq;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.entity.SpiderServer;
import xzf.spiderman.worker.service.GroupSpiderKey;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Slf4j
public class SpiderDispatcher
{
    private final RestTemplate restTemplate = new RestTemplate();

    private final List<SpiderCnf> cnfs;
    private final GroupSpiderKey key;


    public SpiderDispatcher(
            GroupSpiderKey key,
            List<SpiderCnf> cnfs)
    {
        this.key = key;
        this.cnfs = cnfs;

    }

    public void dispatchStart()
    {
        dispatch(cnf->()->startSpiderRequest(cnf));
    }

    public void dispatchClose()
    {
        dispatch(cnf->()->closeSpiderRequest(cnf));
    }

    private void dispatch(Function<SpiderCnf,Runnable> func)
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
                Runnable r = func.apply(cnf);
                executorService.submit(r);
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
        startSpiderReq.setGroupId(key.getGroupId());
        startSpiderReq.setSpiderId(key.getSpiderId());

        String url = "http://"+host+":"+port+"/worker/spider-slave/start-spider";
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type","application/json");
        HttpEntity<StartSpiderReq> postEntity = new HttpEntity<>(startSpiderReq, headers);

        log.info("dispatch url: " + url);

        restTemplate.postForEntity(url, postEntity, Void.class);
    }

    public void closeSpiderRequest(SpiderCnf cnf)
    {
        SpiderServer server = cnf.getServer();
        String host = server.getHost();
        Integer port = server.getPort();

        CloseSpiderReq closeSpiderReq = new CloseSpiderReq();
        closeSpiderReq.setCnfId(cnf.getId());
        closeSpiderReq.setGroupId(key.getGroupId());
        closeSpiderReq.setSpiderId(key.getSpiderId());

        String url = "http://"+host+":"+port+"/worker/spider-slave/close-spider";
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type","application/json");
        HttpEntity<CloseSpiderReq> postEntity = new HttpEntity<>(closeSpiderReq, headers);

        restTemplate.postForEntity(url, postEntity, Void.class);
    }

    private class  SpiderTaskHttpDispatcherThreadFactory implements ThreadFactory
    {
        final ThreadGroup threadGroup = new ThreadGroup("SpiderTaskHttpDispatcher");
        final AtomicInteger worker = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r)
        {
            String name = "SpiderTaskHttpDispatcher-"+ key.getGroupId() +"-worker-"+worker.incrementAndGet();
            return new Thread(threadGroup, r, name);
        }
    }

}
