package com.nature.repository;

import com.nature.definitions.RepositoryProxy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 示例仓库
 */
public class DemoRepository<T> implements RepositoryProxy<T> {
    /**
     * 假定的最大容量
     */
    private int maxVolume;
    /**
     * 最大同时访问资源数量
     */
    private int maxAccess;
    /**
     * 默认优先级0（最低优先级）
     */
    private static final int PRIORITY_DEFAULT = 0;
    /**
     * 仓库
     */
    private Map<Integer, Queue<T>> queueMap;
    /**
     * 当前存放的全部优先级的有序集合
     */
    private Set<Integer> priorities;
    /**
     * 当前最大优先级
     */
    private int maxPriority = PRIORITY_DEFAULT;
    /**
     * 当前存放元素总量
     */
    private AtomicInteger counter;
    /**
     * 读写锁
     */
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 初始化
     *
     * @param maxAccess 最大访问数量
     * @param maxVolume 假定的最大容量
     */
    public DemoRepository(int maxAccess, int maxVolume) {
        this.maxAccess = maxAccess;
        this.maxVolume = maxVolume;
        // 仓库使用分区方式，所以使用hashmap
        queueMap = new ConcurrentHashMap<>();
        // 初始分区
        queueMap.put(PRIORITY_DEFAULT, new LinkedBlockingDeque<>());
        // 分区类别集合初始化
        priorities = new TreeSet<>();
        priorities.add(PRIORITY_DEFAULT);
        // 计数器初始化
        counter = new AtomicInteger();
    }

    /**
     * 存放
     *
     * @param t 放入队列的对象
     * @return 放入数据
     */
    @Override
    public T put(T t) {

        return putByPriority(t, PRIORITY_DEFAULT);
    }

    /**
     * 按优先级存放（指定分区存放）
     *
     * @param t        放入队列的对象
     * @param priority 优先级
     * @return 放入数据
     */
    @Override
    public T putByPriority(T t, int priority) {

        Queue<T> ts = getOrCreateQueueByPriority(priority);
        boolean offer = ts.offer(t);// 存放
        if (offer) {
            counter.incrementAndGet(); // 成功计数
            return t;
        } else {
            return null;
        }

    }

    /**
     * 获取指定优先级队列
     *
     * @param priority 优先级
     * @return 取出数据
     */
    private Queue<T> getOrCreateQueueByPriority(int priority) {
        // 这里需要注意线程安全问题，使用互斥锁
        Lock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            Queue<T> queue;

            if (queueMap.containsKey(priority)) {
                queue = queueMap.get(priority);
            } else {
                queue = new LinkedBlockingDeque<>();
                priorities.add(priority);
                queueMap.put(priority, queue);
            }
            if (maxPriority < priority) { // 更新最大优先级
                maxPriority = priority;
            }
            return queue;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 获取指定优先级队列
     *
     * @param priority 优先级
     * @return 取出数据
     */
    private Queue<T> getQueueByPriority(int priority) {
        Queue<T> queue = null;
        if (queueMap.containsKey(priority)) {
            queue = queueMap.get(priority);
        }
        return queue;
    }

    /**
     * 取出（优先最高优先级）
     *
     * @return 取出数据
     */
    @Override
    public T pop() {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            return popItem(maxPriority, true);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 按优先级取出（低于或者等于指定优先级）
     *
     * @param priority 优先级
     * @return 取出数据
     */
    @Override
    public T popByPriority(int priority) {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            boolean fromMax = false;
            if (priority >= maxPriority) {
                fromMax = true;
            }
            return popItem(priority, fromMax);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 取出操作逻辑
     *
     * @param priority 优先级
     * @param fromMax  是否从最大优先级开始
     * @return 取出数据
     */
    private T popItem(int priority, boolean fromMax) {
        Queue<T> queue = getQueueByPriority(priority);
        if (queue != null) {
            T poll = queue.poll();
            if (poll != null || priority == 0) {    // 获取到有值或者已经达到最低优先级返回结果
                if (poll != null) {
                    if (fromMax) {  // 从最大优先级取的话取到元素的优先级即更新为当前最大优先级
                        maxPriority = priority;
                    }
                    counter.decrementAndGet();  // 取出成功计数减1
                }
                return poll;
            }
        }
        return popItem(getNextPriority(priority), fromMax);
    }

    /**
     * 获取小于且最接近指定优先级的优先级
     *
     * @param priority 优先级
     * @return 下一个优先级
     */
    private int getNextPriority(int priority) {
        if (priority > 0) {
            Integer lower = ((TreeSet<Integer>) priorities).lower(priority);
            return lower == null ? 0 : lower;
        } else {
            return 0;
        }

    }

    /**
     * 监控逻辑
     *
     * @param shared 共享资源
     */
    @Override
    public void handleWatch(Map<String, Object> shared) {
        Date now = new Date();
        String threadName = Thread.currentThread().getName();

        int currVolume = counter.get();
        int position = 0;  // 仓位 0 --> maxAccess
        if (currVolume != 0) {
            int num = currVolume * maxAccess / maxVolume;
            if (num > maxAccess) {
                num = maxAccess;
            }
            position = num;
        }
        System.out.println(String.format("%s %s repository resource count = %s  position = %s ", now, threadName, counter, position));
        shared.put("position", position);
        shared.put("maxAccess", maxAccess);
    }
}
