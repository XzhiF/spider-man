package xzf.spiderman.worker.service.master;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.service.GroupSpiderKey;
import xzf.spiderman.worker.webmagic.BlockingPollRedisScheduler;
import xzf.spiderman.worker.webmagic.SpiderParams;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class SpiderQueueProducer
{
    private BlockingPollRedisScheduler scheduler;

    public SpiderQueueProducer(BlockingPollRedisScheduler scheduler)
    {
        this.scheduler = scheduler;
    }

    public void sendTasks(GroupSpiderKey key, List<SpiderCnf> cnfs)
    {
        List<SpiderCnf> sharedSpiderCnfs = cnfs.stream().filter(SpiderCnf::isSharedMode).collect(Collectors.toList());
        sendSharedTasks(key, sharedSpiderCnfs);

        List<SpiderCnf> exclusiveSpiderCnfs = cnfs.stream().filter(SpiderCnf::isExclusiveMode).collect(Collectors.toList());
        sendExclusiveTasks(key, exclusiveSpiderCnfs);
    }

    private void sendSharedTasks(GroupSpiderKey key, List<SpiderCnf> sharedSpiderCnfs)
    {
        sendTasks(key,sharedSpiderCnfs, true);
    }

    private void sendExclusiveTasks(GroupSpiderKey key, List<SpiderCnf> sharedSpiderCnfs)
    {
        sendTasks(key,sharedSpiderCnfs, false);
    }

    private void sendTasks(GroupSpiderKey key, List<SpiderCnf> sharedSpiderCnfs, boolean isShared)
    {
        for (SpiderCnf cnf : sharedSpiderCnfs) {
            SpiderParams params = SpiderParams.parse(cnf.getParams());

            for (String url : params.getUrls())
            {
                Request request = new Request();
                request.setUrl(url);

                String uuid = key.getSpiderId();
                if(!isShared){
                    uuid = uuid + "_" + cnf.getId();
                }

                Task task = new DefaultTask(uuid, getSite(url));

                scheduler.push(request, task);
            }
        }
    }

    private Site getSite(String urlString)
    {
        try {
            URL url = new URL(urlString);
            return Site.me().setDomain(url.getHost());
        } catch (MalformedURLException e) {
            log.warn("parse url 失败." + urlString);
        }
        return new Site();
    }

    public void clear(GroupSpiderKey key)
    {
        scheduler.clearDuplicateSet(new DefaultTask(key.getSpiderId()));
    }


    public static class DefaultTask implements Task
    {
        private final String uuid;
        private final Site site;

        public DefaultTask(String uuid, Site site) {
            this.uuid = uuid;
            this.site = site;
        }

        public DefaultTask(String uuid){
            this(uuid, new Site());
        }


        @Override
        public String getUUID() {
            return uuid;
        }

        @Override
        public Site getSite() {
            return site;
        }
    }


}
