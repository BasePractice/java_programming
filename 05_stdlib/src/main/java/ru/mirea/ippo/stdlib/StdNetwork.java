package ru.mirea.ippo.stdlib;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class StdNetwork {
    private static final int TIMEOUT = 3000;
    private static final int PORT = 3487;
    private static final InetSocketAddress SOCKET_ADDRESS = new InetSocketAddress("localhost", PORT);

    private static final class PingServer implements Runnable {

        @Override
        public void run() {
            try {
                ServerSocket listen = new ServerSocket(PORT, 2, SOCKET_ADDRESS.getAddress());
                listen.setReuseAddress(true);
                System.out.println("Listen: " + SOCKET_ADDRESS);
                Socket socket = listen.accept();
                socket.setSoTimeout(TIMEOUT);
                process(socket);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.flush();
        }

        private void process(Socket socket) throws IOException {
            System.out.println("Connected: " + socket.getRemoteSocketAddress());
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            System.out.println(new String(
                    ByteStreams.toByteArray(socket.getInputStream()), "UTF-8"));
            writer.print("PONG");
            writer.flush();
        }
    }

    private static final class PingClient implements Runnable {

        @Override
        public void run() {
            try {
                Socket socket = new Socket();
                socket.setSoTimeout(TIMEOUT);
                socket.connect(SOCKET_ADDRESS);
                System.out.println("Connected: " + SOCKET_ADDRESS);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.print("PING");
                writer.flush();
                socket.shutdownOutput();//FIXME: Wat?
                System.out.println(new String(
                        ByteStreams.toByteArray(socket.getInputStream()), "UTF-8"));
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new PingServer());
        PingClient client = new PingClient();
        client.run();
        service.shutdown();
    }
}
