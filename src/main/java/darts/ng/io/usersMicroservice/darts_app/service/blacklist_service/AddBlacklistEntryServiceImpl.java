package darts.ng.io.usersMicroservice.darts_app.service.blacklist_service;

import darts.ng.io.usersMicroservice.darts_app.entity.AddBlacklistEntryReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.AddBlacklistEntryResModel;
import darts.ng.io.usersMicroservice.darts_app.entity.FetchUserDetailsCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserBlackListedDbModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.kafka.MessageBrokerManager;
import darts.ng.io.usersMicroservice.darts_app.repository.UserBlackListedRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserRedisCacheRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.security.JwtService;
import darts.ng.io.usersMicroservice.utilities.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class AddBlacklistEntryServiceImpl {


    private static final Logger logger = LoggerFactory.getLogger(AddBlacklistEntryServiceImpl.class);
    private final UserDatabaseRepo userDatabaseRepo;
    private final UserRedisCacheRepo cacheService;
    private final MessageBrokerManager messageBrokerManager;
    private final UtilitiesManager utilitiesManager;
    private final JwtService jwtService;
    private final ValidationUtils validationUtils;
    private final DatabaseSaveUpdatedService databaseSaveUpdatedService;
    private final UserBlackListedRepo userBlackListedRepo;

    public AddBlacklistEntryServiceImpl(
            UserDatabaseRepo userDatabaseRepo,
            UserRedisCacheRepo cacheService,
            MessageBrokerManager messageBrokerManager,
            UtilitiesManager utilitiesManager,
            JwtService jwtService,
            ValidationUtils validationUtils,
            DatabaseSaveUpdatedService databaseSaveUpdatedService,
            UserBlackListedRepo userBlackListedRepo
    ) {
        this.userDatabaseRepo = userDatabaseRepo;
        this.cacheService = cacheService;
        this.messageBrokerManager = messageBrokerManager;
        this.utilitiesManager = utilitiesManager;
        this.jwtService = jwtService;
        this.validationUtils = validationUtils;
        this.databaseSaveUpdatedService = databaseSaveUpdatedService;
        this.userBlackListedRepo = userBlackListedRepo;
    }


    public ResponseEntity<AddBlacklistEntryResModel> addBlacklistEntry(
            AddBlacklistEntryReqModel bodyRequest,
            HttpServletRequest headerRequest,
            String token
    ) {

        validationUtils.validateRequestFromBlackList(bodyRequest);
        utilitiesManager.isValidNumber(bodyRequest.getPeriod_in_second());

        String ipAddress = headerRequest.getRemoteAddr();

        Optional<UsersDatabaseModel> getUserById = userDatabaseRepo.findById(bodyRequest.getUser_id());

        if (getUserById.isPresent()) {

            UsersDatabaseModel existingUser = getUserById.get();
//            validationUtils.validateAccountStatus(existingUser.getIs_active(), existingUser.getIs_blacklisted());
//            validationUtils.validateBlackListExpirationDate(LocalDateTime.now(), existingUser.getBlacklist_expire_at());

            UsersDatabaseModel updatedUserModel = UsersDatabaseModel.builder()
                    .id(existingUser.getId())
                    .user_id(existingUser.getUser_id())
                    .email(existingUser.getEmail())
                    .password(existingUser.getPassword())
                    .role(existingUser.getRole())
                    .organisation_id(existingUser.getOrganisation_id())
                    .password_reset_code(existingUser.getPassword_reset_code())
                    .password_reset_expiration(existingUser.getPassword_reset_expiration())
                    .confirmation_link(existingUser.getConfirmation_link())
                    .confirmation_code(existingUser.getConfirmation_code())
                    .confirmation_token_expiration(existingUser.getConfirmation_token_expiration())
                    .is_active(existingUser.getIs_active())
                    .is_blacklisted(true)
                    .blacklist_expire_at(utilitiesManager.expiryDatePeriod(utilitiesManager.convertStringToNumber(bodyRequest.getPeriod_in_second())))
                    .created_at(existingUser.getCreated_at())
                    .updated_at(existingUser.getUpdated_at())
                    .build();


            UsersDatabaseModel savedUser = databaseSaveUpdatedService.saveUpdatedUserDetails(updatedUserModel);

            if (savedUser.getId().equals(existingUser.getId())) {

                UserBlackListedDbModel saveUserModel = UserBlackListedDbModel.builder()
                        .userId(bodyRequest.getUser_id())
                        .ip_address(ipAddress)
                        .reason(bodyRequest.getReason())
                        .is_active(true)
                        .created_at(LocalDateTime.now())
                        .updated_at(LocalDateTime.now())
                        .expiry_at(utilitiesManager.expiryDatePeriod(utilitiesManager.convertStringToNumber(bodyRequest.getPeriod_in_second())))
                        .build();

                userBlackListedRepo.save(saveUserModel);

                FetchUserDetailsCacheModel deleteCachedUser = cacheService.deleteUserDetails(savedUser.getEmail());

                if (deleteCachedUser.getStatus()) {
                    messageBrokerManager.deleteUserDetailsFromCacheMQ("delete", savedUser.getEmail());
                }

                return ResponseEntity.status(HttpStatus.CREATED).body(
                        new AddBlacklistEntryResModel(
                                true,
                                "User/IP added to blacklist successfully",
                                saveUserModel.getId()
                        )
                );
            }

            throw new CustomRuntimeException(
                    new ErrorHandler(false,
                            "error saving record into database",
                            "Kindly visit the support team"),
                    HttpStatus.BAD_REQUEST
            );

        }

        throw new CustomRuntimeException(
                new ErrorHandler(false,
                        "invalid user id",
                        "Kindly provide valid user id"),
                HttpStatus.BAD_REQUEST
        );
    }
}
