package com.yzl.java.compile;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.tools.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yinzuolong
 */
public class JavaCompileTest {
    private static JavaCompiler javaCompiler;

    @BeforeClass
    public static void before() {
        javaCompiler = ToolProvider.getSystemJavaCompiler();
    }

    @Test
    public void test() throws Exception {
        String javaCode = "package com.yzl.java.compile;\n" +
                "\n" +
                "public class MyClass1 {\n" +
                "\n" +
                "    public int add(int a, int b) {\n" +
                "        return a + b;\n" +
                "    }\n" +
                "}";
        JavaFileObject javaFileObject = new JavaSourceFromString("com.yzl.java.compile.MyClass1", javaCode);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(diagnostics, null, null));

        Boolean b = compiler.getTask(null, fileManager, diagnostics, getOptions(), null, Collections.singletonList(javaFileObject)).call();
        if (b != null && b) {
            JavaClassObject jco = fileManager.getJavaClassObject();
            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(this.getClass().getClassLoader());
            Class clazz = dynamicClassLoader.loadClass("com.yzl.java.compile.MyClass1", jco);
            System.out.println(clazz);

        } else {
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                System.out.println(compilePrint(diagnostic));
            }
        }

    }

    private List<String> getOptions() {
        List<String> options = new ArrayList<>();
        options.add("-encoding");
        options.add("UTF-8");
        options.add("-classpath");
        options.add(getClassPath());
        return options;
    }

    private String getClassPath() {
        StringBuilder sb = new StringBuilder();
        for (URL url : ((URLClassLoader) this.getClass().getClassLoader()).getURLs()) {
            String p = url.getFile();
            sb.append(p).append(File.pathSeparator);
        }
        return sb.toString();
    }

    private String compilePrint(Diagnostic diagnostic) {
        System.out.println("Code:" + diagnostic.getCode());
        System.out.println("Kind:" + diagnostic.getKind());
        System.out.println("Position:" + diagnostic.getPosition());
        System.out.println("Start Position:" + diagnostic.getStartPosition());
        System.out.println("End Position:" + diagnostic.getEndPosition());
        System.out.println("Source:" + diagnostic.getSource());
        System.out.println("Message:" + diagnostic.getMessage(null));
        System.out.println("LineNumber:" + diagnostic.getLineNumber());
        System.out.println("ColumnNumber:" + diagnostic.getColumnNumber());
        return ("Code:[" + diagnostic.getCode() + "]\n") +
                "Kind:[" + diagnostic.getKind() + "]\n" +
                "Position:[" + diagnostic.getPosition() + "]\n" +
                "Start Position:[" + diagnostic.getStartPosition() + "]\n" +
                "End Position:[" + diagnostic.getEndPosition() + "]\n" +
                "Source:[" + diagnostic.getSource() + "]\n" +
                "Message:[" + diagnostic.getMessage(null) + "]\n" +
                "LineNumber:[" + diagnostic.getLineNumber() + "]\n" +
                "ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n";
    }
}
