package darts.ng.io.usersMicroservice.configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PreDestroy;


import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcClientConfig {

    private ManagedChannel managedChannel;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder
                .forAddress("localhost", 9096)
                .usePlaintext() // Use useTransportSecurity() in production
                .keepAliveTime(30, TimeUnit.SECONDS)
                .enableFullStreamDecompression()
                .build();
    }

    //production
//    @Bean
//    public ManagedChannel managedChannel() {
//        managedChannel = ManagedChannelBuilder
//                .forAddress("your.production.server", 9095) // Use your production server address and port
//                .useTransportSecurity(new File("src/main/resources/certs/server.crt"), // Path to your certificate
//                        new File("src/main/resources/certs/server.key")) // Path to your key
//                .keepAliveTime(30, TimeUnit.SECONDS)
//                .enableFullStreamDecompression()
//                .build();
//        return managedChannel;
//    }

    // Use setter injection
    public void setManagedChannel(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

    @PreDestroy
    public void shutdown() {
        if (managedChannel != null) {
            managedChannel.shutdownNow();
        }
    }
}



