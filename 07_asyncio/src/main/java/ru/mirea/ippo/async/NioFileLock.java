package ru.mirea.ippo.async;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class NioFileLock {
    private static final CountDownLatch STARTER = new CountDownLatch(1);
    private static final CountDownLatch COMPLETE = new CountDownLatch(2);
    private static final String FILE_NAME = "w.txt";

    private static final class WriteLock implements Runnable {

        private final long start;
        private final long end;
        private final char symbol;

        private WriteLock(long start, long end, char symbol) {
            this.start = start;
            this.end = end;
            this.symbol = symbol;
        }

        @Override
        public void run() {
            try {
                STARTER.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            try (FileChannel channel = new RandomAccessFile(FILE_NAME, "rw").getChannel()) {
                readAndWriteLocked(channel, start, end, symbol);
            } catch (Exception e) {
                e.printStackTrace();
            }
            COMPLETE.countDown();
        }

        private static void readAndWriteLocked(FileChannel channel, long start, long end, char symbol)
                throws IOException {
            FileLock lock = channel.lock(start, end, true);
            ByteBuffer buffer = ByteBuffer.allocate((int) (end - start));
            try {
                lock.channel().read(buffer, start);
                System.out.println(String.format("Read: %s", new String(buffer.array(), "UTF-8")));
                buffer.put(0, (byte) symbol);
                buffer.flip();
                lock.channel().write(buffer, start);
            } finally {
                lock.release();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FileChannel channel = FileChannel.open(Paths.get(FILE_NAME),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.wrap("CONTENTCONTENT".getBytes("UTF-8"));
        int capacity = buffer.capacity();
        channel.write(buffer);
        channel.close();
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new WriteLock(0, capacity / 2, 'W'));
        service.submit(new WriteLock(capacity / 2, capacity, 'P'));
        service.shutdown();
        STARTER.countDown();
        COMPLETE.await();
        try (FileChannel readChannel = FileChannel.open(Paths.get(FILE_NAME),
                StandardOpenOption.READ, StandardOpenOption.DELETE_ON_CLOSE)) {
            buffer.flip();
            readChannel.read(buffer);
            System.out.println(String.format("Result: %s", new String(buffer.array(), "UTF-8")));
        }
    }
}
