package xin.eason.dynamic.thread.pool.sdk.registry;

import xin.eason.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 注册中心接口
 */
public interface IRegistry {
    /**
     * 上报一个线程池配置实体列表, 即上报列表中所有的线程池配置实体
     * @param threadPoolConfigEntityList 线程池配置实体列表
     */
    void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntityList);

    /**
     * 上报一个线程池配置实体
     * @param threadPoolConfigEntity 线程池配置实体
     */
    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);
}
