package com.yzl.java.compile;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yzl
 * @date 2019-04-24
 */
public class PreHandlerGeneratorTest {
    @Test
    public void test() {
        PreHandlerGenerator preHandlerGenerator = new PreHandlerGenerator(new JavaCodeCompileManager());
        Map<String, String> methodCodeMap = new HashMap<>();
        methodCodeMap.put("preExecute", "");
        PreHandler preHandler = preHandlerGenerator.generate("TestHandler", methodCodeMap);
        boolean result = preHandler.preExecute(null);
        Assert.assertTrue(result);

        methodCodeMap.put("preExecute", "System.out.println(\"preExecute\"); return false;");
        preHandler = preHandlerGenerator.generate("TestHandler", methodCodeMap);
        result = preHandler.preExecute(null);
        Assert.assertFalse(result);
    }
}
