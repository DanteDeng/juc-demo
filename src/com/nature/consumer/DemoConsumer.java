package com.nature.consumer;

import com.nature.definitions.OperateResource;
import com.nature.definitions.OperationCallable;
import com.nature.definitions.RepositoryProxy;
import com.nature.product.DemoProduct;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 示例消费者
 */
public class DemoConsumer implements OperationCallable<DemoProduct> {

    private RepositoryProxy<DemoProduct> repository;

    @Override
    public DemoProduct call() throws Exception {
        Random random = new Random();
        // 消费资源
        DemoProduct demoProduct = repository.pop();
        //TimeUnit.MILLISECONDS.sleep(500 + random.nextInt(500));
        TimeUnit.MILLISECONDS.sleep(1000);
        Date now = new Date();
        String threadName = Thread.currentThread().getName();
        System.out.println(String.format("%s %s consumer %s", now, threadName, demoProduct));
        return demoProduct;
    }

    @Override
    public void setRepository(RepositoryProxy<DemoProduct> repository) {
        this.repository = repository;
    }
}
