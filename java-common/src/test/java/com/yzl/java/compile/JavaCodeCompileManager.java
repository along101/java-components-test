package com.yzl.java.compile;

import org.apache.commons.codec.digest.Md5Crypt;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
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

    private Map<String, Class> classCache = new ConcurrentHashMap<>();

    public CompileResult compile(String name, String code) {
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

    public CompileResult compileAndloadClass(String name, String code) {
        if (existCatch(code)) {
            return CompileResult.create(true);
        }
        synchronized (this) {
            //double check
            if (existCatch(code)) {
                return CompileResult.create(true);
            }
            CompileResult compileResult = compile(name, code);
            if (compileResult.isSuccess()) {
                DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(this.getClass().getClassLoader());
                for (JavaClassObject classObject : compileResult.getClassObjects()) {
                    try {
                        Class clazz = dynamicClassLoader.loadClass(classObject.getName(), classObject);
                        this.classCache.put(clazz.getName(), clazz);
                    } catch (Exception e) {
                        compileResult.addLoadClassMessage("load class " + classObject.getName() + " error, " + e.getMessage());
                        compileResult.setSuccess(false);
                    }
                }
            }
            if (compileResult.isSuccess()) {
                this.putCatch(code);
            }
            return compileResult;
        }
    }

    public Class getClass(String name) {
        return this.classCache.get(name);
    }

    private boolean existCatch(String code) {
        String md5 = Md5Crypt.md5Crypt(code.getBytes());
        return this.codeCache.get(md5) != null;
    }

    private void putCatch(String code) {
        String md5 = Md5Crypt.md5Crypt(code.getBytes());
        this.codeCache.put(md5, code);
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

}
