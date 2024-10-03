package darts.ng.io.usersMicroservice.darts_app.service.authentication_service;

import darts.ng.io.usersMicroservice.darts_app.entity.*;
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


@Service
public class RequestResetPasswordServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(RequestResetPasswordServiceImpl.class);
    private final UserDatabaseRepo databaseRep;
    private final UtilitiesManager utilitiesManager;
    private final ValidationUtils validationUtils;
    private final DbSaveUpdatedService dbSaveUpdatedService;

    public RequestResetPasswordServiceImpl(
            UserDatabaseRepo databaseRep,
            UtilitiesManager utilitiesManager,
            ValidationUtils validationUtils,
            DbSaveUpdatedService dbSaveUpdatedService
    ) {
        this.databaseRep = databaseRep;
        this.utilitiesManager = utilitiesManager;
        this.validationUtils = validationUtils;
        this.dbSaveUpdatedService = dbSaveUpdatedService;
    }

    public ResponseEntity<RequestResetPasswordResModel> requestPasswordReset(RequestResetPasswordReqModel request) {

        validationUtils.sanitizeEmail(request.getEmail());
        validationUtils.bruteForceProtection(request.getEmail(), AppConfig.REQUEST_PASSWORD_RESET_LIMIT);

        // Check if the user exists
        UsersDatabaseModel existingUser = databaseRep.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, AppConfig.KAY_ERROR, AppConfig.INVALID_EMAIL),
                        HttpStatus.NOT_FOUND
                ));

        validationUtils.validateAccountStatus(existingUser.getIs_active(), existingUser.getIs_blacklisted());
        validationUtils.validateBlackListExpirationDate(LocalDateTime.now(), existingUser.getBlacklist_expire_at());
        UsersDatabaseModel updateUser = requestPassword(existingUser);
        UserRecordMapper saveResult = dbSaveUpdatedService.saveUpdatedUserRecord(updateUser);

        if (!saveResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "We can't confirm the email at this moment", saveResult.getError()),
                    HttpStatus.BAD_REQUEST
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponse(saveResult));

    }

    private UsersDatabaseModel requestPassword(UsersDatabaseModel user) {
        String plainPassword  = utilitiesManager.SixRandomDigitNumberGenerator().toString();
        String encodedPassword = validationUtils.plainTextEncryption(plainPassword);
        return UsersDatabaseModel.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .organisation_id(user.getOrganisation_id())
                .password_reset_code(encodedPassword)
                .password_reset_expiration(utilitiesManager.expiryDatePeriod(AppConfig.PASSWORD_EXPIRATION))
                .confirmation_link(user.getConfirmation_link())
                .confirmation_code(user.getConfirmation_code())
                .confirmation_token_expiration(user.getConfirmation_token_expiration())
                .is_active(user.getIs_active())
                .is_blacklisted(user.getIs_blacklisted())
                .blacklist_expire_at(user.getBlacklist_expire_at())
                .created_at(user.getCreated_at())
                .updated_at(user.getUpdated_at())
                .build();
    }

    /**
     * Builds the response to be sent after a successful confirmation link generation.
     *
     * @param updatedRecord The updated user record after processing.
     * @return UserRegistrationEmailConfirmationResModel containing the confirmation details.
     */
    private RequestResetPasswordResModel buildResponse(UserRecordMapper updatedRecord) {
        return new RequestResetPasswordResModel(
                true,
                "Confirmation link and token are generated. Kindly send to user email for confirmation.",
                new RequestResetPasswordResModel.Details(
                        updatedRecord.getUsers().getEmail(),
                        updatedRecord.getUsers().getConfirmation_code(),
                        updatedRecord.getUsers().getConfirmation_link(),
                        updatedRecord.getUsers().getUpdated_at()
                )
        );
    }
}

