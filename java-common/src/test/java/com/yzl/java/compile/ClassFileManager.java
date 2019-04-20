package com.yzl.java.compile;

import lombok.Getter;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yzl
 * @date 2019-04-20
 */
public class ClassFileManager extends ForwardingJavaFileManager {

    @Getter
    private List<JavaClassObject> javaClassObjects = new ArrayList<>();

    public ClassFileManager(StandardJavaFileManager standardManager) {
        super(standardManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling)
            throws IOException {
        JavaClassObject javaClassObject = new JavaClassObject(className);
        javaClassObjects.add(javaClassObject);
        return javaClassObject;
    }

}