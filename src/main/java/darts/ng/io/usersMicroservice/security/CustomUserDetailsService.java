package darts.ng.io.usersMicroservice.security;


import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.utilities.CustomRuntimeException;
import darts.ng.io.usersMicroservice.utilities.ErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDatabaseRepo loginRepo;
    private final BCryptPasswordEncoder encoder;

    public CustomUserDetailsService(UserDatabaseRepo loginRepo) {
        this.loginRepo = loginRepo;
        this.encoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public UserDetails loadUserByUsername(String email){
        Optional<UsersDatabaseModel> loginModel = loginRepo.findByEmail(email);
        if(loginModel.isPresent()) {
            UsersDatabaseModel result = loginModel.get();
            return new User(
                    result.getEmail(),
                    encoder.encode(result.getPassword()),
                    Collections.emptyList()
            );
        }

        throw new CustomRuntimeException(
                new ErrorHandler(false, "error", "Invalid request data. Email and password must be provided."),
                HttpStatus.BAD_REQUEST
        );
    }
}


