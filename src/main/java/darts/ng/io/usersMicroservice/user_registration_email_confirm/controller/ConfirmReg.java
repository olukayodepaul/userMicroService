package darts.ng.io.usersMicroservice.user_registration_email_confirm.controller;


import darts.ng.io.usersMicroservice.user_registration_email_confirm.data.ConfirmRegReq;
import darts.ng.io.usersMicroservice.user_registration_email_confirm.service.ConfirmRegService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class ConfirmReg {

    private final ConfirmRegService confirmRegService;

    public ConfirmReg(ConfirmRegService confirmRegService){
        this.confirmRegService = confirmRegService;
    }

    @PostMapping("/send-confirmation-email")
    public ResponseEntity<?> confirmReg(@RequestBody ConfirmRegReq response){
        System.out.println(response.getUserId());
        return confirmRegService.confirmReg(response);
    }

}
