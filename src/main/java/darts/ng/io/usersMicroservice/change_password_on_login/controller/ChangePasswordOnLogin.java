package darts.ng.io.usersMicroservice.change_password_on_login.controller;


import darts.ng.io.usersMicroservice.change_password_on_login.entity.ChangePasswordOnLoginReq;
import darts.ng.io.usersMicroservice.change_password_on_login.servive.ChangePasswordOnLoginImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ChangePasswordOnLogin {

    private final ChangePasswordOnLoginImpl changePassword;

    public ChangePasswordOnLogin(ChangePasswordOnLoginImpl changePassword) {
        this.changePassword = changePassword;
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> save(@RequestBody ChangePasswordOnLoginReq request) {
        System.out.println(request.getEmail());
        return changePassword.onChangePassword(request);
    }

}
