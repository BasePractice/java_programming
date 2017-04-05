package ru.mirea.ippo.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;

public final class NioFile implements CompletionHandler<Integer, NioFile.Attachment> {

    private final CountDownLatch cd = new CountDownLatch(1);

    private NioFile(String writeFileName, String content) throws IOException {
        Path path = Paths.get(writeFileName);
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                path,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ByteBuffer buffer = ByteBuffer.wrap(content.getBytes("UTF-8"));
        Attachment attachment = new Attachment(channel, buffer);
        channel.write(buffer, 0, attachment, this);
    }

    private NioFile(String writeFileName, Path src) throws IOException {
        Path path = Paths.get(writeFileName);
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                path,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.DELETE_ON_CLOSE);
        FileChannel srcChannel = FileChannel.open(src, StandardOpenOption.READ, StandardOpenOption.DELETE_ON_CLOSE);
        MappedByteBuffer map = srcChannel.map(FileChannel.MapMode.READ_ONLY, 0, srcChannel.size());
        Attachment attachment = new Attachment(channel, map);
        channel.write(map, 0, attachment, this);
    }

    @Override
    public void completed(Integer result, Attachment attachment) {
        System.out.println(String.format("Written: %d bytes", result));
        cd.countDown();
        try {
            attachment.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        cd.countDown();
        try {
            attachment.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final class Attachment {
        final Channel channel;
        final ByteBuffer buffer;

        private Attachment(Channel channel, ByteBuffer buffer) {
            this.channel = channel;
            this.buffer = buffer;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new NioFile("w.txt", "ASYNCIO").cd.await();
        new NioFile("w1.txt", Paths.get("w.txt")).cd.await();
    }
}
