package xzf.spiderman.worker.webmagic;

import io.lettuce.core.RedisCommandInterruptedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.RedisSystemException;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;
import xzf.spiderman.common.exception.UnknownException;
import xzf.spiderman.worker.configuration.HessianRedisTemplate;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BlockingPollRedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover
{
    private HessianRedisTemplate redisTemplate;
    private final long pollTimeout;
    private final TimeUnit pollTimeunit;

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";


    public BlockingPollRedisScheduler(HessianRedisTemplate redisTemplate, long pollTimeout, TimeUnit pollTimeunit) {
        this.redisTemplate = redisTemplate;
        this.pollTimeout = pollTimeout;
        this.pollTimeunit = pollTimeunit;
        setDuplicateRemover(this);
    }

    @Override
    public void resetDuplicateCheck(Task task) {

        this.redisTemplate.delete(getSetKey(task));

    }

    @Override
    public boolean isDuplicate(Request request, Task task) {

        return this.redisTemplate.opsForSet().add(getSetKey(task), request.getUrl()) == 0L;

    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task)
    {
        if(request == null || request.getUrl() == null){
            log.warn("request = " + request + ", or request url is null " ) ;
            return ;
        }

        this.redisTemplate.opsForList().rightPush(getQueueKey(task), request.getUrl());
        if (request.getExtras() != null) {
            String field = DigestUtils.sha1Hex(request.getUrl());
//            String value = JSON.toJSONString(request);
            Request value = request;
            this.redisTemplate.opsForHash().put(ITEM_PREFIX + task.getUUID(), field, value);
        }
    }

    @Override
    public Request poll(Task task)
    {
        Object urlObj = this.redisTemplate.opsForList().leftPop(getQueueKey(task));
        return getRequest(task, urlObj);
    }

    private Request getRequest(Task task, Object urlObj)
    {
        if (urlObj == null) {
            return null;
        }

        String url = urlObj.toString();
        String key = ITEM_PREFIX + task.getUUID();
        String field = DigestUtils.sha1Hex(url);

        Request o = (Request) this.redisTemplate.opsForHash().get(key, field);
        if(o != null){
            return o;
        }

        Request request = new Request(url);
        return request;
    }

    public Request blockingPoll(Task task, long timeout, TimeUnit timeUnit) throws InterruptedException
    {
        Object urlObj = popUrlObj(task, timeout, timeUnit);

        return getRequest(task,urlObj);
    }

    private Object popUrlObj(Task task, long timeout, TimeUnit timeUnit) throws InterruptedException
    {
        Object popUrl = null;
//
        try {
            popUrl = this.redisTemplate.opsForList().leftPop(getQueueKey(task), timeout, timeUnit);
            return popUrl;
        }
        catch (RedisCommandInterruptedException e){
            //            Thread.currentThread().interrupt(); 不来了，让捕获异常的地方来处理
            log.info("redisTemplate.leftPop 被中断。");
            throw new InterruptedException(e.getMessage());
        }
        catch (RedisSystemException e){
            if(e.getCause() instanceof RedisCommandInterruptedException){
                log.info("redisTemplate.leftPop 被中断。");
                throw new InterruptedException(e.getMessage());
            }
            throw new UnknownException("Schudler任务队列发生未知异常"+e.getMessage(), e);
        }
    }


    public Request blockingPoll(Task task) throws InterruptedException
    {
        return blockingPoll(task,pollTimeout, pollTimeunit);
    }


    protected String getSetKey(Task task) {
        return SET_PREFIX + task.getUUID();
    }

    protected String getQueueKey(Task task) {
        return QUEUE_PREFIX + task.getUUID();
    }

    protected String getItemKey(Task task)
    {
        return ITEM_PREFIX + task.getUUID();
    }

    @Override
    public int getLeftRequestsCount(Task task)
    {
        return this.redisTemplate.opsForList().size(getQueueKey(task)).intValue();
    }

    @Override
    public int getTotalRequestsCount(Task task)
    {
        return this.redisTemplate.opsForSet().size(getSetKey(task)).intValue();
    }


    public static String getSetPrefix() {
        return SET_PREFIX;
    }

    public HessianRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }
}
