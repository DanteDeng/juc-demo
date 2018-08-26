package com.nature.executor;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 执行器
 */
public class DemoProducerExecutorService<T extends Callable> extends AbstractExecutorService<T> {

    /**
     * 处理监控
     *
     * @param shared 共享资源
     */
    @Override
    public void handleWatch(Map<String, Object> shared) {
        int position = (int) shared.get("position");
        int maxAccess = (int) shared.get("maxAccess");
        // 调整资源
        int resourceCount = maxAccess - position;
        // 调整资源
        redistribute(resourceCount);
        // 当前完成任务数
        long completedTaskCount = service.getCompletedTaskCount();
        // 间隔期间完成任务数
        long periodCompletedCount = completedTaskCount - lastCompleteCount;
        // 更新上次完成任务数
        lastCompleteCount = completedTaskCount;
        Date now = new Date();
        String threadName = Thread.currentThread().getName();
        System.out.println(String.format("%s %s producerThreads = %s", now, threadName, resourceCount));
        System.out.println(String.format("%s %s producer handled total = %s last period = %s", now, threadName, completedTaskCount, periodCompletedCount));
    }
}
