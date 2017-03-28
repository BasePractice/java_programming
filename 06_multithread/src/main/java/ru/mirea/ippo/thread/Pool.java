package ru.mirea.ippo.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class Pool {

    public static void main(String[] args) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Runnable runnable = () -> System.out.println(Thread.currentThread().getName());
        service.submit(runnable);
        service.shutdown();
        ThreadFactory factory = new ThreadFactory() {
            private int count = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("%d поток", count++));
            }
        };
        service = Executors.newFixedThreadPool(1, factory);
        service.submit(runnable);
        service.submit(runnable);
        service.submit(runnable);
        service.submit(runnable);
        service.submit(runnable);
        service.shutdown();
        service = Executors.newFixedThreadPool(10, factory);
        service.submit(runnable);
        service.submit(runnable);
        service.submit(runnable);
        service.submit(runnable);
        service.submit(runnable);
        service.shutdown();
    }
}
