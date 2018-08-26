package com.nature.definitions;

import java.util.concurrent.Callable;

/**
 * 执行器
 *
 * @param <T>
 */
public interface ExecutorService<T> extends Watchable {

    /**
     * 重新分配资源
     *
     * @param resourceCount 资源数
     */
    void redistribute(int resourceCount);

    /**
     * 服务启动
     */
    void start();

    /**
     * 增加处理任务
     *
     * @param worker 任务
     * @return 是否成功
     */
    boolean addWorker(T worker);

    /**
     * 移除任务
     *
     * @param worker 任务
     * @return 是否成功
     */
    boolean removeWorker(T worker);

    /**
     * 关闭服务（销毁）
     */
    void destroy();
}
