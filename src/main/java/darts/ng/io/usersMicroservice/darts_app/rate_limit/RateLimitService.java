package darts.ng.io.usersMicroservice.darts_app.rate_limit;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RateLimitService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public boolean isRateLimited(String email) {
       try {
           String key = "rate_limit_reset:" + email;

           Boolean keyExists = redisTemplate.hasKey(key);

           if (keyExists == null || !keyExists) {
               // Set the initial value as a proper integer (1L) instead of 1
               redisTemplate.opsForValue().set(key, 1L);
               redisTemplate.expire(key, 2, TimeUnit.MINUTES);  // Set expiry time for the rate limit
               return false;  // Allow the request since it's the first one
           }

           Long requests = redisTemplate.opsForValue().increment(key);  // Increment the request count

           // Check if the request count has exceeded the limit (e.g., 3 requests)
           if (requests != null && requests > 3) {
               return true;  // Rate limit has been exceeded
           }

           return false;  // Allow the request if it's within the limit

       } catch (Exception e) {
           logger.error("RateLimitService::isRateLimited Failed to connect to Redis server. Error: {}",  e.getMessage());
           return false;
       }
    }

    // Method to manually reset the rate limit for a user (if needed)
    public void resetRateLimit(String email) {
        String key = "rate_limit_reset:" + email;
        redisTemplate.delete(key);
    }

}
