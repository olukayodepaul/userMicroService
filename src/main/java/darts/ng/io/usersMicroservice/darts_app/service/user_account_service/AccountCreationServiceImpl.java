package darts.ng.io.usersMicroservice.darts_app.service.user_account_service;


import darts.ng.io.usersMicroservice.darts_app.entity.*;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.kafka.MessageBrokerManager;
import darts.ng.io.usersMicroservice.darts_app.repository.UserRedisCacheRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
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
public class AccountCreationServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(AccountCreationServiceImpl.class);
    private final UserDatabaseRepo registrationRepo;
    private final EmailValidator emailValidator;
    private final UserRedisCacheRepo cacheService;
    private final MessageBrokerManager messageBrokerManager;
    private final BCryptPasswordEncoder encoder;
    private final UtilitiesManager utilitiesManager;


    public AccountCreationServiceImpl(
            UserDatabaseRepo registrationRepo,
            EmailValidator emailValidator,
            UserRedisCacheRepo cacheService,
            MessageBrokerManager messageBrokerManager,
            UtilitiesManager utilitiesManager
    )
    {
        this.registrationRepo = registrationRepo;
        this.emailValidator = emailValidator;
        this.cacheService = cacheService;
        this.messageBrokerManager = messageBrokerManager;
        this.encoder = new BCryptPasswordEncoder(12);
        this.utilitiesManager = utilitiesManager;
    }


    public ResponseEntity<CreateAccountResModel> userRegistration(CreateAccountReqModel request) {

        validateRequest(request);
        validateEmail(request.getEmail());

        Optional<UsersDatabaseModel> dbListener = registrationRepo.findByEmail(request.getEmail());

        if (dbListener.isPresent()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false,
                            "",
                            "Email is already taken"),
                    HttpStatus.BAD_REQUEST
            );
        }


        UsersDatabaseModel user = createUserModel(request, utilitiesManager.generateUUID(request.getEmail()));

        UsersDatabaseModel fetchedSavedRecordFromDB = saveAndUpdateDb(user);

        //Todo: Complete the implementation. send other profile details to ProfileMicroservice
        messageBrokerManager.PushProfileToProfileMicroMServiceMQ(fetchedSavedRecordFromDB);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccountResModel(true, "New user successfully created", mapToResponseDetails(fetchedSavedRecordFromDB, request)));

    }


    public ResponseEntity<SendConfirmationResModel> sendConfirmationEmail(SendEmailReqModel request) {

        validateEmail(request.getEmail());

        Optional<UsersDatabaseModel> dbListener = registrationRepo.findByEmail(request.getEmail());

        if (dbListener.isPresent()) {

            UsersDatabaseModel fetchedRecordFromDB = dbListener.get();

            validateAccountStatus(fetchedRecordFromDB);

            UsersDatabaseModel dbModelBuilder = UsersDatabaseModel.builder()
                    .id(fetchedRecordFromDB.getId())
                    .user_id(fetchedRecordFromDB.getUser_id())
                    .email(fetchedRecordFromDB.getEmail())
                    .password(fetchedRecordFromDB.getPassword())
                    .role(fetchedRecordFromDB.getRole())
                    .organisation_id(fetchedRecordFromDB.getOrganisation_id())
                    .password_reset_code(fetchedRecordFromDB.getPassword_reset_code())
                    .password_reset_expiration(fetchedRecordFromDB.getPassword_reset_expiration())
                    .confirmation_link(utilitiesManager.generateVerificationString())
                    .confirmation_code(utilitiesManager.SixRandomDigitNumberGenerator().toString())
                    .confirmation_token_expiration(utilitiesManager.expiryDatePeriod(20)) //change to global seconds
                    .is_active(fetchedRecordFromDB.getIs_active())
                    .is_blacklisted(fetchedRecordFromDB.getIs_blacklisted())
                    .blacklist_expire_at(fetchedRecordFromDB.getBlacklist_expire_at())
                    .created_at(fetchedRecordFromDB.getCreated_at())
                    .updated_at(fetchedRecordFromDB.getUpdated_at())
                    .build();


            UsersDatabaseModel fetchedSavedRecordFromDB = saveAndUpdateDb(dbModelBuilder);

            if (fetchedSavedRecordFromDB.getId().equals(fetchedRecordFromDB.getId())) {

                return ResponseEntity.status(HttpStatus.OK).body(
                        new SendConfirmationResModel(
                                true,
                                "confirmation link and token is generated. Kindly send to user email for confirmation",
                                new SendConfirmationResModel.Details(
                                        fetchedSavedRecordFromDB.getEmail(),
                                        fetchedSavedRecordFromDB.getUser_id(),
                                        fetchedSavedRecordFromDB.getConfirmation_code(),
                                        fetchedSavedRecordFromDB.getConfirmation_link(),
                                        fetchedSavedRecordFromDB.getUpdated_at()
                                )
                        )
                );
            }

            throw new CustomRuntimeException(
                    new ErrorHandler(false,"", "Fail to generate confirmation code and link"),
                    HttpStatus.BAD_REQUEST
            );
        }

        throw new CustomRuntimeException(
                new ErrorHandler(false,"", "Invalid email"),
                HttpStatus.BAD_REQUEST
        );
    }

    public ResponseEntity<ResponseHandler> confirmRegEmail(ConfirmEmailReqModel request) {

        validateEmail(request.getEmail());
        validateConfirmationCodeLink(request);

        Optional<UsersDatabaseModel> dbListener = registrationRepo.findByEmail(request.getEmail());

        if (dbListener.isPresent()) {

            UsersDatabaseModel fetchedRecordFromDB = dbListener.get();

            //account expiration
            if (fetchedRecordFromDB.getConfirmation_token_expiration().isBefore(LocalDateTime.now())) {
                throw new CustomRuntimeException(
                        new ErrorHandler(false, "","Your confirmation code has expired. Please request a new one."),
                        HttpStatus.BAD_REQUEST
                );
            }

            if (!fetchedRecordFromDB.getConfirmation_code().equalsIgnoreCase(request.getConfirmation_code_link()) &&
                    !fetchedRecordFromDB.getConfirmation_link().equalsIgnoreCase(request.getConfirmation_code_link())) {
                throw new CustomRuntimeException(
                        new ErrorHandler(false, "","Invalid confirmation code or link. Please try again."),
                        HttpStatus.BAD_REQUEST
                );
            }

            validateAccountStatus(fetchedRecordFromDB);

            UsersDatabaseModel dbModelBuilder = UsersDatabaseModel.builder()
                    .id(fetchedRecordFromDB.getId())
                    .user_id(fetchedRecordFromDB.getUser_id())
                    .email(fetchedRecordFromDB.getEmail())
                    .password(fetchedRecordFromDB.getPassword())
                    .role(fetchedRecordFromDB.getRole())
                    .organisation_id(fetchedRecordFromDB.getOrganisation_id())
                    .password_reset_code("")
                    .password_reset_expiration(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                    .confirmation_link("")
                    .confirmation_code("")
                    .confirmation_token_expiration(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                    .is_active(true)
                    .is_blacklisted(fetchedRecordFromDB.getIs_blacklisted())
                    .blacklist_expire_at(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                    .created_at(fetchedRecordFromDB.getCreated_at())
                    .updated_at(fetchedRecordFromDB.getUpdated_at())
                    .build();

            UsersDatabaseModel fetchedSavedRecordFromDB = saveAndUpdateDb(dbModelBuilder);

            UserCacheModel cacheModelBuilder = UserCacheModel.builder()
                    .id(fetchedSavedRecordFromDB.getId())
                    .user_id(fetchedSavedRecordFromDB.getUser_id())
                    .email(fetchedSavedRecordFromDB.getEmail())
                    .password(fetchedSavedRecordFromDB.getPassword())
                    .role(fetchedSavedRecordFromDB.getRole())
                    .organisation_id(fetchedSavedRecordFromDB.getOrganisation_id())
                    .password_reset_code("")
                    .password_reset_expiration("1900-01-01 00:00:00")
                    .confirmation_link("")
                    .confirmation_code("")
                    .confirmation_token_expiration("1900-01-01 00:00:00")
                    .is_active(true).is_blacklisted(fetchedSavedRecordFromDB.getIs_blacklisted())
                    .blacklist_expire_at("1900-01-01 00:00:00")
                    .created_at(utilitiesManager.convertDateTimeToString(fetchedSavedRecordFromDB.getCreated_at()))
                    .updated_at(utilitiesManager.convertDateTimeToString(fetchedSavedRecordFromDB.getUpdated_at()))
                    .build();

            Boolean cacheListener = cacheService.saveUpdateUserDetails(cacheModelBuilder);

            //todo: if cache failed, then push to messageMq for retry
            if (!cacheListener) {
                messageBrokerManager.PushRegistrationDetailsToCacheMQ("create", cacheModelBuilder);
            }

            if (fetchedSavedRecordFromDB.getId().equals(fetchedRecordFromDB.getId())) {
                return new ResponseEntity<>(new ResponseHandler(true, "Email successful confirm"), HttpStatus.CREATED);
            }

            throw new CustomRuntimeException(
                    new ErrorHandler(false, "","Invalid Email"),
                    HttpStatus.BAD_REQUEST
            );
        }

        throw new CustomRuntimeException(
                new ErrorHandler(false, "","Invalid Email"),
                HttpStatus.BAD_REQUEST
        );
    }

    private void validateAccountStatus(UsersDatabaseModel request) {
        isAccountStatusValidated(request.getIs_active(), request.getEmail(), "Email already confirmed");
        isAccountStatusValidated(request.getIs_blacklisted(), request.getEmail(), "Account is blacklisted. Kindly visit the customer support team");
    }

    private UsersDatabaseModel saveAndUpdateDb(UsersDatabaseModel regDetails) {
        try {
            return registrationRepo.save(regDetails);
        } catch (Exception e) {
            logger.error("RegistrationImpl::saveRegistrationDetails - Error adding new users: {}", e.getMessage());
            throw new CustomRuntimeException(
                    new ErrorHandler(false,"", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private void validateEmail(String email) {
        if (!emailValidator.isValid(email)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "","Invalid email"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateConfirmationCodeLink(ConfirmEmailReqModel request) {
        validateField(request.getConfirmation_code_link(), "Confirmation code or link");
    }

    private void validateRequest(CreateAccountReqModel request) {
        validateField(request.getPassword(), "Password");
        validateField(request.getRole(), "User Role");
        validateField(request.getOrganisation_id(), "Organisation ID");
        validateField(request.getFirst_name(), "First Name");
        validateField(request.getLast_name(), "Last Name");
        validateField(request.getPhone_number(), "Phone Number");
        validateField(request.getDate_of_birth(), "Date of Birth");
        validateField(request.getGender(), "Gender");
        validateField(request.getBio(), "Bio");
    }

    private void validateField(Object field, String fieldName) {
        if (field == null) {
            throw new CustomRuntimeException(new ErrorHandler(false, "",fieldName + " cannot be null"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Kafka Message Broker.
     * Redis Stream Implementation
     */
    private void isAccountStatusValidated(Boolean field, String email, String comment) {
        if (field) {

            FetchUserDetailsCacheModel cacheListener = cacheService.deleteUserDetails(email);

            if(!cacheListener.getStatus() || cacheListener.getEvent()==2){
                messageBrokerManager.deleteUserDetailsFromCacheMQ("delete", email);
            }

            throw new CustomRuntimeException(new ErrorHandler(false, "",comment), HttpStatus.BAD_REQUEST);
        }
    }

    private UsersDatabaseModel createUserModel(CreateAccountReqModel request, String userId) {
        return UsersDatabaseModel.builder()
                .id(0)
                .user_id(userId)
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .organisation_id(request.getOrganisation_id())
                .password_reset_code("")
                .password_reset_expiration(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                .confirmation_link("")
                .confirmation_code("")
                .confirmation_token_expiration(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                .is_active(false)
                .is_blacklisted(false)
                .password_reset_expiration(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
    }

    private CreateAccountResModel.Details mapToResponseDetails(UsersDatabaseModel user, CreateAccountReqModel request) {
        return new CreateAccountResModel.Details(
                user.getEmail(),
                user.getUser_id(),
                user.getRole(),
                request.getFirst_name(),
                request.getLast_name(),
                request.getPhone_number(),
                request.getDate_of_birth(),
                request.getGender(),
                request.getBio(),
                user.getOrganisation_id(),
                user.getIs_active(),
                user.getIs_blacklisted(),
                user.getCreated_at(),
                user.getUpdated_at()
        );
    }

}



