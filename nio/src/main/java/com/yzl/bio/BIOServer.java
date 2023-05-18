package com.yzl.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author yzl
 * @date 2018/12/13
 */
public class BIOServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8002));
        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> {
                try {
                    InputStream in = socket.getInputStream();
                    byte[] b = new byte[1024];
                    int n = 0;
                    while ((n = in.read(b)) > 0) {
                        System.out.println(new String(b, 0, n, StandardCharsets.UTF_8));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    OutputStream os = socket.getOutputStream();
                    for (int i = 0; i < 10; i++) {
                        os.write((i + " server send\n").getBytes());
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
