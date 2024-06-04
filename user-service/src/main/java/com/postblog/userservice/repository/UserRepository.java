package com.postblog.userservice.repository;

import com.postblog.userservice.entities.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByEmail(String email);

  UserEntity findByUsername(String username);

  @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles where u.username = :username")
  UserEntity findByUsernameFetchRoles(@Param("username") String username);
}
