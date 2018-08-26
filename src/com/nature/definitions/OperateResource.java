package com.nature.definitions;

/**
 * 资源
 */
public interface OperateResource {

    /**
     * 获取优先级
     *
     * @return 优先值
     */
    int getPriority();

    /**
     * 设置优先级
     *
     * @param priority 优先级
     */
    void setPriority(int priority);
}
