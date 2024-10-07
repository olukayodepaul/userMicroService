package darts.ng.io.usersMicroservice.darts_app.service.blacklist_service;

import darts.ng.io.usersMicroservice.darts_app.entity.AddBlacklistEntryReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.AddBlacklistEntryResModel;
import darts.ng.io.usersMicroservice.darts_app.entity.FetchUserDetailsCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserBlackListedDbModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.entity.mapper.UserRecordMapper;
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
import java.util.UUID;


@Service
public class AddBlacklistEntryServiceImpl {


    private static final Logger logger = LoggerFactory.getLogger(AddBlacklistEntryServiceImpl.class);
    private final UserDatabaseRepo userDatabaseRepo;
    private final UtilitiesManager utilitiesManager;
    private final JwtService jwtService;
    private final ValidationUtils validationUtils;
    private final DbSaveUpdatedService dbSaveUpdatedService;
    private final UserBlackListedRepo userBlackListedRepo;
    private final UserRedisCacheRepo cacheService;
    private final MessageBrokerManager messageBrokerManager;


    public AddBlacklistEntryServiceImpl(
            UserDatabaseRepo userDatabaseRepo,
            UserRedisCacheRepo cacheService,
            MessageBrokerManager messageBrokerManager,
            UtilitiesManager utilitiesManager,
            JwtService jwtService,
            ValidationUtils validationUtils,
            DbSaveUpdatedService dbSaveUpdatedService,
            UserBlackListedRepo userBlackListedRepo
    ) {
        this.userDatabaseRepo = userDatabaseRepo;
        this.cacheService = cacheService;
        this.messageBrokerManager = messageBrokerManager;
        this.utilitiesManager = utilitiesManager;
        this.jwtService = jwtService;
        this.validationUtils = validationUtils;
        this.dbSaveUpdatedService = dbSaveUpdatedService;
        this.userBlackListedRepo = userBlackListedRepo;
    }


    public ResponseEntity<AddBlacklistEntryResModel> addBlacklistEntry(
            AddBlacklistEntryReqModel bodyRequest,
            HttpServletRequest headerRequest,
            String token
    ) {

        validationUtils.tokenValidateRequest(token);

        String ipAddress = headerRequest.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = headerRequest.getRemoteAddr();
        }

        validationUtils.validateRequestFromBlackList(bodyRequest);
        utilitiesManager.isValidNumber(bodyRequest.getPeriod_in_second());

        UsersDatabaseModel existingUser = userDatabaseRepo.findByEmail(jwtService.extractEmail(token))
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, AppConfig.KAY_ERROR, AppConfig.INVALID_EMAIL),
                        HttpStatus.NOT_FOUND
                ));

        validationUtils.validateAccountNotConfirm(existingUser.getIs_active());
        validationUtils.validateAccountBlackListed(existingUser.getIs_blacklisted());
        validationUtils.blackListExpiration(existingUser.getBlacklist_expire_at());

        UsersDatabaseModel updateUser = updateUser(existingUser, bodyRequest.getPeriod_in_second());
        UserRecordMapper saveResult = dbSaveUpdatedService.saveUpdatedUserRecord(updateUser);

        if (!saveResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "We can't confirm the user at the moment", saveResult.getError()),
                    HttpStatus.BAD_REQUEST
            );
        }

        //testing deleting
        FetchUserDetailsCacheModel deleteCacheRecord = cacheService.deleteUserDetails(saveResult.getUsers().getEmail());

        //Todo: change this to kafka
        if (deleteCacheRecord.getStatus()) {
            messageBrokerManager.deleteUserDetailsFromCacheMQ("delete", saveResult.getUsers().getEmail());
        }


        UserBlackListedDbModel newBlacklist = blackListTrail(saveResult.getUsers().getUuid(), bodyRequest.getReason(), bodyRequest.getPeriod_in_second(), ipAddress);
        UserBlackListedDbModel blackListTrailSaveResult = userBlackListedRepo.save(newBlacklist);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new AddBlacklistEntryResModel(true,  saveResult.getUsers().getEmail()+" account is blackList", blackListTrailSaveResult.getId())
        );
    }

    /**
     * Updates the user model with a new confirmation link, token, and expiration time.
     *
     * @param request The user data to be updated.
     * @return UsersDatabaseModel containing updated user details.
     */
    private UsersDatabaseModel updateUser(UsersDatabaseModel request, String periodInSeconds) {
        return UsersDatabaseModel.builder()
                .id(request.getId())
                .uuid(request.getUuid())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .organisation_id(request.getOrganisation_id())
                .password_reset_code(request.getPassword_reset_code())
                .password_reset_expiration(request.getPassword_reset_expiration())
                .confirmation_link(request.getConfirmation_link())
                .confirmation_code(request.getConfirmation_code())
                .confirmation_token_expiration(request.getConfirmation_token_expiration())
                .is_active(request.getIs_active())
                .is_blacklisted(true)
                .blacklist_expire_at(utilitiesManager.expiryDatePeriod(utilitiesManager.convertStringToNumber(periodInSeconds)))
                .created_at(request.getCreated_at())
                .updated_at(request.getUpdated_at())
                .build();
    }

    private UserBlackListedDbModel blackListTrail(UUID uuid, String reason, String periodInSeconds, String ip) {
        LocalDateTime dateTime = LocalDateTime.now();
        return UserBlackListedDbModel.builder()
                .uuid(uuid)
                .ip_address(ip)
                .reason(reason)
                .is_active(true)
                .created_at(dateTime)
                .updated_at(dateTime)
                .expiry_at(utilitiesManager.expiryDatePeriod(utilitiesManager.convertStringToNumber(periodInSeconds)))
                .build();
    }

}

