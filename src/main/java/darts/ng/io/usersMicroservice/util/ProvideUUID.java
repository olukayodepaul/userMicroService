package darts.ng.io.usersMicroservice.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ProvideUUID {

    @Bean
    @Scope
    public UUIDGenerator uuidGenerator(){
        return new UUIDGenerator();
    }

}
