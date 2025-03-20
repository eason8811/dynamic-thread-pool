package xin.eason.dynamic.thread.pool.sdk.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.eason.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态线程池领域服务实现类
 */
public class DynamicThreadPoolService implements IDynamicThreadPoolService {

    /**
     * 日志记录器
     */
    private final Logger log = LoggerFactory.getLogger(DynamicThreadPoolService.class);

    /**
     * 启动线程池的应用名称
     */
    private final String applicationName;

    /**
     * 储存线程池 Bean 对象的 Map 集合
     */
    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    public DynamicThreadPoolService(String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    /**
     * 查询线程池配置实体列表
     *
     * @return 所有线程池配置实体列表
     */
    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolConfigList() {
        Set<String> keySet = threadPoolExecutorMap.keySet();
        ArrayList<ThreadPoolConfigEntity> threadPoolConfigEntityList = new ArrayList<>();
        for (String key : keySet) {
            ThreadPoolConfigEntity threadPoolConfigEntity = queryThreadPoolConfigByName(key);
            threadPoolConfigEntityList.add(threadPoolConfigEntity);
        }
        return threadPoolConfigEntityList;
    }

    /**
     * 根据线程池名字查询线程池配置实体
     *
     * @param threadPoolName 线程池名字
     * @return 线程池配置实体
     */
    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName) {
        // 根据线程池 Bean 对象名字获取线程池对象
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);

        if (threadPoolExecutor == null)
            return new ThreadPoolConfigEntity(applicationName, threadPoolName);
        // new 一个线程池配置实体类对象
        ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity();
        threadPoolConfigEntity.setApplicationName(applicationName);
        threadPoolConfigEntity.setThreadPoolName(threadPoolName);
        threadPoolConfigEntity.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        threadPoolConfigEntity.setMaxPoolSize(threadPoolExecutor.getMaximumPoolSize());
        threadPoolConfigEntity.setActiveCount(threadPoolExecutor.getActiveCount());
        threadPoolConfigEntity.setPoolSize(threadPoolExecutor.getPoolSize());
        threadPoolConfigEntity.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
        threadPoolConfigEntity.setQueueSize(threadPoolExecutor.getQueue().size());
        threadPoolConfigEntity.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
        if (log.isDebugEnabled()) {
            log.info("动态线程池配置查询, applicationName: {}, threadPoolName: {}, 线程池配置: {}", applicationName, threadPoolName, threadPoolConfigEntity);
        }
        return threadPoolConfigEntity;
    }

    /**
     * 更新线程池配置实体信息
     *
     * @param threadPoolConfigEntity 用于更新的线程池配置实体
     */
    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        // 校验 applicationName 是否一致
        if (threadPoolConfigEntity.getApplicationName() == null || !applicationName.equals(threadPoolConfigEntity.getApplicationName()))
            return;
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
        // 校验是否有这个名字的线程池
        if (threadPoolExecutor == null)
            return;
        // 当前只调整最大线程数和核心线程数
        Integer corePoolSize = threadPoolConfigEntity.getCorePoolSize();
        Integer maxPoolSize = threadPoolConfigEntity.getMaxPoolSize();

        int originCorePoolSize = threadPoolExecutor.getCorePoolSize();
        int originMaxPoolSize = threadPoolExecutor.getMaximumPoolSize();

        if (corePoolSize > maxPoolSize) {
            log.warn("调整线程池参数时, 核心线程数应小于最大线程数, corePoolSize < maxPoolSize!");
            return;
        }

        if (corePoolSize > originMaxPoolSize) {
            threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
            threadPoolExecutor.setCorePoolSize(corePoolSize);
            return;
        }

        if (maxPoolSize < originCorePoolSize) {
            threadPoolExecutor.setCorePoolSize(corePoolSize);
            threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
        }
    }
}
