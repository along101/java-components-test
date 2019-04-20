package com.yzl.java.compile;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author yzl
 * @date 2019-04-19
 */
public class DynamicClassLoader extends URLClassLoader {
    public DynamicClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public Class findClassByClassName(String className) throws ClassNotFoundException {
        return this.findClass(className);
    }

    public Class loadClass(String fullName, JavaClassObject javaClassObject) {
        byte[] classData = javaClassObject.getBytes();
        return this.defineClass(fullName, classData, 0, classData.length);
    }
}