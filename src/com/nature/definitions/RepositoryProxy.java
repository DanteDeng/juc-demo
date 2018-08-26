package com.nature.definitions;

/**
 * 仓库代理
 *
 * @param <T>
 */
public interface RepositoryProxy<T> extends Watchable {

    /**
     * 存放
     *
     * @param t 放入队列的对象
     * @return 放入成功
     */
    T put(T t);

    /**
     * 存放
     *
     * @param t        放入队列的对象
     * @param priority 优先级
     * @return 放入成功
     */
    T putByPriority(T t, int priority);

    /**
     * 弹出元素
     *
     * @return 被取出元素
     */
    T pop();

    /**
     * 按优先级弹出
     *
     * @param priority 优先级
     * @return 被取出元素
     */
    T popByPriority(int priority);
}
