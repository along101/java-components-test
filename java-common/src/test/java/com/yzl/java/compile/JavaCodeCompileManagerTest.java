package com.yzl.java.compile;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author yzl
 * @date 2019-04-19
 */
public class JavaCodeCompileManagerTest {

    @Test
    public void test() {
        String name = "com.test.java.compile.MyClass1";
        String javaCode = "package com.test.java.compile;\n" +
                "\n" +
                "public class MyClass1 {\n" +
                "\n" +
                "    public int add(int a, int b) {\n" +
                "        return a + b;\n" +
                "    }\n" +
                "} class A{}";
        JavaCodeCompileManager manager = new JavaCodeCompileManager();
        CompileResult result = manager.compileAndloadClass(name, javaCode);
        System.out.println(result.getMessage());
        Assert.assertTrue(result.isSuccess());
        Class clazz = manager.getClass(name);
        Assert.assertNotNull(clazz);
    }
}
