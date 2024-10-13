package darts.ng.io.usersMicroservice.darts_app.service.authentication_service;


import darts.ng.io.usersMicroservice.darts_app.entity.*;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.kafka.MessageBrokerManager;
import darts.ng.io.usersMicroservice.darts_app.repository.UserRedisCacheRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.security.FilterService;
import darts.ng.io.usersMicroservice.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserLoginServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginServiceImpl.class);
    private final UserDatabaseRepo userRepo;
    private final UserRedisCacheRepo cacheService;
    private final MessageBrokerManager messageBrokerManager;
    private final UtilitiesManager utilitiesManager;
    private final FilterService jwtService;
    private final ValidationUtils validationUtils;


    public UserLoginServiceImpl(
            UserDatabaseRepo userRepo,
            UserRedisCacheRepo cacheService,
            MessageBrokerManager messageBrokerManager,
            UtilitiesManager utilitiesManager,
            FilterService jwtService,
            ValidationUtils validationUtils
    ) {
        this.userRepo = userRepo;
        this.cacheService = cacheService;
        this.messageBrokerManager = messageBrokerManager;
        this.utilitiesManager = utilitiesManager;
        this.jwtService = jwtService;
        this.validationUtils = validationUtils;
    }

    public ResponseEntity<UserLoginResModel> userAuthentication(UserLoginReqModel request) {

        validationUtils.sanitizeEmail(request.getEmail());
        validationUtils.bruteForceProtection(request.getEmail(), AppConfig.LOGIN_RESET_LIMIT);
        validationUtils.validatePasswordStrength(request.getPassword());

        // Check cache first
        FetchUserDetailsCacheModel existingUser = cacheService.fetchUserDetails(request.getEmail());
        if (existingUser.getStatus()) {

            UserCacheModel existingUserInCache = existingUser.getUserDetails();

            validationUtils.validateAccountNotConfirm(existingUserInCache.getIs_active());
            validationUtils.validateAccountBlackListed(existingUserInCache.getIs_blacklisted());
            validationUtils.blackListExpiration(utilitiesManager.convertStringToDateTime(existingUserInCache.getBlacklist_expire_at()));
            validationUtils.validatePassword(request.getPassword(),existingUserInCache.getPassword());

            System.out.println(
                    "cache "+existingUserInCache.getUuid().toString()+" "+existingUserInCache.getEmail()+" "+existingUserInCache.getOrganisation_id().toString()
            );

            String requestToken = jwtService.jwtToken(existingUserInCache.getUuid().toString(), existingUserInCache.getEmail(), existingUserInCache.getOrganisation_id().toString());
            return ResponseEntity.status(HttpStatus.OK).body(buildResponse(
                    existingUserInCache.getEmail(),
                    existingUserInCache.getRole(),
                    existingUserInCache.getOrganisation_id(),
                    existingUserInCache.getIs_active(),
                    existingUserInCache.getIs_blacklisted(),
                    utilitiesManager.convertStringToDateTime(existingUserInCache.getUpdated_at()),
                    utilitiesManager.convertStringToDateTime(existingUserInCache.getCreated_at()),
                    requestToken
            ));


        }
        // If not in cache, fetch from database
        else {

            UsersDatabaseModel findUser = userRepo.findByEmail(request.getEmail())
                    .orElseThrow(() -> new CustomRuntimeException(
                            new ErrorHandler(false, AppConfig.KAY_ERROR, AppConfig.INVALID_EMAIL),
                            HttpStatus.NOT_FOUND
                    ));

            validationUtils.validateAccountNotConfirm(findUser.getIs_active());
            validationUtils.validateAccountBlackListed(findUser.getIs_blacklisted());
            validationUtils.blackListExpiration(findUser.getBlacklist_expire_at());
            validationUtils.validatePassword(request.getPassword(),findUser.getPassword());

            String requestToken = jwtService.jwtToken(findUser.getUuid().toString(), findUser.getEmail(), findUser.getOrganisation_id().toString());

            System.out.println(
                    "db "+findUser.getUuid().toString()+" "+findUser.getEmail()+" "+findUser.getOrganisation_id().toString()
            );

            // Save user data to cache
            Boolean saveIntoRedisCache = cacheService.saveUpdateUserDetails(cacheRecord(findUser));
            if (!saveIntoRedisCache) {
                messageBrokerManager.updateUserDetailsThroughMQ("update", findUser);
            }

            return ResponseEntity.status(HttpStatus.OK).body(buildResponse(
                    findUser.getEmail(),
                    findUser.getRole(),
                    findUser.getOrganisation_id(),
                    findUser.getIs_active(),
                    findUser.getIs_blacklisted(),
                    findUser.getUpdated_at(),
                    findUser.getCreated_at(),
                    requestToken
            ));
        }
    }


    private UserLoginResModel buildResponse(String email, String role, Integer organisationId, Boolean isActive, Boolean isBlackListed,
            LocalDateTime updatedAt, LocalDateTime createdAt, String token)
    {
        return new UserLoginResModel(
                true,
                "Login Successfully",
                new UserLoginResModel.Details(
                        email,
                        role,
                        organisationId,
                        isActive,
                        isBlackListed,
                        updatedAt,
                        createdAt,
                        token
                )
        );
    }

    private UserCacheModel cacheRecord(UsersDatabaseModel user) {
        return UserCacheModel.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .organisation_id(user.getOrganisation_id())
                .password_reset_code(user.getPassword_reset_code())
                .password_reset_expiration(utilitiesManager.convertDateTimeToString(user.getPassword_reset_expiration()))
                .confirmation_link(user.getConfirmation_link())
                .confirmation_code(user.getConfirmation_code())
                .confirmation_token_expiration(utilitiesManager.convertDateTimeToString(user.getConfirmation_token_expiration()))
                .is_active(user.getIs_active())
                .is_blacklisted(user.getIs_blacklisted())
                .blacklist_expire_at(utilitiesManager.convertDateTimeToString(user.getBlacklist_expire_at()))
                .created_at(utilitiesManager.convertDateTimeToString(user.getCreated_at()))
                .updated_at(utilitiesManager.convertDateTimeToString(user.getUpdated_at()))
                .build();
    }
}