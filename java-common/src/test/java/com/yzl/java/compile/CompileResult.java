package com.yzl.java.compile;

import lombok.Data;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yzl
 * @date 2019-04-20
 */
@Data
public class CompileResult {

    private boolean success;
    /**
     * 编译得到的class对象
     */
    private List<JavaClassObject> classObjects;

    /**
     * 编译信息
     */
    private DiagnosticCollector<JavaFileObject> diagnostics;

    private List<String> loadClassMessages = new ArrayList<>();

    public static CompileResult create(boolean success) {
        CompileResult result = new CompileResult();
        result.setSuccess(success);
        return result;
    }

    public void addLoadClassMessage(String message) {
        this.loadClassMessages.add(message);
    }

    public String getMessage() {
        StringBuilder resultMessage = new StringBuilder();
        if (success) {
            resultMessage.append("success.\n");
        }
        if (diagnostics == null || diagnostics.getDiagnostics().size() == 0) {
            resultMessage.append("compile success.\n");
        } else {
            String compileMessage =
                    diagnostics.getDiagnostics().stream().map(diagnostic -> {
                        String message = diagnostic.getSource().getName() + "\n";
                        message += diagnostic.getKind() + ", line: " + diagnostic.getLineNumber() + ", col" + diagnostic.getColumnNumber() + "\n";
                        message += diagnostic.getCode() + ", " + diagnostic.getMessage(null);
                        return message;
                    }).collect(Collectors.joining("\n\n"));
            resultMessage.append(compileMessage);
        }
        if (loadClassMessages.size() != 0) {
            resultMessage.append(String.join("\n", loadClassMessages));
        }
        return resultMessage.toString();
    }
}
