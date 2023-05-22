package com.yzl.test;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

/**
 * @author yutu
 * @date 2023/4/23
 */
public class OpenTelemetrySupport {

    private static Tracer tracer;
    private static OpenTelemetry openTelemetry;
    private static SdkTracerProvider sdkTracerProvider;
    private static SdkMeterProvider sdkMeterProvider;
    private static SdkLoggerProvider sdkLoggerProvider;

    public static BatchSpanProcessor batchSpanProcessor = null;

    static {
        // 获取OpenTelemetry Tracer
        Resource resource = Resource.getDefault()
                .merge(Resource.create(Attributes.of(
                        ResourceAttributes.SERVICE_NAME, "trace-test",
                        ResourceAttributes.HOST_NAME, "myapp1"
                )));
        batchSpanProcessor = BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder()
//                .setEndpoint("xxxx")
//                .addHeader("Authentication", "xxxx")
                .build()).build();

        sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(batchSpanProcessor)
                .setResource(resource)
                .build();

        sdkMeterProvider = SdkMeterProvider.builder()
                .registerMetricReader(PeriodicMetricReader.builder(new SystemOutMetricExporter()).build())
                .setResource(resource)
                .build();

        sdkLoggerProvider = SdkLoggerProvider.builder()
                .addLogRecordProcessor(
                        SimpleLogRecordProcessor.create(new SystemOutLogRecordExporter())
                ).build();

        openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setMeterProvider(sdkMeterProvider)
                .setLoggerProvider(sdkLoggerProvider)
                .buildAndRegisterGlobal();

        tracer = openTelemetry.getTracer("my_trace123", "1.0.0");

    }

    public static OpenTelemetry getOpenTelemetry() {
        return openTelemetry;
    }

    public static void shutdown() {
        sdkTracerProvider.shutdown();
        sdkMeterProvider.shutdown();
        sdkLoggerProvider.shutdown();
    }

    public static Tracer getTracer() {
        return tracer;
    }
}
