package xin.eason.dynamic.thread.pool.sdk.domain;

import xin.eason.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 动态线程池领域服务接口
 */
public interface IDynamicThreadPoolService {
    /**
     * 查询线程池配置实体列表
     * @return 所有线程池配置实体列表
     */
    List<ThreadPoolConfigEntity> queryThreadPoolConfigList();

    /**
     * 根据线程池名字查询线程池配置实体
     * @param threadPoolName 线程池名字
     * @return 线程池配置实体
     */
    ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName);

    /**
     * 更新线程池配置实体信息
     * @param threadPoolConfigEntity 用于更新的线程池配置实体
     */
    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);

}
