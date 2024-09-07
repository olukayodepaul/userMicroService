package darts.ng.io.usersMicroservice.change_login_password.controller;

import darts.ng.io.usersMicroservice.change_login_password.model.ChangePasswordReq;
import darts.ng.io.usersMicroservice.change_login_password.service.ChangePasswordImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ChangePassword {

    private final ChangePasswordImpl service;

    public ChangePassword(ChangePasswordImpl service) {
        this.service = service;
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody ChangePasswordReq request
    ){
        return service.changePassword(request);
    }

}
