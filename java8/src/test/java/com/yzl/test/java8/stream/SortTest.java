package com.yzl.test.java8.stream;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yzl
 * @date 2018/11/1
 */
public class SortTest {

    @Test
    public void test1() {
        List<String> a = new ArrayList<>(Arrays.asList("1", "3", "4", "2", "5"));

        List<String> b = new ArrayList<>(a);
        b.sort(Comparator.comparing(s -> s));
        System.out.println(b);

        b = new ArrayList<>(a);
        b.sort(String::compareTo);
        System.out.println(b);


        b = new ArrayList<>(a);
        b.sort(Comparator.comparing(String::hashCode));
        System.out.println(b);

        b = a.stream().sorted(String::compareTo).collect(Collectors.toList());
        System.out.println(b);
    }
}
