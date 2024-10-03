package darts.ng.io.usersMicroservice.darts_app.repository;

import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserBlackListedDbModel;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

public interface UserBlackListedRepo extends JpaRepository<UserBlackListedDbModel, Long> {
    Optional<Page<UserBlackListedDbModel>> findByUuid(UUID Uuid, Pageable pageable);
}
