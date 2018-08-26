package com.nature.definitions;

import java.util.concurrent.Callable;

/**
 * 操作线程
 */
public interface OperationCallable<T> extends Callable<T> {

    /**
     * 设置数据源
     *
     * @param repository 仓库
     */
    void setRepository(RepositoryProxy<T> repository);

}
