package darts.ng.io.usersMicroservice.darts_app.controller;


import darts.ng.io.usersMicroservice.darts_app.entity.*;
import darts.ng.io.usersMicroservice.darts_app.service.authentication_service.UserLoginServiceImpl;
import darts.ng.io.usersMicroservice.darts_app.service.authentication_service.RequestResetPasswordServiceImpl;
import darts.ng.io.usersMicroservice.darts_app.service.authentication_service.ResetPasswordServiceImpl;
import darts.ng.io.usersMicroservice.darts_app.service.authentication_service.ChangePasswordOnLoginServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserLoginServiceImpl authenticationService;
    private final RequestResetPasswordServiceImpl requestResetPasswordService;
    private final ResetPasswordServiceImpl resetPasswordService;
    private final ChangePasswordOnLoginServiceImpl changePasswordOnLoginService;

    public AuthenticationController(UserLoginServiceImpl authenticationService, RequestResetPasswordServiceImpl requestResetPasswordService, ResetPasswordServiceImpl resetPasswordService, ChangePasswordOnLoginServiceImpl changePasswordOnLoginService) {
        this.authenticationService = authenticationService;
        this.requestResetPasswordService = requestResetPasswordService;
        this.resetPasswordService = resetPasswordService;
        this.changePasswordOnLoginService = changePasswordOnLoginService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResModel> userAuthentication(@RequestBody  UserLoginReqModel bodyRequest) {
        return authenticationService.userAuthentication(bodyRequest);
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<RequestResetPasswordResModel> requestPasswordReset(@RequestBody RequestResetPasswordReqModel request) {
        return requestResetPasswordService.requestPasswordReset(request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResModel> resetPassword(@RequestBody ResetPasswordReqModel request) {
        return resetPasswordService.resetPassword(request);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ChangePasswordOnLoginResModel> changePasswordOnLogin(
            @RequestBody ChangePasswordOnLoginReqModel request,
            @RequestHeader("Authorization") String token
    ) {
        return changePasswordOnLoginService.changePasswordOnLogin(request, token);
    }

}
