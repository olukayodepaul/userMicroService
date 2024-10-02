package darts.ng.io.usersMicroservice.darts_app.repository;


import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDatabaseRepo extends JpaRepository<UsersDatabaseModel,Long> {
    Optional<UsersDatabaseModel> findByEmail(String Email);
    Optional<UsersDatabaseModel> findById(Integer Id);
}