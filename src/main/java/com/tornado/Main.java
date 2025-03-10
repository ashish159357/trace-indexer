package com.tornado;

import com.tornado.service.TraceServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Start the gRPC Server
        Server server = ServerBuilder.forPort(4317)  // Default OTLP gRPC Port
                .addService(new TraceServiceImpl())
                .build()
                .start();

        System.out.println("OpenTelemetry gRPC Server is running on port 4317...");

        // Keep server alive
        server.awaitTermination();
    }
}