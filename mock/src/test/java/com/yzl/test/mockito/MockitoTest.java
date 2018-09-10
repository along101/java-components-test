package com.yzl.test.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
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
        //arrange
        Iterator i = mock(Iterator.class);
        when(i.next()).thenReturn("Hello").thenReturn("World");
        //act
        String result = i.next() + " " + i.next();
        //verify
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
        OutputStream outputStream = mock(OutputStream.class);
        //预设当流关闭时抛出异常
        doThrow(new IOException()).when(outputStream).close();
        outputStream.close();
    }

    @Test
    public void argumentMatchersTest() {
        List<String> mock = mock(List.class);
        when(mock.get(anyInt())).thenReturn("Hello").thenReturn("World");
        String result = mock.get(100) + " " + mock.get(200);
        verify(mock, times(2)).get(anyInt());
        assertEquals("Hello World", result);
    }

    @Test
    public void argumentMatchersTest1() {
        Map mapMock = mock(Map.class);
        when(mapMock.put(anyInt(), anyString())).thenReturn("world");
        mapMock.put(1, "hello");
        verify(mapMock).put(anyInt(), eq("hello"));
        verify(mapMock).put(1, "hello");
    }

    @Test
    public void verifyTestTest() {
        List<String> mock = mock(List.class);
        List<String> mock2 = mock(List.class);
        when(mock.get(0)).thenReturn("hello");
        mock.get(0);
        mock.get(1);
        mock.get(2);
        mock2.get(0);
        verify(mock).get(2);
        verify(mock, never()).get(3);
    }

    @Test
    public void customAnswerTest() {
        List<String> mock = mock(List.class);
        when(mock.get(4)).thenAnswer(new Answer() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Integer num = (Integer) args[0];
                if (num > 3) {
                    return "yes";
                } else {
                    throw new RuntimeException();
                }
            }
        });
        Assert.assertEquals("yes", mock.get(4));
    }

    @Test
    public void argumentCaptorTest() {
        List<String> mock = mock(List.class);
        List<String> mock2 = mock(List.class);
        mock.add("John");
        mock2.add("Brian");
        mock2.add("Jim");
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(mock).add(argument.capture());
        assertEquals("John", argument.getValue());
        verify(mock2, times(2)).add(argument.capture());
        assertEquals("Jim", argument.getValue());
        assertArrayEquals(new Object[]{"John","Brian", "Jim"}, argument.getAllValues().toArray());
    }

    @Test
    public void argumentCaptorTest1() {
        Map<String,String> myMap = new HashMap<>();
        myMap.put("a","A");
        myMap.put("b","B");
        myMap.put("c","C");
        Map<String,String> mock = mock(Map.class);

        when(mock.get(anyString())).thenAnswer((Answer<String>) invocation -> {
            String arg = invocation.getArgumentAt(0,String.class);
            return myMap.get(arg);
        });

        String a = mock.get("a");
        Assert.assertEquals("A",a);
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
}
