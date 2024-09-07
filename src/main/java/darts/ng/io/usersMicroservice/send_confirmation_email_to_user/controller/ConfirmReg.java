package darts.ng.io.usersMicroservice.send_confirmation_email_to_user.controller;


import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.model.ConfirmRegReq;
import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.service.ConfirmRegImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class ConfirmReg {

    private final ConfirmRegImpl confirmRegService;

    public ConfirmReg(ConfirmRegImpl confirmRegService){
        this.confirmRegService = confirmRegService;
    }

    @PostMapping("/send-confirmation-email")
    public ResponseEntity<?> confirmReg(@RequestBody ConfirmRegReq response){
        return confirmRegService.confirmReg(response);
    }

}
