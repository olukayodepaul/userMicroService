package darts.ng.io.usersMicroservice.send_email_to_confirm_login.controller;

import darts.ng.io.usersMicroservice.send_email_to_confirm_login.entity.EmailToConfirmPasswordReq;
import darts.ng.io.usersMicroservice.send_email_to_confirm_login.service.EmailToConfirmPasswordImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class EmailToConfirmPassword {

    private final EmailToConfirmPasswordImpl service;

    public EmailToConfirmPassword(EmailToConfirmPasswordImpl service) {
        this.service = service;
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> sendMail(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody EmailToConfirmPasswordReq request
    ) {
        return service.sendMail(request);
    }

}
