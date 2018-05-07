package com.yzl.archaius;

import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringListProperty;
import com.netflix.config.DynamicStringSetProperty;

/**
 * @author yinzuolong
 */
public class Main {
    private static final DynamicLongProperty timeToWait =
            DynamicPropertyFactory.getInstance().getLongProperty("lock.waitTime", 1000);
    private static DynamicStringListProperty prop = new DynamicStringListProperty("list", "1,1");

    public static void main(String[] args) throws Exception {
        timeToWait.addCallback(() -> {
            System.out.println("timeToWait callback, new value: " + timeToWait.get());
        });
        for (int i = 0; i < 100000; i++) {
            Thread.sleep(1000);
            System.out.println("timeToWait: " + timeToWait.get());
            System.out.println("list: " + prop.get());
        }

        DynamicStringSetProperty prop = new DynamicStringSetProperty("test4", "a,b,c");
    }
}
