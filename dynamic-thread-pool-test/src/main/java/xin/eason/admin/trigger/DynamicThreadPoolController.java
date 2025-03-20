package xin.eason.admin.trigger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;
import xin.eason.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import xin.eason.dynamic.thread.pool.sdk.domain.model.valobj.RegistryEnumVO;
import xin.eason.admin.types.Result;

import java.util.List;

/**
 * 动态线程池管理触发器
 */
@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/dynamic/thread/pool")
@RequiredArgsConstructor
public class DynamicThreadPoolController {
    /**
     * Redisson 客户端
     */
    private final RedissonClient redissonClient;

    /**
     * 动态查询线程池配置列表
     * @return 线程池配置实体类对象的 列表
     */
    @GetMapping("/query_thread_pool_list")
    public Result<List<ThreadPoolConfigEntity>> queryThreadPoolConfigEntityList() {
        try {
            RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
            log.info("已查询到线程池配置信息列表: {}", list);
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询线程池配置列表错误! 错误信息: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 动态查询线程池配置
     * @return 线程池配置实体类对象
     */
    @GetMapping("/query_thread_pool_config")
    public Result<ThreadPoolConfigEntity> queryThreadPoolConfigEntity(String applicationName, String threadPoolName) {
        try {
            RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName + "_" + threadPoolName);
            ThreadPoolConfigEntity threadPoolConfigEntity = bucket.get();
            log.info("查询线程池配置信息成功! applicationName: {}, threadPoolName: {}, 配置信息: {}", applicationName, threadPoolName, threadPoolConfigEntity);
            return Result.success(threadPoolConfigEntity);
        } catch (Exception e) {
            log.error("查询线程池配置信息错误! 错误信息: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 动态更新线程池配置
     * @param threadPoolConfigEntity 需要更新的动态线程池配置
     * @return 更新结果
     */
    @PostMapping("/update_thread_pool_config")
    public Result<Boolean> updateThreadPoolConfig(@RequestBody ThreadPoolConfigEntity threadPoolConfigEntity) {
        try {
            RTopic topic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey() + "_" + threadPoolConfigEntity.getApplicationName());
            topic.publish(threadPoolConfigEntity);
            log.info("更新线程池配置成功! 更新参数: {}", threadPoolConfigEntity);
            return Result.success(true);
        } catch (Exception e) {
            log.error("更新线程池配置错误! 错误信息: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
