package com.yzl.java.compile;


import java.util.ArrayList;
import java.util.List;

/**
 * @author yzl
 * @date 2019-04-22
 */
public class PreHandlerGenerator extends DynamicObjectGenerator<PreHandler> {

    public PreHandlerGenerator(JavaCodeCompileManager javaCodeCompileManager) {
        super(javaCodeCompileManager);
    }

    @Override
    protected String getNamePrefix() {
        return PreHandler.class.getSimpleName() + "_";
    }

    @Override
    protected String getMethodDefine(String name) {
        return "public boolean " + name + "(Object object)";
    }

    @Override
    protected Class getInterface() {
        return PreHandler.class;
    }

    protected List<String> getImports() {
        return new ArrayList<>(super.getImports());
    }

    protected String getDefaultBodyCode() {
        return "return true;";
    }
}
