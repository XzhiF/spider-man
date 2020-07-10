package xzf.spiderman.worker.service;

/**
 * 1. 选举出boss
 *
 * 2. 接受到调度任务，从数据中load到spider配置，获取爬虫集群信息
 *
 * 3. 创建任务 start, stop。 发布给每一台爬虫 http
 *
 * 4. 创建zk的工作节点， uuid。 并监控长的变化
 *
 * 5. 假如监听到zk工作目录的所有worker completed = true ，遍历发布stop消息给爬虫(http)
 *
 */
public class Boss
{


}
