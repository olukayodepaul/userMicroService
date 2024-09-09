package darts.ng.io.usersMicroservice.registration.controller;

import darts.ng.io.usersMicroservice.registration.entity.RegRequestHandler;
import darts.ng.io.usersMicroservice.registration.service.RegistrationImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class Registration {

    private final RegistrationImpl registration;

    public Registration(RegistrationImpl registration){
        this.registration = registration;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegRequestHandler request) {
        return registration.registerUser(request);
    }

}
