package com.yzl.test;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

/**
 * @author yzl
 * @date 2018/9/11
 */
public class StringEscapeTest {

    @Test
    public void test() {
        String json = "{\"a\":\"A\"}";
        System.out.println(json);
        String escape = StringEscapeUtils.escapeJavaScript(json);
        System.out.println(escape);
        String unescape = StringEscapeUtils.unescapeJavaScript(json);
        System.out.println(unescape);
    }
}
