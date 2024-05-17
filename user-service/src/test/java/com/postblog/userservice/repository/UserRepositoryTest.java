package com.postblog.userservice.repository;

import com.postblog.userservice.configuration.PasswordEnconder;
import com.postblog.userservice.entities.UserEntity;
import com.postblog.userservice.utils.UserCreator;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;


@DataJpaTest
@DisplayName("Test for User repository")
@Import(PasswordEnconder.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRepositoryTest {

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("Test for save user when successful")
  void save_persistUserTest_WhenSuccessful() {
    UserEntity userToBeSaved = UserCreator.createUserToBeSaved();
    UserEntity userSaved = this.userRepository.save(userToBeSaved);

    Assertions.assertThat(userSaved).isNotNull();
    Assertions.assertThat(userSaved.getName()).isEqualTo(userToBeSaved.getName());
    Assertions.assertThat(userSaved.getEmail()).isEqualTo(userToBeSaved.getEmail());
    Assertions.assertThat(userSaved.getAge()).isEqualTo(userToBeSaved.getAge());
    Assertions.assertThat(userSaved.getUsername()).isEqualTo(userToBeSaved.getUsername());

  }

  @Test
  @DisplayName("Test for update user when successful")
  void update_persistUserTest_WhenSuccessful() {
    UserEntity userToBeSaved = UserCreator.createUserToBeSaved();
    UserEntity userSaved = this.userRepository.save(userToBeSaved);

    userSaved.setName("user test");
    userSaved.setEmail("teste@gmail.com");
    userSaved.setAge(25);
    userSaved.setUsername("username");

    String newPassword = bCryptPasswordEncoder.encode(userSaved.getPassword());
    userSaved.setPassword(newPassword);

    UserEntity userUpdated = this.userRepository.save(userSaved);

    Assertions.assertThat(userUpdated).isNotNull();
    Assertions.assertThat(userUpdated.getId()).isEqualTo(userSaved.getId());
    Assertions.assertThat(userUpdated.getName()).isEqualTo(userSaved.getName());
    Assertions.assertThat(userUpdated.getAge()).isEqualTo(userSaved.getAge());
    Assertions.assertThat(userUpdated.getEmail()).isEqualTo(userSaved.getEmail());
    Assertions.assertThat(userUpdated.getUsername()).isEqualTo(userSaved.getUsername());
    Assertions.assertThat(userUpdated.getCreatedAt()).isEqualTo(userSaved.getCreatedAt());
    Assertions.assertThat(userUpdated.getPassword()).isEqualTo(newPassword);
  }

  private String getCurrentUserOrSystem() {
    return "Admin";
  }


  @Test
  @DisplayName("Test for delete when successful")
  void delete_persistUserTest_WhenSuccessful() {
    UserEntity userToBySaved = UserCreator.createUserToBeSaved();
    UserEntity userSaved = this.userRepository.save(userToBySaved);

    this.userRepository.delete(userSaved);

    Optional<UserEntity> userOptional = this.userRepository.findById(userSaved.getId());

    Assertions.assertThat(userOptional).isEmpty();
  }

  @Test
  @DisplayName("Test for get user by id when successful")
  void findById_persistUserTest_WhenSuccessful() {
    UserEntity userToBeSaved = UserCreator.createUserToBeSaved();
    UserEntity userSaved = this.userRepository.save(userToBeSaved);
    Long userId = userSaved.getId();

    Optional<UserEntity> userOptional = this.userRepository.findById(userSaved.getId());

    Assertions.assertThat(userOptional).as("user Id" + userId).isPresent();
    Assertions.assertThat(userOptional.get().getId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("Test for get all users when successful")
  void findAll_persistUserTest_WhenSuccessful() {
    UserEntity userToBeSaved1 = UserCreator.createUserToBeSaved();
    UserEntity userToBeSaved2 = UserCreator.createValidUser();

    UserEntity userSaved1 = this.userRepository.save(userToBeSaved1);
    UserEntity userSaved2 = this.userRepository.save(userToBeSaved2);

    List<UserEntity> users = this.userRepository.findAll();

    Assertions.assertThat(users).isNotNull().hasSize(2).contains(userSaved1, userSaved2);
  }
}