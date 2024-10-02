package darts.ng.io.usersMicroservice.darts_app.service.blacklist_service;


import darts.ng.io.usersMicroservice.darts_app.entity.FetchUserDetailsCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.WhitelistReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserBlackListedDbModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.kafka.MessageBrokerManager;
import darts.ng.io.usersMicroservice.darts_app.repository.UserBlackListedRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserRedisCacheRepo;
import darts.ng.io.usersMicroservice.security.JwtService;
import darts.ng.io.usersMicroservice.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WhitelistServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(WhitelistServiceImpl.class);
    private final UserDatabaseRepo userDatabaseRepo;
    private final UserRedisCacheRepo cacheService;
    private final MessageBrokerManager messageBrokerManager;
    private final UtilitiesManager utilitiesManager;
    private final JwtService jwtService;
    private final ValidationUtils validationUtils;
    private final DatabaseSaveUpdatedService databaseSaveUpdatedService;
    private final UserBlackListedRepo userBlackListedRepo;

    public WhitelistServiceImpl(
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

    public ResponseEntity<Void> whitelistUser(Integer user_id, WhitelistReqModel request){

        Optional<UsersDatabaseModel> getUserById = userDatabaseRepo.findById(user_id);

        if (getUserById.isPresent()) {

            UsersDatabaseModel existingUser = getUserById.get();

            UsersDatabaseModel whileListUserModel = UsersDatabaseModel.builder()
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
                    .is_blacklisted(false)
                    .blacklist_expire_at(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                    .created_at(existingUser.getCreated_at())
                    .updated_at(existingUser.getUpdated_at())
                    .build();


            UsersDatabaseModel savedUser = databaseSaveUpdatedService.saveUpdatedUserDetails(whileListUserModel);

            if (savedUser.getId().equals(existingUser.getId())) {

                UserBlackListedDbModel saveUserModel = UserBlackListedDbModel.builder()
                        .userId(user_id)
                        .ip_address("0.0.0.")//check the ip to proper ip
                        .reason(request.getReason())
                        .is_active(false)
                        .created_at(LocalDateTime.now())
                        .updated_at(LocalDateTime.now())
                        .expiry_at(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                        .build();

                userBlackListedRepo.save(saveUserModel);

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
