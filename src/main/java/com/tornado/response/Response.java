package com.tornado.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a generic API response containing various information like response time, status, message, method, data, etc.
 */
@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    /**
     * The date and time when the response was generated.
     */
    private LocalDateTime responseTime;

    /**
     * The HTTP status of the response.
     */
    private HttpStatus status;

    /**
     * The numerical HTTP status code of the response.
     */
    private int statusCode;

    /**
     * A message providing additional information about the response.
     */
    private String message;

    /**
     * The HTTP method used to make the request that generated this response.
     */
    private String method;

    /**
     * A message providing further details about the execution of the request.
     */
    private String executionMessage;

    /**
     * A map containing additional data that can be included in the response.
     * The keys and values in the map can have any data type.
     */
    private Map<?, ?> data;
}

