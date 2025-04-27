package com.tornado;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sun.net.httpserver.HttpServer;
import com.tornado.service.TraceServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
//        var type = "GRPC";

//        if (type.equals("HTTP"))
//        {
//            createHttpServer();;
//        }

        SpringApplication.run(Main.class, args);
    }

//    @Bean
//    public GRpcServerRunner grpcServerRunner() {
//        return new GRpcServerRunner(4317); // gRPC server port
//    }

    // GRpcServerRunner.java
    public static class GRpcServerRunner implements CommandLineRunner {
        private final int port;
        private Server server;

        public GRpcServerRunner(int port) {
            this.port = port;
        }

        @Override
        public void run(String... args) throws Exception {
            server = ServerBuilder.forPort(port)
                    .addService(new TraceServiceImpl())
                    .build()
                    .start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (server != null) {
                    server.shutdown();
                }
            }));
        }
    }

    public static void createHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(4318), 0);

        server.createContext("/v1/traces", exchange -> {
            try {
                // 1. Verify POST method
                if (!"POST".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                    return;
                }

                // 2. Parse Protobuf
                InputStream is = exchange.getRequestBody();
                ExportTraceServiceRequest request = ExportTraceServiceRequest.parseFrom(is);

                // 3. Process traces (implement your logic here)
                System.out.println("Received " + request.getResourceSpansCount() + " trace(s)");

                // 4. Send success response
                ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
                exchange.getResponseHeaders().set("Content-Type", "application/x-protobuf");
                exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                response.writeTo(os);
                os.close();

            } catch (InvalidProtocolBufferException e) {
                exchange.sendResponseHeaders(400, -1); // Bad Request
            } finally {
                exchange.close();
            }
        });

        server.start();
        System.out.println("OTLP/HTTP Server running on port 4318");
    }
}