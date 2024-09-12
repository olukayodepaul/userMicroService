package darts.ng.io.usersMicroservice.send_confirmation_email_to_user.controller;


import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.entity.ConfirmRegReq;
import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.service.ConfirmRegImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class ConfirmReg {

    private final ConfirmRegImpl confirmRegService;

    public ConfirmReg(ConfirmRegImpl confirmRegService){
        this.confirmRegService = confirmRegService;
    }

    @GetMapping("/send-confirmation-email")
    public ResponseEntity<?> confirmReg(@RequestBody ConfirmRegReq response, @RequestHeader("Authorization") String authHeader){
        return confirmRegService.confirmReg(response, authHeader);
    }

}
