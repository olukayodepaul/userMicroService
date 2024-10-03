package darts.ng.io.usersMicroservice.darts_app.service.user_account_service;

import darts.ng.io.usersMicroservice.darts_app.entity.SendRegistrationConfirmationEmailToUserResModel;
import darts.ng.io.usersMicroservice.darts_app.entity.SendRegistrationConfirmationEmailToUserReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.entity.mapper.UserRecordMapper;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service class for handling user email confirmation processes.
 * It validates user accounts and sends confirmation tokens via email.
 */
@Service
public class SendRegistrationConfirmationEmailToUserServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(SendRegistrationConfirmationEmailToUserServiceImpl.class);

    private final UserDatabaseRepo updateRepo;
    private final UtilitiesManager utilitiesManager;
    private final ValidationUtils validationUtils;
    private final DbSaveUpdatedService dbSaveUpdatedService;

    /**
     * Constructor for UserRegistrationEmailConfirmationServiceImpl.
     *
     * @param updateRepo          Repository for updating user data.
     * @param utilitiesManager    Utility manager for generating tokens, strings, etc.
     * @param validationUtils     Utility class for validating emails and user statuses.
     * @param dbSaveUpdatedService Service for saving and updating user records in the database.
     */
    public SendRegistrationConfirmationEmailToUserServiceImpl(
            UserDatabaseRepo updateRepo,
            UtilitiesManager utilitiesManager,
            ValidationUtils validationUtils,
            DbSaveUpdatedService dbSaveUpdatedService
    ) {
        this.updateRepo = updateRepo;
        this.utilitiesManager = utilitiesManager;
        this.validationUtils = validationUtils;
        this.dbSaveUpdatedService = dbSaveUpdatedService;
    }

    /**
     * Validates and sends a confirmation email with a confirmation link and token.
     *
     * @param request The email confirmation request containing the user's email.
     * @return ResponseEntity containing the result of the confirmation process.
     * @throws CustomRuntimeException if validation fails or the user account is invalid.
     */
    public ResponseEntity<SendRegistrationConfirmationEmailToUserResModel> sendConfirmationEmail(SendRegistrationConfirmationEmailToUserReqModel request) {

        // Validate email format
        validationUtils.sanitizeEmail(request.getEmail());

        // Check if the user exists
        UsersDatabaseModel existingUser = updateRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, "validation check", "Email does not exist"),
                        HttpStatus.NOT_FOUND
                ));


        // Validate the user account's active and blacklist status
        validationUtils.validateAccountStatus(existingUser.getIs_active(), existingUser.getIs_blacklisted());
        validationUtils.validateBlackListExpirationDate(LocalDateTime.now(), existingUser.getBlacklist_expire_at());

        // Update the user account with new confirmation token and link
        UsersDatabaseModel updateUser = updateUser(existingUser);

        // Save the updated user record
        UserRecordMapper saveResult = dbSaveUpdatedService.saveUpdatedUserRecord(updateUser);

        // Handle failure in saving the user
        if (!saveResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "We can't confirm the email at this moment", saveResult.getError()),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Build the response with the updated record and return
        return ResponseEntity.status(HttpStatus.OK).body(buildResponse(saveResult));
    }

    /**
     * Updates the user model with a new confirmation link, token, and expiration time.
     *
     * @param request The user data to be updated.
     * @return UsersDatabaseModel containing updated user details.
     */
    private UsersDatabaseModel updateUser(UsersDatabaseModel request) {
        LocalDateTime dateTime = LocalDateTime.now();
        return UsersDatabaseModel.builder()
                .id(request.getId())
                .uuid(request.getUuid())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .organisation_id(request.getOrganisation_id())
                .password_reset_code(request.getPassword_reset_code())
                .password_reset_expiration(request.getPassword_reset_expiration())
                .confirmation_link(utilitiesManager.generateVerificationString())
                .confirmation_code(utilitiesManager.SixRandomDigitNumberGenerator().toString())
                .confirmation_token_expiration(utilitiesManager.expiryDatePeriod(AppConfig.EMAIL_CONFIRMATION_EXPIRATION))
                .is_active(request.getIs_active())
                .is_blacklisted(request.getIs_blacklisted())
                .password_reset_expiration(request.getPassword_reset_expiration())
                .created_at(request.getCreated_at())
                .updated_at(dateTime)
                .build();
    }

    /**
     * Builds the response to be sent after a successful confirmation link generation.
     *
     * @param updatedRecord The updated user record after processing.
     * @return UserRegistrationEmailConfirmationResModel containing the confirmation details.
     */
    private SendRegistrationConfirmationEmailToUserResModel buildResponse(UserRecordMapper updatedRecord) {
        return new SendRegistrationConfirmationEmailToUserResModel(
                true,
                "Confirmation link and token are generated. Kindly send to user email for confirmation.",
                new SendRegistrationConfirmationEmailToUserResModel.Details(
                        updatedRecord.getUsers().getEmail(),
                        updatedRecord.getUsers().getConfirmation_code(),
                        updatedRecord.getUsers().getConfirmation_link(),
                        updatedRecord.getUsers().getUpdated_at()
                )
        );
    }
}
