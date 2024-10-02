package darts.ng.io.usersMicroservice.darts_app.service.authentication_service;

import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.ResetPasswordReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.ResetPasswordResModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.kafka.MessageBrokerManager;
import darts.ng.io.usersMicroservice.darts_app.repository.UserRedisCacheRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.security.JwtService;
import darts.ng.io.usersMicroservice.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ResetPasswordServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordServiceImpl.class);
    private final UserDatabaseRepo databaseRep;
    private final UtilitiesManager utilitiesManager;
    private final BCryptPasswordEncoder encoder;
    private final UserRedisCacheRepo cacheService;
    private final MessageBrokerManager messageBrokerManager;
    private final JwtService jwtService;
    private final ValidationUtils validationUtils;
    private final DatabaseSaveUpdatedService databaseSaveUpdatedService;

    /**
     * Constructor to initialize dependencies.
     *
     * @param databaseRep      the repository for accessing user data
     * @param emailValidator   utility for validating email addresses
     * @param utilitiesManager utility for general utility functions
     * @param cacheService     the cache repository for storing user details
     * @param messageBrokerManager the Kafka message broker for messaging
     * @param jwtService       the JWT service for managing tokens
     */
    public ResetPasswordServiceImpl(
            UserDatabaseRepo databaseRep,
            EmailValidator emailValidator,
            UtilitiesManager utilitiesManager,
            UserRedisCacheRepo cacheService,
            MessageBrokerManager messageBrokerManager,
            JwtService jwtService,
            ValidationUtils validationUtils,
            DatabaseSaveUpdatedService databaseSaveUpdatedService

    ) {
        this.databaseRep = databaseRep;
        this.utilitiesManager = utilitiesManager;
        this.encoder = new BCryptPasswordEncoder(12);
        this.cacheService = cacheService;
        this.messageBrokerManager = messageBrokerManager;
        this.jwtService = jwtService;
        this.validationUtils = validationUtils;
        this.databaseSaveUpdatedService = databaseSaveUpdatedService;
    }

    public ResponseEntity<ResetPasswordResModel> resetPassword(ResetPasswordReqModel request) {

        validationUtils.validateEmail(request.getEmail());
        validationUtils.validateRequest(request);
        validationUtils.validatePasswordStrength(request.getNew_password());

        Optional<UsersDatabaseModel> userByEmail = databaseRep.findByEmail(request.getEmail());

        if (userByEmail.isPresent()) {

            UsersDatabaseModel existingUser = userByEmail.get();

            validationUtils.validateAccountStatus(existingUser.getIs_active(), existingUser.getIs_blacklisted());
            validationUtils.validateBlackListExpirationDate(LocalDateTime.now(), existingUser.getBlacklist_expire_at());

            if (existingUser.getPassword_reset_expiration().isBefore(LocalDateTime.now())) {
                throw new CustomRuntimeException(
                        new ErrorHandler(
                                false,
                                "Expiration limit exceeded",
                                "Password reset code has expired. Please request a new reset code."),
                        HttpStatus.GONE
                );
            }

            if (!encoder.matches(request.getReset_code(), existingUser.getPassword_reset_code())) {
                throw new CustomRuntimeException(
                        new ErrorHandler(false,
                                "Invalid reset code",
                                "Kindly provide valid reset code"),
                        HttpStatus.UNAUTHORIZED
                );
            }

            // Build updated user details with password reset info
            UsersDatabaseModel updatedUserModel = UsersDatabaseModel.builder()
                    .id(existingUser.getId())
                    .user_id(existingUser.getUser_id())
                    .email(existingUser.getEmail())
                    .password(encoder.encode(request.getNew_password()))
                    .role(existingUser.getRole())
                    .organisation_id(existingUser.getOrganisation_id())
                    .password_reset_code("")
                    .password_reset_expiration(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                    .confirmation_link(existingUser.getConfirmation_link())
                    .confirmation_code(existingUser.getConfirmation_code())
                    .confirmation_token_expiration(existingUser.getConfirmation_token_expiration())
                    .is_active(existingUser.getIs_active())
                    .is_blacklisted(existingUser.getIs_blacklisted())
                    .blacklist_expire_at(existingUser.getBlacklist_expire_at())
                    .created_at(existingUser.getCreated_at())
                    .updated_at(existingUser.getUpdated_at())
                    .build();

            UsersDatabaseModel savedUser = databaseSaveUpdatedService.saveUpdatedUserDetails(updatedUserModel);

            UserCacheModel updatedCacheModel = UserCacheModel.builder()
                    .id(savedUser.getId())
                    .user_id(savedUser.getUser_id())
                    .email(savedUser.getEmail())
                    .password(savedUser.getPassword())
                    .role(savedUser.getRole())
                    .organisation_id(savedUser.getOrganisation_id())
                    .password_reset_code(savedUser.getPassword_reset_code())
                    .password_reset_expiration(utilitiesManager.convertDateTimeToString(savedUser.getPassword_reset_expiration()))
                    .confirmation_link(existingUser.getConfirmation_link())
                    .confirmation_code(existingUser.getConfirmation_code())
                    .confirmation_token_expiration(utilitiesManager.convertDateTimeToString(existingUser.getConfirmation_token_expiration()))
                    .is_active(existingUser.getIs_active())
                    .is_blacklisted(savedUser.getIs_blacklisted())
                    .blacklist_expire_at(utilitiesManager.convertDateTimeToString(savedUser.getBlacklist_expire_at()))
                    .created_at(utilitiesManager.convertDateTimeToString(savedUser.getCreated_at()))
                    .updated_at(utilitiesManager.convertDateTimeToString(savedUser.getUpdated_at()))
                    .build();

            Boolean cacheUpdateResult = cacheService.saveUpdateUserDetails(updatedCacheModel);

            if (!cacheUpdateResult) {
                messageBrokerManager.updateUserDetailsThroughMQ("create", updatedCacheModel);
            }

            jwtService.resetUserAccessToken(savedUser);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResetPasswordResModel(
                            true,
                            "Password has been updated successfully",
                            new ResetPasswordResModel.Details(
                                    savedUser.getEmail(),
                                    savedUser.getUpdated_at()
                            )
                    )
            );
        }

        throw new CustomRuntimeException(
                new ErrorHandler(false,
                        "invalid email",
                        "Kindly provide valid email address"),
                HttpStatus.BAD_REQUEST
        );
    }
}
