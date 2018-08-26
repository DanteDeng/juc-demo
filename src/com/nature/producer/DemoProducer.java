package com.nature.producer;

import com.nature.definitions.OperationCallable;
import com.nature.definitions.RepositoryProxy;
import com.nature.product.DemoProduct;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * 示例生产者
 */
public class DemoProducer implements OperationCallable<DemoProduct> {

    private RepositoryProxy<DemoProduct> repository;

    @Override
    public DemoProduct call() throws Exception {
        DemoProduct product = new DemoProduct();

        Random random = new Random();

        int priority = random.nextInt(10);
        product.setPriority(priority);
        // 生产资源存库
        DemoProduct demoProduct = repository.putByPriority(product, priority);
        //TimeUnit.MILLISECONDS.sleep(300 + random.nextInt(500));
        TimeUnit.MILLISECONDS.sleep(1000);
        Date now = new Date();
        String threadName = Thread.currentThread().getName();
        System.out.println(String.format("%s %s producer %s", now, threadName, demoProduct));
        return demoProduct;
    }

    @Override
    public void setRepository(RepositoryProxy<DemoProduct> repository) {
        this.repository = repository;
    }
}
