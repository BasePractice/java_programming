package ru.mirea.ippo.thread;


import java.math.BigInteger;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public final class ForkJoin {
    private static final AtomicInteger COUNT = new AtomicInteger(0);
    private static final class Job extends RecursiveTask<BigInteger> {
        private final long[] buffer;
        private final int start;
        private final int end;

        private Job(long[] buffer, int start, int end) {
            this.buffer = buffer;
            this.start = start;
            this.end = end;
        }

        @Override
        protected BigInteger compute() {
            BigInteger result = BigInteger.ZERO;
            if (end - start < 100) {
                for (int i = start; i < end; ++i)
                    result = result.add(BigInteger.valueOf(buffer[i]));
                COUNT.incrementAndGet();
            } else {
                int mid = (end + start) >>> 1;
                ForkJoinTask<BigInteger> t1 = new Job(buffer, start, mid);
                ForkJoinTask<BigInteger> t2 = new Job(buffer, mid, end);
                t1.fork();
                t2.fork();

                result = result.add(t2.join());
                result = result.add(t1.join());
            }
            return result;
        }
    }

    private static final Random r = new Random(new Date().getTime());

    public static void main(String[] args) {
        final long [] buffer = new long[1_000_000];
        for (int i = 0; i < buffer.length; i++) {
            long l = r.nextLong();
            buffer[i] = l < 0 ? l * -1 : l;
        }
        BigInteger result = ForkJoinPool.commonPool().invoke(new Job(buffer, 0, buffer.length));
        System.out.println("Result: " + result);
        System.out.println("Jobs  : " + COUNT.intValue());
    }
}
