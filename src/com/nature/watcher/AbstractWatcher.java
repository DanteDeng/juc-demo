package com.nature.watcher;

import com.nature.definitions.ExecutorService;
import com.nature.definitions.Watchable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractWatcher<T extends Watchable> implements ExecutorService<T> {

    ScheduledFuture<?> scheduledFuture;
    /**
     * 被监控对象集合
     */
    private final List<T> subjects = new ArrayList<>();
    /**
     * 定时任务执行器
     */
    private final ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(1);
    /**
     * 被监控对象共享的资源区
     */
    private final Map<String, Object> shared = new HashMap<>();

    private WatchRunner runner = new WatchRunner();

    /**
     * 服务启动
     */
    public void start() {
        scheduledFuture = service.scheduleAtFixedRate(runner, 0, 3, TimeUnit.SECONDS);
    }

    public boolean addWorker(T worker) {
        return subjects.add(worker);
    }

    public boolean removeWorker(T worker) {
        return subjects.add(worker);
    }

    public void destroy() {
        service.shutdown();
    }


    private class WatchRunner implements Runnable {

        @Override
        public void run() {
            System.out.println("======================================================================================");
            for (Watchable subject : subjects) {
                subject.handleWatch(shared);
            }
            System.out.println("======================================================================================");
        }
    }
}
