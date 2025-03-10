package com.tornado.service;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.trace.v1.ResourceSpans;

import java.util.UUID;

public class TraceServiceImpl extends TraceServiceGrpc.TraceServiceImplBase {

    @Override
    public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver) {
        for (ResourceSpans resourceSpan : request.getResourceSpansList()) {
            String traceId = UUID.randomUUID().toString();
            for (var scopeSpan : resourceSpan.getScopeSpansList()) {
                for (var span : scopeSpan.getSpansList()) {
                    IndexDataService.indexTrace(
                            traceId,
                            span.getSpanId().toStringUtf8(),
                            resourceSpan.getResource().getAttributesList().toString(),
                            span.getName(),
                            span.getStartTimeUnixNano(),
                            span.getEndTimeUnixNano(),
                            "{}"
                    );
                }
            }
        }


        // Respond with an empty response
        ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
