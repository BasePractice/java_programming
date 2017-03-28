package ru.mirea.ippo.thread;

public final class SimpleThread extends Thread {

    private SimpleThread() {
        super("SimpleThread");
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        SimpleThread t = new SimpleThread();
        t.start();
        System.out.println(Thread.currentThread().getName());
    }
}
