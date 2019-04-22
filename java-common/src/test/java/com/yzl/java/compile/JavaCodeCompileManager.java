package com.yzl.java.compile;

import org.apache.commons.codec.digest.Md5Crypt;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * java 代码编译管理器
 *
 * @author yzl
 * @date 2019-04-20
 */
public class JavaCodeCompileManager {

    private Map<String, String> codeCache = new ConcurrentHashMap<>();

    private Map<String, DynamicClassLoader> classCache = new ConcurrentHashMap<>();

    public static CompileResult compile(String name, String code) {
        CompileResult compileResult = new CompileResult();
        JavaSourceObject javaFileObject = new JavaSourceObject(name, code);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(diagnostics, null, null));
        Boolean rs = compiler.getTask(null, fileManager, diagnostics, getOptions(),
                null, Collections.singletonList(javaFileObject))
                .call();
        compileResult.setDiagnostics(diagnostics);
        if (rs != null && rs) {
            compileResult.setSuccess(true);
            compileResult.setClassObjects(fileManager.getJavaClassObjects());
        } else {
            compileResult.setSuccess(false);
        }
        return compileResult;
    }

    public CompileResult compileAndLoadClass(String name, String code) {
        if (existCache(name, code)) {
            return CompileResult.create(true);
        }
        synchronized (this) {
            //double check
            if (existCache(name, code)) {
                return CompileResult.create(true);
            }
            CompileResult compileResult = compile(name, code);
            DynamicClassLoader dynamicClassLoader = classCache.get(name);
            //如果classLoader存在，先关闭
            if (dynamicClassLoader != null) {
                try {
                    dynamicClassLoader.close();
                } catch (IOException e) {
                    throw new RuntimeException("close classLoader error.", e);
                }
            }
            dynamicClassLoader = new DynamicClassLoader(this.getClass().getClassLoader());
            classCache.put(name, dynamicClassLoader);
            if (compileResult.isSuccess()) {
                for (JavaClassObject classObject : compileResult.getClassObjects()) {
                    try {
                        Class clazz = dynamicClassLoader.loadClass(classObject.getName(), classObject);
                        this.classCache.put(clazz.getName(), dynamicClassLoader);
                    } catch (Exception e) {
                        compileResult.addLoadClassMessage("load class " + classObject.getName() + " error, " + e.getMessage());
                        compileResult.setSuccess(false);
                    }
                }
            }
            if (compileResult.isSuccess()) {
                this.putCache(name, code);
            }
            return compileResult;
        }
    }

    public Class getClass(String name) {
        try {
            if (this.classCache.get(name) == null) {
                return null;
            }
            return this.classCache.get(name).loadClass(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private boolean existCache(String name, String code) {
        if (this.codeCache.get(name) != null) {
            String cacheMd5 = Md5Crypt.md5Crypt(this.codeCache.get(name).getBytes());
            String md5 = Md5Crypt.md5Crypt(code.getBytes());
            if (cacheMd5.equals(md5)) {
                return true;
            }
        }
        return false;
    }

    private void putCache(String name, String code) {
        this.codeCache.put(name, code);
    }

    private static List<String> getOptions() {
        List<String> options = new ArrayList<>();
        options.add("-encoding");
        options.add("UTF-8");
        options.add("-classpath");
        options.add(getClassPath());
        return options;
    }

    private static String getClassPath() {
        StringBuilder sb = new StringBuilder();
        for (URL url : ((URLClassLoader) JavaCodeCompileManager.class.getClassLoader()).getURLs()) {
            String p = url.getFile();
            sb.append(p).append(File.pathSeparator);
        }
        return sb.toString();
    }

}
