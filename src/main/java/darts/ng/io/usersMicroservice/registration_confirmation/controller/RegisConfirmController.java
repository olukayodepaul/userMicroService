package darts.ng.io.usersMicroservice.registration_confirmation.controller;


import darts.ng.io.usersMicroservice.registration_confirmation.entity.RegisConfirmReq;
import darts.ng.io.usersMicroservice.registration_confirmation.service.RegisConfirmImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegisConfirmController {

    private final RegisConfirmImpl regisConfirmService;

    public RegisConfirmController(RegisConfirmImpl regisConfirmService){
        this.regisConfirmService = regisConfirmService;
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> regConfirmation(@RequestBody RegisConfirmReq request){
        return regisConfirmService.RegConfirm(request);
    }

}
