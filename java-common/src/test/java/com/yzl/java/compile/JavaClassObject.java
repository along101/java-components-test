package com.yzl.java.compile;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * java class 对象
 *
 * @author yzl
 * @date 2019-04-20
 */
public class JavaClassObject extends SimpleJavaFileObject {

    protected String name;
    protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    public JavaClassObject(String name) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public byte[] getBytes() {
        return bos.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return bos;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        bos.close();
    }
}