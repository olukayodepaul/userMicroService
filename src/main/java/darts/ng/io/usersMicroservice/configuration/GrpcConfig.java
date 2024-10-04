package darts.ng.io.usersMicroservice.configuration;

import darts.ng.io.usersMicroservice.darts_app.grpc.DetailServiceImp;
import darts.ng.io.usersMicroservice.security.JwtAuthInterceptor;
import io.grpc.*;
import io.grpc.netty.NettyServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcConfig {

    private final DetailServiceImp detailsServiceImp;
    private final JwtAuthInterceptor jwtServerInterceptor;

    public GrpcConfig(DetailServiceImp detailsServiceImp, JwtAuthInterceptor jwtServerInterceptor) {
        this.detailsServiceImp = detailsServiceImp;
        this.jwtServerInterceptor = jwtServerInterceptor;
    }

    @Bean
    public Server grpcServer() throws IOException {
        return ServerBuilder.forPort(9095)
                .intercept(jwtServerInterceptor)
                .addService(detailsServiceImp)
                .build()
                .start();
    }


    // gRPC Client setup to communicate with other gRPC servers
    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder
                .forAddress("localhost", 9095)
                .usePlaintext()  // For testing, no TLS. Use secure communication in production
                .keepAliveTime(30, TimeUnit.SECONDS)
                .enableFullStreamDecompression()
                .build();
    }
}




