package com.yzl.java.compile;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author yzl
 * @date 2019-04-24
 */
public abstract class DynamicObjectGenerator<T> {

    protected JavaCodeCompileManager javaCodeCompileManager;

    public DynamicObjectGenerator(JavaCodeCompileManager javaCodeCompileManager) {
        this.javaCodeCompileManager = javaCodeCompileManager;
    }

    @SuppressWarnings("unchecked")
    public T generate(String name, Map<String, String> methodCodes) {
        String className = getClassName(name);
        String code = generateJavaCode(name, methodCodes);
        CompileResult compileResult = javaCodeCompileManager.compileAndLoadClass(className, code);
        if (!compileResult.isSuccess()) {
            throw new RuntimeException(compileResult.getMessage());
        }
        Class clazz = javaCodeCompileManager.getClass(className);
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("generate " + name + " error.", e);
        }
    }

    public String generateJavaCode(String name, Map<String, String> methodCodes) {
        String code = "package " + getPackage() + ";\n" +
                String.join("\n", getImports()) + "\n" +
                "\n" +
                "public class " + getSimpleName(name) + " implements " + getInterface().getName() + " {\n";
        StringBuilder methodCode = new StringBuilder();
        methodCodes.forEach((key, value) -> {
            methodCode.append("\n")
                    .append("    ").append(getMethodDefine(key)).append(" {\n")
                    .append(getBodyCode(value)).append("\n")
                    .append("    }\n");
        });
        code += methodCode.toString() +
                "}\n";
        return code;
    }

    protected abstract String getNamePrefix();

    protected abstract String getMethodDefine(String name);

    protected abstract Class getInterface();

    protected abstract String getDefaultBodyCode();

    protected String getSimpleName(String name) {
        return getNamePrefix() + name.replaceAll("[^0-9A-Za-z]", "_");
    }

    protected String getPackage() {
        return getInterface().getPackage().getName();
    }

    protected String getClassName(String name) {
        return getPackage() + "." + getSimpleName(name);
    }

    protected List<String> getImports() {
        return Arrays.asList(
                "import java.util.*;"
        );
    }

    protected String getBodyCode(String code) {
        if (code == null || "".equalsIgnoreCase(code)) {
            code = getDefaultBodyCode();
        }
        return code;
    }

}
