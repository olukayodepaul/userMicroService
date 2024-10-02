package darts.ng.io.usersMicroservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public JedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName("localhost");
        configuration.setPassword("@admin123");
        configuration.setPort(6379);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);

        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .usePooling().poolConfig(poolConfig).build();

        return new JedisConnectionFactory(configuration, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> template() {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());

        // Use StringRedisSerializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Create ObjectMapper and register JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional

        // Use GenericJackson2JsonRedisSerializer for values with custom ObjectMapper
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;

    }
}