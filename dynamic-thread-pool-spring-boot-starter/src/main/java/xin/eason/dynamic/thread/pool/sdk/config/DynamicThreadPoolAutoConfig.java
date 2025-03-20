package xin.eason.dynamic.thread.pool.sdk.config;

import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import xin.eason.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import xin.eason.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import xin.eason.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import xin.eason.dynamic.thread.pool.sdk.domain.model.valobj.RegistryEnumVO;
import xin.eason.dynamic.thread.pool.sdk.registry.IRegistry;
import xin.eason.dynamic.thread.pool.sdk.registry.redis.RedisRegistry;
import xin.eason.dynamic.thread.pool.sdk.trigger.job.ThreadPoolDataReportJob;
import xin.eason.dynamic.thread.pool.sdk.trigger.listener.ThreadPoolConfigAdjustListener;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态线程池自动装配类
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
public class DynamicThreadPoolAutoConfig {

    /**
     * 日志记录器
     */
    private final Logger log = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);
    /**
     * Application 应用名称
     */
    private String applicationName;

    /**
     * 提供一个 Redis 客户端 Bean 对象 用于操作 Redis
     * @param properties Redis注册中心相关配置
     * @return RedissonClient Bean对象
     */
    @Bean("dynamicThreadRedissonClient")
    public RedissonClient redissonClient(DynamicThreadPoolAutoProperties properties) {
        Config config = new Config();
        // 根据需要可以设定编解码器；https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96
        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive())
        ;

        RedissonClient redissonClient = Redisson.create(config);

        log.info("动态线程池，注册器 (redis) 链接初始化完成。host:{}, poolSize:{}, 状态:{}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }

    /**
     * 将 Redis 注册中心实现类注册为 Bean 对象
     * @param redissonClient Redis 客户端, 用于操作 Redis 注册中心
     * @return 以 Redis 为实现方式的注册中心实现类 Bean 对象
     */
    @Bean
    public IRegistry registry(RedissonClient redissonClient) {
        return new RedisRegistry(redissonClient);
    }

    /**
     * 提供一个动态线程池的 Bean 对象
     * @param applicationContext 应用上下文, 用于获取注入这个 Bean 对象的 SpringBoot 应用信息
     * @param threadPoolExecutorMap 线程池 Map 用于获取使用这个动态线程池的 SpringBoot 应用所提供的所有线程池 Bean 对象
     * @return 动态线程池的 Bean 对象
     */
    @Bean
    public IDynamicThreadPoolService dynamicThreadPool(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap, RedissonClient redissonClient) {
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (applicationName != null && applicationName.isBlank()) {
            applicationName = "缺省的 Application 名称";
            log.warn("动态线程池启动提示: SpringBoot 应用未指定 spring.application.name !");
        }
        log.info("SpringBoot 应用: {} 已启用动态线程池, 线程池信息: {}", applicationName, threadPoolExecutorMap.keySet());

        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());

        for (ThreadPoolConfigEntity threadPoolConfigEntity : list) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaxPoolSize());
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        }

        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }

    /**
     * 将 定时上报动态线程池配置 任务类注入
     * @param registry 注册中心
     * @param threadPoolService 动态线程池领域服务
     * @return 定时任务类 Bean 对象
     */
    @Bean
    public ThreadPoolDataReportJob threadPoolDataReportJob(IRegistry registry, IDynamicThreadPoolService threadPoolService) {
        return new ThreadPoolDataReportJob(registry, threadPoolService);
    }

    /**
     * 注册 线程池配置调整监听器 Bean 对象
     * @param registry 注册中心
     * @param threadPoolService 动态线程池领域服务
     * @return 线程池配置调整监听器 Bean 对象
     */
    @Bean
    public ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener(IRegistry registry, IDynamicThreadPoolService threadPoolService) {
        return new ThreadPoolConfigAdjustListener(registry, threadPoolService);
    }
    
    /**
     * 将 Redis 订阅发布 Topic 注册为 Bean 对象
     * @param redissonClient Redisson 客户端
     * @param threadPoolConfigAdjustListener 线程池配置动态调整监听器对象
     * @return Redis 订阅发布 Topic
     */
    @Bean
    public RTopic rTopic(RedissonClient redissonClient, ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener) {
        RTopic topic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey() + "_" + applicationName);
        topic.addListener(ThreadPoolConfigEntity.class, threadPoolConfigAdjustListener);
        return topic;
    }
}
