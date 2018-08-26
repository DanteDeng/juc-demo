package com.nature;

import com.nature.consumer.DemoConsumer;
import com.nature.definitions.ExecutorService;
import com.nature.definitions.OperationCallable;
import com.nature.definitions.RepositoryProxy;
import com.nature.definitions.Watchable;
import com.nature.executor.DemoConsumerExecutorService;
import com.nature.executor.DemoProducerExecutorService;
import com.nature.producer.DemoProducer;
import com.nature.product.DemoProduct;
import com.nature.repository.DemoRepository;
import com.nature.watcher.DemoWatcher;

/**
 * 测试示例
 */
public class DemoTest {

    public static void main(String[] args) {
        // 仓库实例化
        RepositoryProxy<DemoProduct> repository = new DemoRepository<>(10, 100);
        // 创建生产操作
        OperationCallable<DemoProduct> producer = new DemoProducer();
        producer.setRepository(repository);
        // 创建消费操作
        OperationCallable<DemoProduct> consumer = new DemoConsumer();
        consumer.setRepository(repository);
        // 创建生产服务
        ExecutorService<OperationCallable<DemoProduct>> producers = new DemoProducerExecutorService<>();
        producers.addWorker(producer);
        // 创建消费服务
        ExecutorService<OperationCallable<DemoProduct>> consumers = new DemoConsumerExecutorService<>();
        consumers.addWorker(consumer);
        // 创建监控服务
        ExecutorService<Watchable> watcher = new DemoWatcher<>();
        watcher.addWorker(repository);
        watcher.addWorker(producers);
        watcher.addWorker(consumers);
        // 启动服务
        watcher.start();
        producers.start();
        consumers.start();
    }

}
