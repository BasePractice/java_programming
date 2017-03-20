package ru.mirea.ippo.stdlib.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class NioNetwork {
    private static final CountDownLatch START = new CountDownLatch(1);
    private static final int PORT = 3487;
    private static final InetSocketAddress SOCKET_ADDRESS = new InetSocketAddress("localhost", PORT);

    private static final class PingServer implements Runnable {
        private final Selector selector;

        PingServer() throws IOException {
            selector = Selector.open();
        }

        @Override
        public void run() {
            ServerSocketChannel serverChannel;
            try {
                serverChannel = ServerSocketChannel.open();
                serverChannel.configureBlocking(false);
                serverChannel.socket().bind(SOCKET_ADDRESS);
                serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                boolean isRunning = true;
                while (isRunning) {
                    START.countDown();
                    selector.select();
                    Iterator keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = (SelectionKey) keys.next();
                        keys.remove();

                        if (!key.isValid()) {
                            continue;
                        }

                        if (key.isAcceptable()) {
                            accept(key);
                        } else if (key.isReadable()) {
                            if (read(key, "PING")) {
                                key.interestOps(SelectionKey.OP_WRITE);
                            }
                        } else if (key.isWritable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            socketChannel.write(ByteBuffer.wrap("PONG".getBytes("UTF-8")));
                            isRunning = false;
                            socketChannel.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void accept(SelectionKey key) throws IOException {
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverChannel.accept();
            channel.configureBlocking(false);
            Socket socket = channel.socket();
            SocketAddress remoteAddress = socket.getRemoteSocketAddress();
            System.out.println("Connected to: " + remoteAddress);
            channel.register(this.selector, SelectionKey.OP_READ);
        }
    }

    private static boolean read(SelectionKey key, String et) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead;
        numRead = channel.read(buffer);

        if (numRead == -1) {
            Socket socket = channel.socket();
            SocketAddress remoteAddress = socket.getRemoteSocketAddress();
            System.out.println("Connection closed by client: " + remoteAddress);
            channel.close();
            key.cancel();
            return false;
        }

        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);
        String content = new String(data, "UTF-8");
        System.out.println("Got: " + content);
        return et.equals(content);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new PingServer());
        START.await();
        SocketChannel client = SocketChannel.open();
        client.configureBlocking(false);
        Selector selector = Selector.open();
        client.register(selector, SelectionKey.OP_CONNECT);
        client.connect(SOCKET_ADDRESS);
        boolean isRunning = true;
        while (isRunning) {
            selector.select();
            Iterator keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();
                keys.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isReadable()) {
                    if (read(key, "PONG")) {
                        isRunning = false;
                    }
                } else if (key.isConnectable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    socketChannel.finishConnect();
                    key.interestOps(SelectionKey.OP_WRITE);
                } else if (key.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    socketChannel.write(ByteBuffer.wrap("PING".getBytes("UTF-8")));
                    key.interestOps(SelectionKey.OP_READ);
                }
            }
        }
        client.close();
        service.shutdown();
    }

}
