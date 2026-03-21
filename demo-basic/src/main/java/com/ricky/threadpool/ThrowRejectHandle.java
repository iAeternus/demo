package com.ricky.threadpool;

public class ThrowRejectHandle implements RejectHandle {
    @Override
    public void reject(Runnable rejectCommand, MyThreadPool threadPool) {
        throw new RuntimeException("阻塞队列满了！");
    }
}
