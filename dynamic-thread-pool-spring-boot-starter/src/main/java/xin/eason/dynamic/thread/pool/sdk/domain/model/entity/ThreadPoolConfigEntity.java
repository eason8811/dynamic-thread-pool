package xin.eason.dynamic.thread.pool.sdk.domain.model.entity;

/**
 * 线程池配置信息实体
 */
public class ThreadPoolConfigEntity {
    /**
     * 应用名称
     */
    private String applicationName;
    /**
     * 线程池名称
     */
    private String threadPoolName;
    /**
     * 核心线程数
     */
    private Integer corePoolSize;
    /**
     * 最大线程数
     */
    private Integer maxPoolSize;
    /**
     * 当前活跃线程数
     */
    private Integer activeCount;
    /**
     * 当前池中线程数
     */
    private Integer poolSize;
    /**
     * 队列类型
     */
    private String queueType;
    /**
     * 当前队列任务数 (队列大小)
     */
    private Integer queueSize;
    /**
     * 队列剩余任务数
     */
    private Integer remainingCapacity;

    public ThreadPoolConfigEntity() {
    }

    public ThreadPoolConfigEntity(String applicationName, String threadPoolName, Integer corePoolSize, Integer maxPoolSize, Integer activeCount, Integer poolSize, String queueType, Integer queueSize, Integer remainingCapacity) {
        this.applicationName = applicationName;
        this.threadPoolName = threadPoolName;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.activeCount = activeCount;
        this.poolSize = poolSize;
        this.queueType = queueType;
        this.queueSize = queueSize;
        this.remainingCapacity = remainingCapacity;
    }

    public ThreadPoolConfigEntity(String applicationName, String threadPoolName) {
        this.applicationName = applicationName;
        this.threadPoolName = threadPoolName;
    }

    /**
     * 获取
     * @return appName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * 设置
     * @param appName
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * 获取
     * @return threadPoolName
     */
    public String getThreadPoolName() {
        return threadPoolName;
    }

    /**
     * 设置
     * @param threadPoolName
     */
    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    /**
     * 获取
     * @return corePoolSize
     */
    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * 设置
     * @param corePoolSize
     */
    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * 获取
     * @return maxPoolSize
     */
    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * 设置
     * @param maxPoolSize
     */
    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * 获取
     * @return activeCount
     */
    public Integer getActiveCount() {
        return activeCount;
    }

    /**
     * 设置
     * @param activeCount
     */
    public void setActiveCount(Integer activeCount) {
        this.activeCount = activeCount;
    }

    /**
     * 获取
     * @return poolSize
     */
    public Integer getPoolSize() {
        return poolSize;
    }

    /**
     * 设置
     * @param poolSize
     */
    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    /**
     * 获取
     * @return queueType
     */
    public String getQueueType() {
        return queueType;
    }

    /**
     * 设置
     * @param queueType
     */
    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    /**
     * 获取
     * @return queueSize
     */
    public Integer getQueueSize() {
        return queueSize;
    }

    /**
     * 设置
     * @param queueSize
     */
    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    /**
     * 获取
     * @return remainingCapacity
     */
    public Integer getRemainingCapacity() {
        return remainingCapacity;
    }

    /**
     * 设置
     * @param remainingCapacity
     */
    public void setRemainingCapacity(Integer remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }

    public String toString() {
        return "ThreadPoolConfigEntity{appName = " + applicationName + ", threadPoolName = " + threadPoolName + ", corePoolSize = " + corePoolSize + ", maxPoolSize = " + maxPoolSize + ", activeCount = " + activeCount + ", poolSize = " + poolSize + ", queueType = " + queueType + ", queueSize = " + queueSize + ", remainingCapacity = " + remainingCapacity + "}";
    }
}

