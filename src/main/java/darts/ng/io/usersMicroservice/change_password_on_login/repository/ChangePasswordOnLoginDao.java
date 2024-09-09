package darts.ng.io.usersMicroservice.change_password_on_login.repository;


import darts.ng.io.usersMicroservice.change_password_on_login.entity.ChangePasswordOnLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChangePasswordOnLoginDao extends JpaRepository<ChangePasswordOnLogin, Long> {
    Optional<ChangePasswordOnLogin> findByEmail(String Email);
}