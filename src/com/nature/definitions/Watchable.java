package com.nature.definitions;

import java.util.Map;

/**
 * 可监控
 */
public interface Watchable {

    /**
     * 处理监控
     *
     * @param shared 共享资源
     */
    void handleWatch(Map<String, Object> shared);
}
