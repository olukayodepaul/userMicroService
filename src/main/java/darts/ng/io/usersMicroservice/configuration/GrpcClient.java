package darts.ng.io.usersMicroservice.configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcClient {

    //Client for grpc and grpc communication
    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder
                .forAddress("localhost", 9095)
                .usePlaintext()
                .keepAliveTime(30, TimeUnit.SECONDS)
                .enableFullStreamDecompression()
                .build();
    }
}
