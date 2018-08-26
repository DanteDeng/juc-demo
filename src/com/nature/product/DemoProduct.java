package com.nature.product;

import com.nature.definitions.OperateResource;

/**
 * 示例产品
 */
public class DemoProduct implements OperateResource {
    /**
     * 优先级
     */
    private int priority;

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "DemoProduct{" +
                "priority=" + priority +
                '}';
    }
}
