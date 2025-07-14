package com.tornado;

import com.tornado.service.TraceServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public GRpcServerRunner grpcServerRunner() {
        return new GRpcServerRunner(4317); // gRPC server port
    }

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
}