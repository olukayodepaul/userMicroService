package darts.ng.io.usersMicroservice.login.repository;

import darts.ng.io.usersMicroservice.login.entity.LoginModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepo extends JpaRepository<LoginModel, Integer> {
    Optional<LoginModel> findByEmail(String Email);
}
