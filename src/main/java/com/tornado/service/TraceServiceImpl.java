package com.tornado.service;

import com.tornado.index.IndexDataService;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import java.util.Map;
import java.util.HashMap;

public class TraceServiceImpl extends TraceServiceGrpc.TraceServiceImplBase {

    @Override
    public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver) {

        processTrace(request);

        // Respond with an empty response
        ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    public void processTrace(ExportTraceServiceRequest request) {
        for (ResourceSpans resourceSpan : request.getResourceSpansList()) {

            // Extract resource-level fields
            Map<String, Object> resourceFields = TraceFieldExtractor.extractResourceFields(resourceSpan);

            for (var scopeSpan : resourceSpan.getScopeSpansList()) {
                for (var span : scopeSpan.getSpansList()) {
                    // Extract span-level fields
                    Map<String, Object> spanFields = TraceFieldExtractor.extractSpanFields(span);

                    // Combine resource and span fields
                    Map<String, Object> allFields = new HashMap<>(resourceFields);
                    allFields.putAll(spanFields);

                    // Index the combined fields
                    IndexDataService.indexTraceData(allFields);
                }
            }
        }
    }
}
