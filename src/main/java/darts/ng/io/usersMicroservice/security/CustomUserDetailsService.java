package darts.ng.io.usersMicroservice.security;

import darts.ng.io.usersMicroservice.login.entity.LoginModel;
import darts.ng.io.usersMicroservice.login.repository.LoginRepo;
import darts.ng.io.usersMicroservice.util.CustomException;
import darts.ng.io.usersMicroservice.util.RegErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginRepo loginRepo;

    public CustomUserDetailsService(LoginRepo loginRepo) {
        this.loginRepo = loginRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username){

        Optional<LoginModel> loginModel = loginRepo.findByEmail(username);

        if(loginModel.isPresent()) {

            LoginModel result = loginModel.get();

            return new User(
                    result.getEmail(),
                    result.getPassword(),
                    Collections.emptyList()
            );
        }

        throw new CustomException(
                new RegErrorHandler(false, "Invalid request data. Email and password must be provided."),
                HttpStatus.BAD_REQUEST
        );
    }
}

