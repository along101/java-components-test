package com.yzl.java.compile;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * @author yinzuolong
 */
public class JavaSourceFromString extends SimpleJavaFileObject {
    final String code;

    public JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension),
                JavaFileObject.Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
