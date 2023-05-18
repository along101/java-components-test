package com.yzl.test;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yutu
 * @date 2023/5/18
 */
public class GroovyTest {

    private static Map<String, Script> cache = new ConcurrentHashMap<>();

    @Test
    public void test() throws Exception {
        String script = "def a = s+1";
        Map<String, Object> vars = new HashMap<>();
        vars.put("s", 3L);

        long n = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Object result = run(script, vars);
            System.out.println(result);
        }
        System.out.println("cost: " + (System.currentTimeMillis() - n));
    }

    public Object run(String scriptString, Map<String, Object> vars) throws Exception {
        Script groovy = compile(scriptString);
        Binding binding = new Binding(vars);
        groovy.setBinding(binding);
        return groovy.run();
    }

    public Script compile(String scriptStr) throws Exception {
        String md5 = md5(scriptStr);
        Script script = cache.get(md5);
        if (script != null) {
            return cache.get(md5);
        }
        synchronized (this) {
            GroovyClassLoader classLoader = new GroovyClassLoader();
            Class scriptClass = classLoader.parseClass(scriptStr);
            script = (Script) scriptClass.newInstance();
            classLoader.clearCache();
            ClassInfo.clearModifiedExpandos();
            InvokerHelper.removeClass(scriptClass);
            cache.put(md5, script);
            return script;
        }
    }

    private String md5(String content) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(content.getBytes());
        byte[] hash = md.digest();

        String res = new BigInteger(1, hash).toString(16);
        while (res.length() < 32) {
            res = "0" + res;
        }
        return res.toUpperCase();
    }
}
