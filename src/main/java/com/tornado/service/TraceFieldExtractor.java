package com.tornado.service;

import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import lombok.Getter;

import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines which fields should be extracted from trace data and how they should be stored
 */
public class TraceFieldExtractor {
    
    // Define field extraction configuration
    public static class FieldConfig<T> {
        private final Function<T, Object> extractor;
        private final boolean store;
        @Getter
        private final String fieldName;
        @Getter
        private final String fieldType;
        
        private FieldConfig(String fieldName, Function<T, Object> extractor, boolean store, String fieldType) {
            this.fieldName = fieldName;
            this.extractor = extractor;
            this.store = store;
            this.fieldType = fieldType;
        }

        public Object extract(T source) {
            return extractor.apply(source);
        }
        
        public boolean shouldStore() {
            return store;
        }
    }

    /**
     * -- GETTER --
     *  Get all span field configurations
     */
    // Span field extractors
    @Getter
    private static final Map<String, FieldConfig<Span>> spanFields = new HashMap<>();

    /**
     * -- GETTER --
     *  Get all resource field configurations
     */
    // Resource field extractors
    @Getter
    private static final Map<String, FieldConfig<ResourceSpans>> resourceFields = new HashMap<>();
    
    static {
        // Configure span fields
        spanFields.put("traceId", new FieldConfig<>("traceId",
                span -> span.getTraceId().toStringUtf8(), true, "StringField"));
        spanFields.put("spanId", new FieldConfig<>("spanId",
                span -> span.getSpanId().toStringUtf8(), true, "StringField"));
        spanFields.put("name", new FieldConfig<>("name",
                Span::getName, true, "TextField"));
        spanFields.put("startTime", new FieldConfig<>("startTime", 
                Span::getStartTimeUnixNano, true, "LongPoint"));
        spanFields.put("endTime", new FieldConfig<>("endTime", 
                Span::getEndTimeUnixNano, true, "LongPoint"));
        spanFields.put("spanAttributes", new FieldConfig<>("spanAttributes",
                span -> span.getAttributesList().toString(), true, "StoredField"));
        
        // Configure resource fields
        resourceFields.put("resourceAttributes", new FieldConfig<>("resourceAttributes",
                rs -> rs.getResource().getAttributesList().toString(), true, "TextField"));
    }
    
    /**
     * Extract all configured fields from a span
     */
    public static Map<String, Object> extractSpanFields(Span span) {
        Map<String, Object> fields = new HashMap<>();
        for (FieldConfig<Span> config : spanFields.values()) {
            if (config.shouldStore()) {
                fields.put(config.getFieldName(), config.extract(span));
            }
        }
        return fields;
    }
    
    /**
     * Extract all configured fields from resource spans
     */
    public static Map<String, Object> extractResourceFields(ResourceSpans resourceSpans) {
        Map<String, Object> fields = new HashMap<>();
        for (FieldConfig<ResourceSpans> config : resourceFields.values()) {
            if (config.shouldStore()) {
                fields.put(config.getFieldName(), config.extract(resourceSpans));
            }
        }
        return fields;
    }

}