package xin.eason.dynamic.thread.pool.sdk.trigger.listener;


import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.eason.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import xin.eason.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import xin.eason.dynamic.thread.pool.sdk.registry.IRegistry;

import java.util.List;

public class ThreadPoolConfigAdjustListener implements MessageListener<ThreadPoolConfigEntity> {

    /**
     * 日志记录器
     */
    private final Logger log = LoggerFactory.getLogger(ThreadPoolConfigAdjustListener.class);
    /**
     * 注册中心
     */
    private final IRegistry registry;
    /**
     * 动态线程池领域服务
     */
    private final IDynamicThreadPoolService threadPoolService;

    public ThreadPoolConfigAdjustListener(IRegistry registry, IDynamicThreadPoolService threadPoolService) {
        this.registry = registry;
        this.threadPoolService = threadPoolService;
    }

    /**
     * 订阅的 Topic 消息监听器监听到信息之后触发的方法
     * @param channel Topic 名字
     * @param threadPoolConfigEntity Topic 消息内容
     */
    @Override
    public void onMessage(CharSequence channel, ThreadPoolConfigEntity threadPoolConfigEntity) {
        // 动态更新下线程池配置
        threadPoolService.updateThreadPoolConfig(threadPoolConfigEntity);
        log.info("已更新线程池配置: {}", threadPoolConfigEntity);
        // 马上上报注册中心
        registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
        log.info("已上报线程池配置: {}", threadPoolConfigEntity);
        List<ThreadPoolConfigEntity> threadPoolConfigEntityList = threadPoolService.queryThreadPoolConfigList();
        registry.reportThreadPool(threadPoolConfigEntityList);
        log.info("已上报线程池列表: {}", threadPoolConfigEntityList);
    }
}
