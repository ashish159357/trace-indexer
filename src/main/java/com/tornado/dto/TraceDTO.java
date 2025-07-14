package com.tornado.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for trace information.
 * Represents the structure of a trace document in the Lucene index.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceDTO {
    private String traceId;
    private String spanId;
    private String serviceName;
    private String spanName;
    private long startTime;
    private long endTime;
    private String attributes;
    private String resourceAttributes;
}