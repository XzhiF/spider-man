package xzf.spiderman.worker.configuration;

public class WorkerConst
{
    //
    public static final String REDIS_RUNNING_SPIDER_TASK_KEY = "worker:running:spider_task";

    public static final String REDIS_RUNNING_SPIDER_GROUP_KEY = "worker:running:spider_group";

    public static final String REDIS_RUNNING_SPIDER_GROUP_LOCK_PREFIX = "worker:running:spider_group:lock:";

    //
    public static final String ZK_SPIDER_TASK_BASE_PATH = "/worker/spider-task";

    //
    public static final String KAFKA_SPIDER_MAN_STORAGE_QUEUE = "spider-man-storage";

}
