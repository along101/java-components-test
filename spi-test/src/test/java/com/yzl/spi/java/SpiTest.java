package com.yzl.spi.java;

import org.junit.Test;

import java.util.ServiceLoader;

public class SpiTest {

    @Test
    public void test() {
        ServiceLoader<SpiInterface> loaders = ServiceLoader.load(SpiInterface.class);
        for (SpiInterface spiInterface : loaders) {
            spiInterface.test();
        }
    }
}
