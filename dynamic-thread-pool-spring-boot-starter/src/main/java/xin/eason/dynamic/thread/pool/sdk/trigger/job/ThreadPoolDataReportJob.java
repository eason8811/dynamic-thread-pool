package xin.eason.dynamic.thread.pool.sdk.trigger.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import xin.eason.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import xin.eason.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import xin.eason.dynamic.thread.pool.sdk.registry.IRegistry;

import java.util.List;

/**
 * 动态线程池信息上报定时任务类
 */
public class ThreadPoolDataReportJob {
    /**
     * 日志记录器
     */
    private final Logger log = LoggerFactory.getLogger(ThreadPoolDataReportJob.class);
    /**
     * 注册中心, 用于动态上报线程池配置信息
     */
    private final IRegistry registry;
    /**
     * 动态线程池领域服务, 用于获取动态线程池配置信息
     */
    private final IDynamicThreadPoolService threadPoolService;

    public ThreadPoolDataReportJob(IRegistry registry, IDynamicThreadPoolService threadPoolService) {
        this.registry = registry;
        this.threadPoolService = threadPoolService;
    }

    /**
     * 定时任务, 定时获取动态线程池配置信息并上报
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void exec() {
        List<ThreadPoolConfigEntity> threadPoolConfigEntityList = threadPoolService.queryThreadPoolConfigList();
        registry.reportThreadPool(threadPoolConfigEntityList);
        log.info("已上报动态线程池列表: {}", threadPoolConfigEntityList);

        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntityList) {
            registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
            log.info("已上报动态线程池配置: {}", threadPoolConfigEntity);
        }
    }
}
