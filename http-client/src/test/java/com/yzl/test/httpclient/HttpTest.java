package com.yzl.test.httpclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author yutu
 * @date 2020-10-16
 */
public class HttpTest {

    @Before
    public void setUp() throws Exception {
        TestServer.start();
    }

    @After
    public void tearDown() throws Exception {
        TestServer.stop();
    }

    @Test
    public void test() throws Exception {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(50000)
                .setConnectionRequestTimeout(2)//从连接池中获取连接超时时间
                .build();

        //配置io线程
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().
                setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setSoKeepAlive(true)
                .build();
        //设置连接池大小
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        connManager.setMaxTotal(1);//最大连接数设置1
        connManager.setDefaultMaxPerRoute(1);//per route最大连接数设置1


        final CloseableHttpAsyncClient client = HttpAsyncClients.custom().
                setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
        client.start();

        TimeUnit.SECONDS.sleep(10);
        //构造请求
        for (int i = 0; i < 2; i++) {
            String url = "http://localhost:8080/test?p=" + i;
            HttpGet httpGet = new HttpGet(url);
            client.execute(httpGet, new CallBack());
            TimeUnit.SECONDS.sleep(5);
        }

        for (int i = 0; i < 1000000; i++) {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("i=" + i);
        }
    }

    static class CallBack implements FutureCallback<HttpResponse> {

        private long start = System.currentTimeMillis();

        CallBack() {
        }

        public void completed(HttpResponse httpResponse) {
            try {
                System.out.println("cost is:" + (System.currentTimeMillis() - start) + ":" + EntityUtils.toString(httpResponse.getEntity()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void failed(Exception e) {
            e.printStackTrace();
            System.err.println(" cost is:" + (System.currentTimeMillis() - start) + ":" + e);
        }

        public void cancelled() {

        }
    }
}
