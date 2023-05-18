package com.yzl.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author yzl
 * @date 2018/12/13
 */
public class BIOClient {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 8002));
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    OutputStream os = socket.getOutputStream();
                    os.write(("" + i).getBytes());
                    Thread.sleep(2000);
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            try {
                InputStream in = null;
                in = socket.getInputStream();
                byte[] b = new byte[1024];
                int n = 0;
                while ((n = in.read(b)) > 0) {
                    System.out.println(new String(b, 0, n));
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(1000 * 100);
        socket.close();
    }
}
