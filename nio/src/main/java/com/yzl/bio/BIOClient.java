package com.yzl.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author yutu
 * @date 2018/12/13
 */
public class BIOClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 8002));
        OutputStream os = socket.getOutputStream();
        os.write("123123".getBytes());
        InputStream in = socket.getInputStream();
        byte[] b = new byte[1024];
        int n = in.read(b);
        System.out.println(new String(b, 0, n));
        socket.close();
    }
}
