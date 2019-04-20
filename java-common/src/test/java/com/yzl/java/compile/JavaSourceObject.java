package com.yzl.java.compile;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * java 源代码对象
 * @author yzl
 * @date 2019-04-19
 */
public class JavaSourceObject extends SimpleJavaFileObject {

    protected String code;

    public JavaSourceObject(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
                Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
