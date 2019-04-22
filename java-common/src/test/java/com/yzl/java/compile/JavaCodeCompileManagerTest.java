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
        CompileResult result = manager.compileAndLoadClass(name, javaCode);
        System.out.println(result.getMessage());
        Assert.assertTrue(result.isSuccess());
        Assert.assertNotNull(manager.getClass(name));
        Assert.assertNotNull(manager.getClass("com.test.java.compile.A"));

        //修改后编译
        javaCode = javaCode + " class B{}";
        manager.compileAndLoadClass(name, javaCode);
        System.out.println(result.getMessage());
        Assert.assertTrue(result.isSuccess());
        Assert.assertNotNull(manager.getClass(name));
        Assert.assertNotNull(manager.getClass("com.test.java.compile.A"));
        Assert.assertNotNull(manager.getClass("com.test.java.compile.B"));
    }
}
