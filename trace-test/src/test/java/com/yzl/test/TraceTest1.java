package com.yzl.test;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * https://help.aliyun.com/document_detail/410559.html
 * https://opentelemetry.io/docs/instrumentation/java/manual/
 *
 * @author yutu
 * @date 2023/4/23
 */
public class TraceTest1 {

    @Test
    public void test() throws Exception {
        Tracer tracer = OpenTelemetrySupport.getTracer();

        for (int i = 0; i < 10; i++) {
            Span span = tracer.spanBuilder("test")
                    .setParent(Context.current().with(Span.current()))
                    .startSpan();
            System.out.println(span.getSpanContext().getTraceId());
            Scope scope = span.makeCurrent();
            span.setAttribute("biz-id", "111");

            child();

            scope.close();
            span.end();
            Thread.sleep(1000);
        }
        Thread.sleep(10000);
        OpenTelemetrySupport.shutdown();
        Thread.sleep(5000);
    }


    public void child() throws Exception {
        Span childSpan = OpenTelemetrySupport.getTracer().spanBuilder("child")
                .startSpan();
        try (Scope scope = childSpan.makeCurrent()) {
            Thread.sleep(100);
        } finally {
            childSpan.end();
        }
    }

    @Test
    public void name() throws Exception {
        TextMapGetter<Map<String, String>> getter =
                new TextMapGetter<Map<String, String>>() {
                    @Override
                    public String get(Map<String, String> headers, String s) {
                        assert headers != null;
                        return headers.get(s);
                    }

                    @Override
                    public Iterable<String> keys(Map<String, String> headers) {
                        return headers.keySet();
                    }
                };

        TextMapSetter<HttpURLConnection> setter =
                new TextMapSetter<HttpURLConnection>() {
                    @Override
                    public void set(HttpURLConnection carrier, String key, String value) {
                        // Insert the context as Header
                        carrier.setRequestProperty(key, value);
                    }
                };
        B3Propagator b3Propagator = B3Propagator.injectingMultiHeaders();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-B3-TraceId", "39a6b7e9a3045aab");
        headers.put("X-B3-SpanId", "f01aca3b9c48c758");
        //这里是将请求中的traceId设置到当前的context
        Context context1 = b3Propagator.extract(Context.current(), headers, getter);

        Context extractedContext = OpenTelemetrySupport.getOpenTelemetry().getPropagators().getTextMapPropagator()
                .extract(Context.current(), headers, getter);
        try (Scope scope = extractedContext.makeCurrent()) {
            // Automatically use the extracted SpanContext as parent.
            Span serverSpan = OpenTelemetrySupport.getTracer().spanBuilder("GET /resource")
                    .setSpanKind(SpanKind.SERVER)
                    .startSpan();

            try (Scope ignored = serverSpan.makeCurrent()) {
                // Add the attributes defined in the Semantic Conventions
                serverSpan.setAttribute(SemanticAttributes.HTTP_METHOD, "GET");
                serverSpan.setAttribute(SemanticAttributes.HTTP_SCHEME, "http");
                serverSpan.setAttribute(SemanticAttributes.HTTP_HOST, "localhost:8080");
                serverSpan.setAttribute(SemanticAttributes.HTTP_TARGET, "/resource");

                URL url = new URL("http://127.0.0.1:7001/test/getHeaders");
                HttpURLConnection transportLayer = (HttpURLConnection) url.openConnection();
                // Inject the request with the *current*  Context, which contains our current Span.
//                OpenTelemetrySupport.getOpenTelemetry().getPropagators().getTextMapPropagator()
//                        .inject(Context.current(), transportLayer, setter);
                b3Propagator.inject(Context.current(), transportLayer, setter);
                // Make outgoing call
            } finally {
                serverSpan.end();
            }
        }
    }
}
