package com.nature.watcher;

import com.nature.definitions.Watchable;

import java.util.Map;

/**
 * 实例观察者
 */
public class DemoWatcher<T extends Watchable> extends AbstractWatcher<T> {

    /**
     * 重新分配
     *
     * @param resourceCount 资源数
     */
    @Override
    public void redistribute(int resourceCount) {
        // 取消任务后重启
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
        }
        while (true) {
            if (scheduledFuture != null && scheduledFuture.isCancelled()) {
                start();// 重启
                break;
            }
        }
    }

    @Override
    public void handleWatch(Map<String, Object> shared) {
        // 可以进行公共资源后续处理
    }


}
