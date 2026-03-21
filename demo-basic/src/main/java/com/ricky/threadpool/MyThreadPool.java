package com.ricky.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {

    private final int corePoolSize;
    private final int maxSize;
    private final int timeout;
    private final TimeUnit timeUnit;
    public final BlockingQueue<Runnable> blockingQueue;
    private final RejectHandle rejectHandle;

    public MyThreadPool(int corePoolSize, int maxSize, int timeout, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue, RejectHandle rejectHandle) {
        this.corePoolSize = corePoolSize;
        this.maxSize = maxSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.blockingQueue = blockingQueue;
        this.rejectHandle = rejectHandle;
    }

    private List<Thread> coreList = new ArrayList<>();
    private List<Thread> supportList = new ArrayList<>();

    public void execute(Runnable command) {
        if (coreList.size() < corePoolSize) { // 线程数量不够
            Thread thread = new CoreThread(command);
            coreList.add(thread);
            thread.start();
            return;
        }
        if (blockingQueue.offer(command)) { // 阻塞队列还有空间
            return;
        }
        if (coreList.size() + supportList.size() < maxSize) { // 辅助队列还有空间
            Thread thread = new SupportThread(command);
            supportList.add(thread);
            thread.start();
            return;
        }
        if (!blockingQueue.offer(command)) {
            rejectHandle.reject(command, this);
        }
    }

    // 线程执行的任务是固定的，但可以让线程做这样的任务：不断从阻塞队列中取任务并执行
    class CoreThread extends Thread {
        private final Runnable firstTask;

        CoreThread(Runnable firstTask) {
            this.firstTask = firstTask;
        }

        @Override
        public void run() {
            firstTask.run();
            while (true) {
                try {
                    Runnable command = blockingQueue.take();
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    class SupportThread extends Thread {
        final Runnable firstTask;

        SupportThread(Runnable firstTask) {
            this.firstTask = firstTask;
        }

        @Override
        public void run() {
            firstTask.run();
            while (true) {
                try {
                    Runnable command = blockingQueue.poll(timeout, timeUnit); // 超时时间后没有拿到阻塞队列中的元素就返回null
                    if (command == null) {
                        break;
                    }
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName() + "线程结束了！");
            supportList.remove(Thread.currentThread());
        }
    }

}
