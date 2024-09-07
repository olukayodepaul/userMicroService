package darts.ng.io.usersMicroservice.login.controller;

import darts.ng.io.usersMicroservice.login.model.LoginReq;
import darts.ng.io.usersMicroservice.login.service.LoginImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class Login {

    private LoginImpl loginImpl;

    public Login(LoginImpl loginImpl){
        this.loginImpl = loginImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq request) {
        return   loginImpl.loginUser(request);
    }

}
