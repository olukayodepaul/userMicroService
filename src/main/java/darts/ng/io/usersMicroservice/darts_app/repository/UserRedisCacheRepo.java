package darts.ng.io.usersMicroservice.darts_app.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.FetchUserDetailsCacheModel;
import darts.ng.io.usersMicroservice.utilities.CustomRuntimeException;
import darts.ng.io.usersMicroservice.utilities.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserRedisCacheRepo {

    private static final String REDIS_KEY_PATTERN = "registration:user";
    private static final int USER_NOT_FOUND_IN_REDIS = 1;
    private static final int USER_FOUND_IN_REDIS = 0;
    private static final int USER_NOT_AVAILABLE = 2;
    private static final int ERROR_FETCHING_USER = 3;
    private static final boolean SAVE_UPDATE_SUCCESS = true;
    private static final boolean SAVE_UPDATE_FAILED = false;

    private static final Logger logger = LoggerFactory.getLogger(UserRedisCacheRepo.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public UserRedisCacheRepo(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches the user details from Redis cache based on the provided email.
     *
     * @param email the email address of the user to fetch details for
     * @return a {@link FetchUserDetailsCacheModel} object containing the result of the fetch operation
     */
    public FetchUserDetailsCacheModel fetchUserDetails(String email) {
        try {

            Object cachedObject = redisTemplate.opsForHash().get(REDIS_KEY_PATTERN, email);

            if (cachedObject == null) {
                return new FetchUserDetailsCacheModel(false, 1, "No user found in redis", null);
            }

            // Convert the cached object to CacheModel
            UserCacheModel cacheModel = objectMapper.convertValue(cachedObject, UserCacheModel.class);
            return new FetchUserDetailsCacheModel(true, 0, "", cacheModel);

        } catch (Exception e) {
            logger.error("RedisCacheService::fetchUserDetails - Error occurred while trying to fetch user details ID {}: {}", email, e.getMessage());
            return new FetchUserDetailsCacheModel(false, 3, e.getMessage(), new UserCacheModel());
        }
    }

    /**
     * Saves or updates user details in Redis cache.
     *
     * @param user a {@link UserCacheModel} object representing the user details to be saved or updated
     * @return {@code true} if the operation was successful, {@code false} otherwise
     */
    public Boolean saveUpdateUserDetails(UserCacheModel user) {
        try {

            // Sub-key for identifying the user by their email
            String subKey = user.getEmail();

            // Save or update user details in Redis hash
            redisTemplate.opsForHash().put(REDIS_KEY_PATTERN, subKey, user);

            // Return success
            return SAVE_UPDATE_SUCCESS;
        } catch (Exception e) {
            // Log the error and return failure response
            logger.error("RedisCacheService::saveUpdateUserDetails - Error occurred while saving/updating user with email {}: {}", user.getEmail(), e.getMessage());
            return SAVE_UPDATE_FAILED;
        }
    }

    /**
     * Deletes user details from Redis cache based on the provided email.
     *
     * @param email the email address of the user to delete
     * @return a {@link FetchUserDetailsCacheModel} object indicating the result of the delete operation
     */
    public FetchUserDetailsCacheModel deleteUserDetails(String email) {
        try {
            // Redis sub-key identifier based on email
            String subKey = email;

            // Search for keys in Redis with the pattern "registration:user"
            Set<String> keys = redisTemplate.keys(REDIS_KEY_PATTERN);

            // Check if no keys are found in Redis
            if (keys == null || keys.isEmpty()) {
                return new FetchUserDetailsCacheModel(false, USER_NOT_FOUND_IN_REDIS, "No user found in Redis", new UserCacheModel());
            }

            // Delete the user details from Redis hash using the subKey
            Long result = redisTemplate.opsForHash().delete(REDIS_KEY_PATTERN, subKey);

            // Check if the deletion was successful
            if (result > 0) {
                return new FetchUserDetailsCacheModel(true, USER_FOUND_IN_REDIS, "", new UserCacheModel());
            }

            // Return response indicating user not available in Redis
            return new FetchUserDetailsCacheModel(false, USER_NOT_AVAILABLE, "", new UserCacheModel());

        } catch (Exception e) {
            // Log the error and return a failure response
            logger.error("RedisCacheService::deleteUserDetails - Error occurred while trying to delete user details for email {}: {}", email, e.getMessage());
            return new FetchUserDetailsCacheModel(true, USER_NOT_AVAILABLE, e.getMessage(), new UserCacheModel());
        }
    }

    //this is being update by the kafka service
    public void saveJWTBlackListedToken(String uuid, String token) {
            String subKey = "jwt_blacklist_service/"+uuid;
            redisTemplate.opsForList().leftPush(subKey, token);
    }

    public void isTokenBlacklisted(String uuid, String token) {
        String subKey = "jwt_blacklist_service/" + uuid;
        List<Object> tokens = redisTemplate.opsForList().range(subKey, 0, -1);

        // Convert tokens to string list
        List<String> tokenList = tokens.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        System.out.println(tokenList);
        System.out.println(tokens);

        if(tokenList.contains(token)){
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "access token", "Access token blacklisted"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }


}
