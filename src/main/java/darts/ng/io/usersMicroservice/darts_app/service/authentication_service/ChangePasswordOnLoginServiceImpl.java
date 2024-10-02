package darts.ng.io.usersMicroservice.darts_app.service.authentication_service;


import darts.ng.io.usersMicroservice.darts_app.entity.ChangePasswordOnLoginReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.ChangePasswordOnLoginResModel;
import darts.ng.io.usersMicroservice.darts_app.entity.FetchUserDetailsCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.kafka.MessageBrokerManager;
import darts.ng.io.usersMicroservice.darts_app.rate_limit.RateLimitService;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserRedisCacheRepo;
import darts.ng.io.usersMicroservice.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class ChangePasswordOnLoginServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(RequestResetPasswordServiceImpl.class);
    private final UserDatabaseRepo databaseRep;
    private final ValidationUtils validationUtils;
    private final DatabaseSaveUpdatedService databaseSaveUpdatedService;
    private final BCryptPasswordEncoder encoder;
    private final UserRedisCacheRepo cacheService;
    private final MessageBrokerManager messageBrokerManager;


    public ChangePasswordOnLoginServiceImpl(
            UserDatabaseRepo databaseRep,
            ValidationUtils validationUtils,
            DatabaseSaveUpdatedService databaseSaveUpdatedService,
            UserRedisCacheRepo cacheService,
            MessageBrokerManager messageBrokerManager
    ) {
        this.databaseRep = databaseRep;
        this.validationUtils = validationUtils;
        this.databaseSaveUpdatedService = databaseSaveUpdatedService;
        this.encoder = new BCryptPasswordEncoder(12);
        this.cacheService = cacheService;
        this.messageBrokerManager = messageBrokerManager;
    }

    public ResponseEntity<ChangePasswordOnLoginResModel> changePasswordOnLogin(ChangePasswordOnLoginReqModel request) {

        validationUtils.validateMatchPattern(
                request.getNew_password(),
                request.getConfirm_password(),
                "New password and confirm password dot not match"
        );

        validationUtils.validatePasswordStrength(request.getNew_password());

        Optional<UsersDatabaseModel> userByEmail =  databaseRep.findById(request.getUser_id());

        if(userByEmail.isPresent()) {

            //check if email if confirm or blacklisted
            UsersDatabaseModel existingUser = userByEmail.get();
            validationUtils.validatePassword(request.getConfirm_password(), existingUser.getPassword());

            // Build updated user details with password reset info
            UsersDatabaseModel resetPassword = UsersDatabaseModel.builder()
                    .id(existingUser.getId())
                    .user_id(existingUser.getUser_id())
                    .email(existingUser.getEmail())
                    .password(encoder.encode(request.getNew_password()))
                    .role(existingUser.getRole())
                    .organisation_id(existingUser.getOrganisation_id())
                    .password_reset_code(existingUser.getPassword_reset_code())
                    .password_reset_expiration(existingUser.getPassword_reset_expiration())
                    .confirmation_link(existingUser.getConfirmation_link())
                    .confirmation_code(existingUser.getConfirmation_code())
                    .confirmation_token_expiration(existingUser.getConfirmation_token_expiration())
                    .is_active(existingUser.getIs_active())
                    .is_blacklisted(existingUser.getIs_blacklisted())
                    .blacklist_expire_at(existingUser.getBlacklist_expire_at())
                    .created_at(existingUser.getCreated_at())
                    .updated_at(existingUser.getUpdated_at())
                    .build();

            UsersDatabaseModel savedUser = databaseSaveUpdatedService.saveUpdatedUserDetails(resetPassword);

            if (savedUser.getId().equals(existingUser.getId())) {

                FetchUserDetailsCacheModel deleteCachedUser = cacheService.deleteUserDetails(savedUser.getEmail());

                if (deleteCachedUser.getStatus()) {
                    messageBrokerManager.deleteUserDetailsFromCacheMQ("delete", savedUser.getEmail());
                }

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }


            throw new CustomRuntimeException(
                    new ErrorHandler(false, "User not found in the blacklist", "The user you are trying to whitelist is not blacklisted"),
                    HttpStatus.NOT_FOUND
            );

        }

        throw new CustomRuntimeException(
                new ErrorHandler(false, "User not found in the blacklist", "The user you are trying to whitelist is not blacklisted"),
                HttpStatus.NOT_FOUND
        );

    }
}
