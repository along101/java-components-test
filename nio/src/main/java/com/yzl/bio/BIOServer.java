package com.yzl.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author yutu
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
                    int n = in.read(b);
                    System.out.println(new String(b, 0, n, StandardCharsets.UTF_8));

                    OutputStream os = socket.getOutputStream();
                    os.write("received : 123123".getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
