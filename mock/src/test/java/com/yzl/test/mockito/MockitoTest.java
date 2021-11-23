package com.yzl.test.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MockitoTest {

    @Test
    public void simpleTest() {
        //先mock需要的对象
        Iterator i = mock(Iterator.class);
        //给mock对象设置执行的结果
        when(i.next()).thenReturn("Hello").thenReturn("World");
        //对象方法执行
        String result = i.next() + " " + i.next();
        //mock的verify验证
        verify(i, times(2)).next();
        //assert
        assertEquals("Hello World", result);
    }

    @Test
    public void when_thenReturn() {
        //mock一个Iterator类
        Iterator iterator = mock(Iterator.class);
        //预设当iterator调用next()时第一次返回hello，第n次都返回world
        when(iterator.next()).thenReturn("hello").thenReturn("world");
        //使用mock的对象
        String result = iterator.next() + " " + iterator.next() + " " + iterator.next();
        //验证结果
        assertEquals("hello world world", result);
    }

    @Test(expected = IOException.class)
    public void when_thenThrow() throws IOException {
        //抛出异常测试
        OutputStream outputStream = mock(OutputStream.class);
        //预设当流关闭时抛出异常
        doThrow(new IOException()).when(outputStream).close();
        outputStream.close();
    }

    @Test
    public void argumentMatchersTest() {
        List mock = mock(List.class);
        when(mock.get(anyInt())).thenReturn("Hello").thenReturn("World");
        String result = mock.get(100) + " " + mock.get(200);
        //验证mock对象的get方法被调用了2次
        verify(mock, times(2)).get(anyInt());
        assertEquals("Hello World", result);
    }

    @Test
    public void argumentMatchersTest1() {
        Map mapMock = mock(Map.class);
        when(mapMock.put(anyInt(), anyString())).thenReturn("world");
        mapMock.put(1, "hello");

        //验证put方法执行，如果没有按照下面参数执行，则验证失败
        verify(mapMock).put(anyInt(), eq("hello"));
        verify(mapMock).put(1, "hello");
    }

    @Test
    public void verifyTest() {
        List<String> mock = mock(List.class);
        List<String> mock2 = mock(List.class);
        when(mock.get(0)).thenReturn("hello");
        mock.get(0);
        mock.get(1);
        mock.get(2);
        mock2.get(0);
        //验证mock对象get(2)方法被执行了
        verify(mock).get(2);
        //验证mock对象没有执行过get(3)方法
        verify(mock, never()).get(3);
    }

    @Test
    public void customAnswerTest() {
        //Answer函数返回mock方法的调用
        List<String> mock = mock(List.class);
        when(mock.get(4)).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Integer num = (Integer) args[0];
            if (num > 3) {
                return "yes";
            } else {
                throw new RuntimeException();
            }
        });
        Assert.assertEquals("yes", mock.get(4));

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Integer num = (Integer) args[0];
            return "test" + num;
        }).when(mock).get(anyInt());
        Assert.assertEquals("test2", mock.get(2));
    }

    @Test
    public void argumentCaptorTest() {
        //捕获mock方法调用的参数
        List<String> mock = mock(List.class);
        List<String> mock2 = mock(List.class);
        mock.add("John");
        mock2.add("Brian");
        mock2.add("Jim");
        //参数捕获器
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        //捕获第一次调用的参数
        verify(mock).add(argument.capture());
        assertEquals("John", argument.getValue());
        //捕获第二次调用的参数
        verify(mock2, times(2)).add(argument.capture());
        assertEquals("Jim", argument.getValue());
        //所有捕获的参数
        assertArrayEquals(new Object[]{"John", "Brian", "Jim"}, argument.getAllValues().toArray());
    }

    @Test
    public void argumentCaptorTest1() {
        Map<String, String> myMap = new HashMap<>();
        myMap.put("a", "A");
        myMap.put("b", "B");
        myMap.put("c", "C");
        Map<String, String> mock = mock(Map.class);

        when(mock.get(anyString())).thenAnswer((Answer<String>) invocation -> {
            String arg = invocation.getArgumentAt(0, String.class);
            return myMap.get(arg);
        });

        String a = mock.get("a");
        Assert.assertEquals("A", a);
    }

    @Test
    public void returnsSmartNullsTest() {
        List mock = mock(List.class, RETURNS_SMART_NULLS);
        System.out.println(mock.get(0));
        System.out.println(mock.toArray().length);

        when(mock.get(0)).thenReturn(1);
        System.out.println(mock.get(0));
        System.out.println(mock.toArray().length);
    }

    @Test
    public void doAnswer1() {
        Map<String, String> mock = mock(Map.class);
        doAnswer(i -> {
            System.out.println(i.getArguments()[0]);
            return null;
        }).when(mock).get("a");

        mock.get("a");
    }

    @Test
    public void voidTest() {
        Map<String, String> mock = mock(Map.class);
        doAnswer(i -> {
            System.out.println(i.getArguments()[0]);
            return null;
        }).when(mock).putAll(any());

        Map<String, String> myMap = new HashMap<>();
        myMap.put("a", "A");
        mock.putAll(myMap);
    }

}
