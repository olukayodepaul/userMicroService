package darts.ng.io.usersMicroservice.darts_app.controller;

import darts.ng.io.usersMicroservice.darts_app.entity.*;
import darts.ng.io.usersMicroservice.darts_app.service.user_account_service.SendRegistrationConfirmationEmailToUserServiceImpl;
import darts.ng.io.usersMicroservice.darts_app.service.user_account_service.UserRegistrationConfirmationServiceImpl;
import darts.ng.io.usersMicroservice.darts_app.service.user_account_service.UserRegistrationServiceImpl;
import darts.ng.io.usersMicroservice.utilities.ResponseHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;




@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final UserRegistrationServiceImpl userRegistrationService;
    private final SendRegistrationConfirmationEmailToUserServiceImpl userRegistrationEmailConfirmationService;
    private final UserRegistrationConfirmationServiceImpl userRegistrationConfirmationService;

    public AccountController(
            UserRegistrationServiceImpl userRegistrationService,
            SendRegistrationConfirmationEmailToUserServiceImpl userRegistrationEmailConfirmationService,
            UserRegistrationConfirmationServiceImpl userRegistrationConfirmationService)
    {
        this.userRegistrationService = userRegistrationService;
        this.userRegistrationEmailConfirmationService = userRegistrationEmailConfirmationService;
        this.userRegistrationConfirmationService = userRegistrationConfirmationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResModel> userRegistration(@RequestBody UserRegistrationReqModel request) {
        return userRegistrationService.userRegistration(request);
    }

    @PostMapping("/send-confirmation-email")
    public ResponseEntity<SendRegistrationConfirmationEmailToUserResModel> sendConfirmationEmail(@RequestBody SendRegistrationConfirmationEmailToUserReqModel request) {
        return userRegistrationEmailConfirmationService.sendConfirmationEmail(request);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<ResponseHandler> confirmRegEmail(@RequestBody UserRegistrationConfirmationReqModel request) {
        return userRegistrationConfirmationService.confirmRegEmail(request);
    }

}
