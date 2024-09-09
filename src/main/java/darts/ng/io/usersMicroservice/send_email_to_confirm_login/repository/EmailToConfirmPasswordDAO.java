package darts.ng.io.usersMicroservice.send_email_to_confirm_login.repository;

import darts.ng.io.usersMicroservice.send_email_to_confirm_login.entity.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface EmailToConfirmPasswordDAO extends JpaRepository<Database, Integer> {
    Optional<Database> findByEmail(String Email);
}
