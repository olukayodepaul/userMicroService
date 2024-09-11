package darts.ng.io.usersMicroservice.security;

import darts.ng.io.usersMicroservice.login.entity.LoginModel;
import darts.ng.io.usersMicroservice.login.repository.LoginRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginRepo loginRepo;

    public CustomUserDetailsService(LoginRepo loginRepo) {
        this.loginRepo = loginRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginModel loginModel = loginRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return new org.springframework.security.core.userdetails.User(
                loginModel.getEmail(),
                loginModel.getPassword(),
                Collections.emptyList() // You can add roles/authorities here if needed
        );
    }
}