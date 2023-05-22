package com.yzl.test;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;

import java.util.Collection;

/**
 * @author yutu
 * @date 2023/4/24
 */
public class SystemOutLogRecordExporter implements LogRecordExporter {

    @Override
    public CompletableResultCode export(Collection<LogRecordData> logs) {
        for (LogRecordData logRecordData : logs) {
            System.out.println(logRecordData);
        }
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        return CompletableResultCode.ofSuccess();
    }
}
