package xin.eason.dynamic.thread.pool.sdk.registry.redis;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import xin.eason.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import xin.eason.dynamic.thread.pool.sdk.domain.model.valobj.RegistryEnumVO;
import xin.eason.dynamic.thread.pool.sdk.registry.IRegistry;

import java.time.Duration;
import java.util.List;

/**
 * 注册中心实现类 实现方法 (Redis)
 */
public class RedisRegistry implements IRegistry {

    /**
     * Redisson 客户端
     */
    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 上报一个线程池配置实体列表, 即上报列表中所有的线程池配置实体
     *
     * @param threadPoolConfigEntityList 线程池配置实体列表
     */
    @Override
    public void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntityList) {
        // 先删除, 再添加, 确保不会重复
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        list.delete();
        list.addAll(threadPoolConfigEntityList);
    }

    /**
     * 上报一个线程池配置实体
     *
     * @param threadPoolConfigEntity 线程池配置实体
     */
    @Override
    public void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {
        RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + threadPoolConfigEntity.getApplicationName() + "_" + threadPoolConfigEntity.getThreadPoolName());
        bucket.set(threadPoolConfigEntity, Duration.ofDays(30));
    }
}
