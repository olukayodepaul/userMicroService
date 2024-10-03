package darts.ng.io.usersMicroservice.darts_app.service.authentication_service;

import darts.ng.io.usersMicroservice.darts_app.entity.FetchUserDetailsCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.ResetPasswordReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.ResetPasswordResModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.entity.mapper.UserRecordMapper;
import darts.ng.io.usersMicroservice.darts_app.kafka.MessageBrokerManager;
import darts.ng.io.usersMicroservice.darts_app.repository.UserRedisCacheRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ResetPasswordServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordServiceImpl.class);
    private final UserDatabaseRepo databaseRep;
    private final UtilitiesManager utilitiesManager;
    private final MessageBrokerManager messageBrokerManager;
    private final ValidationUtils validationUtils;
    private final DbSaveUpdatedService dbSaveUpdatedService;
    private final UserRedisCacheRepo cacheService;

    /**
     * Constructor to initialize dependencies.
     *
     * @param databaseRep      the repository for accessing user data
     * @param utilitiesManager utility for general utility functions
     * @param cacheService     the cache repository for storing user details
     * @param messageBrokerManager the Kafka message broker for messaging
     */
    public ResetPasswordServiceImpl(
            UserDatabaseRepo databaseRep,
            UtilitiesManager utilitiesManager,
            MessageBrokerManager messageBrokerManager,
            ValidationUtils validationUtils,
            DbSaveUpdatedService dbSaveUpdatedService,
            UserRedisCacheRepo cacheService

    ) {
        this.databaseRep = databaseRep;
        this.utilitiesManager = utilitiesManager;
        this.messageBrokerManager = messageBrokerManager;
        this.validationUtils = validationUtils;
        this.dbSaveUpdatedService = dbSaveUpdatedService;
        this.cacheService = cacheService;
    }

    public ResponseEntity<ResetPasswordResModel> resetPassword(ResetPasswordReqModel request) {

        validationUtils.sanitizeEmail(request.getEmail());
        validationUtils.validateRequest(request);
        validationUtils.validatePasswordStrength(request.getNew_password());

        // Check if the user exists
        UsersDatabaseModel existingUser = databaseRep.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, "validation check", "Email does not exist"),
                        HttpStatus.NOT_FOUND
                ));

        // Validate the user account's active and blacklist status
        validationUtils.validateAccountStatus(existingUser.getIs_active(), existingUser.getIs_blacklisted());
        validationUtils.validateBlackListExpirationDate(LocalDateTime.now(), existingUser.getBlacklist_expire_at());

        UsersDatabaseModel updateUser = updateUserPassword(existingUser, request.getNew_password());
        UserRecordMapper saveResult = dbSaveUpdatedService.saveUpdatedUserRecord(updateUser);

        // Handle failure in saving the user
        if (!saveResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "We can't confirm the email at this moment", saveResult.getError()),
                    HttpStatus.BAD_REQUEST
            );
        }

        FetchUserDetailsCacheModel deleteCacheRecord = cacheService.deleteUserDetails(request.getEmail());

        //Todo: change this to kafka
        if (deleteCacheRecord.getStatus()) {
            messageBrokerManager.deleteUserDetailsFromCacheMQ("delete", request.getEmail());
        }

        // Build the response with the updated record and return
        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponse(saveResult));
    }

    /**
     * Updates the user model with a new confirmation link, token, and expiration time.
     *
     * @param request The user data to be updated.
     * @return UsersDatabaseModel containing updated user details.
     */
    private UsersDatabaseModel updateUserPassword(UsersDatabaseModel request, String newPassword) {
        String encodedPassword = validationUtils.plainTextEncryption(newPassword);
        return UsersDatabaseModel.builder()
                .id(request.getId())
                .uuid(request.getUuid())
                .email(request.getEmail())
                .password(encodedPassword)
                .role(request.getRole())
                .organisation_id(request.getOrganisation_id())
                .password_reset_code("")
                .password_reset_expiration(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                .confirmation_link(request.getConfirmation_link())
                .confirmation_code(request.getConfirmation_code())
                .confirmation_token_expiration(request.getConfirmation_token_expiration())
                .is_active(request.getIs_active())
                .is_blacklisted(request.getIs_blacklisted())
                .blacklist_expire_at(request.getBlacklist_expire_at())
                .created_at(request.getCreated_at())
                .updated_at(request.getUpdated_at())
                .build();
    }

    /**
     * Builds the response to be sent after a successful confirmation link generation.
     *
     * @param updatedRecord The updated user record after processing.
     * @return UserRegistrationEmailConfirmationResModel containing the confirmation details.
     */
    private ResetPasswordResModel buildResponse(UserRecordMapper updatedRecord) {
        return new ResetPasswordResModel(
                true,
                "Password has been updated successfully",
                new ResetPasswordResModel.Details(
                        updatedRecord.getUsers().getEmail(),
                        updatedRecord.getUsers().getUpdated_at()
                )
        );
    }

}
