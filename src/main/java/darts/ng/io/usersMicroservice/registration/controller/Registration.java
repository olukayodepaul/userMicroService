package darts.ng.io.usersMicroservice.registration.controller;

import darts.ng.io.usersMicroservice.registration.entity.RegRequest;
import darts.ng.io.usersMicroservice.registration.service.RegistrationImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class Registration {

    private final RegistrationImpl registration;

    public Registration(RegistrationImpl registration){
        this.registration = registration;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegRequest request) {
        return registration.registerUser(request);
    }

}
