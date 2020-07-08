package xzf.spiderman.worker.webmagic;

import org.apache.commons.codec.digest.DigestUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;
import xzf.spiderman.worker.configuration.HessianRedisTemplate;

public class SpiderManRedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover
{
    protected HessianRedisTemplate redisTemplate;

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";


    public SpiderManRedisScheduler(HessianRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
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
    protected void pushWhenNoDuplicate(Request request, Task task) {

        this.redisTemplate.opsForList().rightPush(getQueueKey(task), request.getUrl());
        if (request.getExtras() != null) {
            String field = DigestUtils.sha1Hex(request.getUrl());
//            String value = JSON.toJSONString(request);
            Request value = request;
            this.redisTemplate.opsForHash().put(ITEM_PREFIX + task.getUUID(), field, value);
        }
    }

    @Override
    public synchronized Request poll(Task task) {

//        Jedis jedis = pool.getResource();
//        String url = jedis.lpop(getQueueKey(task));
        Object popUrl = this.redisTemplate.opsForList().leftPop(getQueueKey(task));
        if (popUrl == null) {
            return null;
        }
        String url = popUrl.toString();
        String key = ITEM_PREFIX + task.getUUID();
        String field = DigestUtils.sha1Hex(url);
//        byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
//        if (bytes != null) {
//            Request o = JSON.parseObject(new String(bytes), Request.class);
//            return o;
//        }
        Request o = (Request) this.redisTemplate.opsForHash().get(key, field);
        if(o != null){
            return o;
        }


        Request request = new Request(url);
        return request;

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
}
