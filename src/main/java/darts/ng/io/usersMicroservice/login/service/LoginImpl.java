package darts.ng.io.usersMicroservice.login.service;

import darts.ng.io.usersMicroservice.login.entity.LoginModel;
import darts.ng.io.usersMicroservice.login.entity.LoginReq;
import darts.ng.io.usersMicroservice.login.entity.LoginRes;
import darts.ng.io.usersMicroservice.login.repository.LoginRepo;
import darts.ng.io.usersMicroservice.security.JwtService;
import darts.ng.io.usersMicroservice.util.CustomException;
import darts.ng.io.usersMicroservice.util.EmailValidator;
import darts.ng.io.usersMicroservice.util.RegErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class LoginImpl {
    private final LoginRepo loginRepo;
    private final EmailValidator emailValidator;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final JwtService jwtService;

    public LoginImpl(LoginRepo loginRepo, EmailValidator emailValidator, JwtService jwtService) {
        this.loginRepo = loginRepo;
        this.emailValidator = emailValidator;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> loginUser(LoginReq request) {

        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid request data. Email and password must be provided."),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!emailValidator.isValid(request.getEmail())) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid email format"),
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<LoginModel> response = loginRepo.findByEmail(request.getEmail());

        if (response.isEmpty()) {
            throw new CustomException(
                    new RegErrorHandler(false, "User not found. Please check your email or register."),
                    HttpStatus.NOT_FOUND
            );
        } else {

            LoginModel loginModel = response.get();

            if (!loginModel.isStatus()) {
                throw new CustomException(
                        new RegErrorHandler(false, "Email not confirmed. Please verify your email before login."),
                        HttpStatus.FORBIDDEN
                );
            }

            System.out.println(loginModel.getPassword());
            if (!encoder.matches(request.getPassword(), loginModel.getPassword())) {
                throw new CustomException(
                        new RegErrorHandler(false, "Invalid password. Please try again."),
                        HttpStatus.UNAUTHORIZED
                );
            }

            // Generate the JWT token after successful login
            String jwtToken = jwtService.generateToken(new HashMap<>(), loginModel);

            // Create response with token
            LoginRes rst = new LoginRes(
                    true,
                    "Successfully logged in.",
                    loginModel.getUserid(),
                    jwtToken  // Include the JWT in the response
            );

            return new ResponseEntity<>(rst, HttpStatus.OK);
        }
    }

}
