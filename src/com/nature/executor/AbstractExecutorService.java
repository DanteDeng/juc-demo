package com.nature.executor;

import com.nature.definitions.ExecutorService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 执行器
 */
public abstract class AbstractExecutorService<T extends Callable> implements ExecutorService<T> {
    /**
     * 上次监控资源数
     */
    private int lastResourceCount = -1;
    /**
     * 上次监控已处理数量
     */
    long lastCompleteCount;
    /**
     * 任务集合
     */
    private List<T> workers = new ArrayList<>();

    /**
     * 执行器
     */
    ThreadPoolExecutor service = new ThreadPoolExecutor(10, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    /**
     * 从新分配资源
     *
     * @param resourceCount 资源数量
     */
    @Override
    public void redistribute(int resourceCount) {
        if (resourceCount != lastResourceCount) {
            lastResourceCount = resourceCount; // 最近资源数更新
            // 调整线程数量
            service.setCorePoolSize(resourceCount);
            service.setMaximumPoolSize(resourceCount + 1);
        }
    }

    /**
     * 服务启动
     */
    @Override
    public void start() {
        new Thread(() -> {
            while (true) {
                long taskCount = service.getTaskCount();
                long completedTaskCount = service.getCompletedTaskCount();
                long toExecute = taskCount - completedTaskCount;
                if (toExecute < 100) {
                    for (Callable<T> command : workers) {
                        service.submit(command);
                    }
                } else {
                    Date now = new Date();
                    String threadName = Thread.currentThread().getName();
                    System.out.println(String.format("%s %s 1 seconds to sleep and toExecute =  %s ", now, threadName, toExecute));
                    try {
                        TimeUnit.SECONDS.sleep(1L);
                    } catch (InterruptedException e) {
                        System.out.println(String.format("%s %s 1 seconds to sleep error happens ", now, threadName));
                    }
                }
            }
        }).start();

    }

    /**
     * 增加工作任务
     *
     * @param command 任务
     * @return 是否成功
     */
    @Override
    public boolean addWorker(T command) {
        return workers.add(command);
    }

    /**
     * 移除任务
     *
     * @param command 任务
     * @return 是否成功
     */
    @Override
    public boolean removeWorker(T command) {
        return workers.remove(command);
    }

    /**
     * 关闭服务（销毁）
     */
    @Override
    public void destroy() {
        service.shutdown();
    }

}
