package darts.ng.io.usersMicroservice.darts_app.controller;

import darts.ng.io.usersMicroservice.darts_app.entity.*;
import darts.ng.io.usersMicroservice.darts_app.service.user_account_service.AccountCreationServiceImpl;
import darts.ng.io.usersMicroservice.utilities.ResponseHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;



@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountCreationServiceImpl account;


    public AccountController(AccountCreationServiceImpl account) {
        this.account = account;
    }

    @PostMapping("/register")
    public ResponseEntity<CreateAccountResModel> userRegistration(@RequestBody CreateAccountReqModel request) {
        return account.userRegistration(request);
    }

    @PostMapping("/send-confirmation-email")
    public ResponseEntity<SendConfirmationResModel> sendConfirmationEmail(@RequestBody SendEmailReqModel request) {
        return account.sendConfirmationEmail(request);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<ResponseHandler> confirmRegEmail(@RequestBody ConfirmEmailReqModel response) {
        return account.confirmRegEmail(response);
    }

}
