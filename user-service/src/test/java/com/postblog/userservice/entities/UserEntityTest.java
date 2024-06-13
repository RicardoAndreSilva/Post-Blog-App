package com.postblog.userservice.entities;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.postblog.userservice.repository.UserRepository;
import com.postblog.userservice.utils.UserCreator;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
class UserEntityTest {

  @InjectMocks
  private UserEntity userEntity;

  @Mock
  private UserRepository userRepository;

  @BeforeEach
  public void setUp() {
    userEntity = new UserEntity(userEntity.getId(), "Test user", "testUserEntity@gmail.com", 25,
        "username",
        "12345", LocalDate.now().atStartOfDay(), "Admin", null, null, false, null);
  }

  @Test
  @DisplayName("Test for save user when successful")
  void testSaveUser_returnsOk_WhenSuccessful() {
    when(userRepository.save(userEntity)).thenReturn(userEntity);

    UserEntity savedUser = userRepository.save(userEntity);

    Assertions.assertThat(userEntity.getName()).isEqualTo(savedUser.getName());
    Assertions.assertThat(userEntity.getEmail()).isEqualTo(savedUser.getEmail());
    Assertions.assertThat(userEntity.getAge()).isEqualTo(savedUser.getAge());
    Assertions.assertThat(userEntity.getId()).isEqualTo(savedUser.getId());
    Assertions.assertThat(userEntity.getUsername()).isEqualTo(savedUser.getUsername());
    Assertions.assertThat(userEntity.getCreatedAt()).isEqualTo(savedUser.getCreatedAt());
  }

  @Test
  @DisplayName("Test for update user when successful")
  void testUpdateUser_returnsOk_WhenSuccessful() {
    userEntity.setId(1L);
    userEntity.setName("Updated User");
    userEntity.setEmail("updatedUser@gmail.com");
    userEntity.setAge(25);
    userEntity.setUsername("username");
    userEntity.setCreatedAt(LocalDate.now().atStartOfDay());
    userEntity.setCreatedBy("Admin");

    when(userRepository.save(userEntity)).thenReturn(userEntity);

    UserEntity updatedUser = userRepository.save(userEntity);

    Assertions.assertThat(userEntity.getName()).isEqualTo(updatedUser.getName());
    Assertions.assertThat(userEntity.getEmail()).isEqualTo(updatedUser.getEmail());
    Assertions.assertThat(userEntity.getAge()).isEqualTo(updatedUser.getAge());
    Assertions.assertThat(userEntity.getId()).isEqualTo(updatedUser.getId());
    Assertions.assertThat(userEntity.getUsername()).isEqualTo(updatedUser.getUsername());
    Assertions.assertThat(userEntity.getCreatedAt()).isEqualTo(updatedUser.getCreatedAt());
    Assertions.assertThat(userEntity.getCreatedBy()).isEqualTo(updatedUser.getCreatedBy());
  }

  @Test
  @DisplayName("Test for get user by id when successful")
  void testFindByIdUser_returnsOk_WhenSuccessful() {
    when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));

    Optional<UserEntity> foundUser = userRepository.findById(userEntity.getId());

    Assertions.assertThat(userEntity.getId()).isEqualTo(foundUser.get().getId());
  }

  @Test
  @DisplayName("Test for get user by id returns empty optional when user not found")
  void testFindByIdUser_returnsEmptyOptional_WhenUserNotFound() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    Optional<UserEntity> foundUser = userRepository.findById(5L);

    Assertions.assertThat(foundUser).isEmpty();
  }

  @Test
  @DisplayName("Test for delete user when successful")
  void testDeleteUser_returnsOk_WhenSuccessful() {
    userRepository.delete(userEntity);

    Optional<UserEntity> deletedUser = userRepository.findById(userEntity.getId());

    Assertions.assertThat(deletedUser).isEqualTo(Optional.empty());
  }

  @Test
  @DisplayName("Test for get all users when successful")
  void testGetAllUsers_returnsOk_WhenSuccessful() {
    UserEntity userEntity2 = UserCreator.createUserToBeSaved();

    when(userRepository.findAll()).thenReturn(Arrays.asList(userEntity, userEntity2));

    List<UserEntity> users = userRepository.findAll();

    Assertions.assertThat(users).hasSize(2);
    Assertions.assertThat(userEntity.getName()).isEqualTo(users.get(0).getName());
    Assertions.assertThat(userEntity2.getName()).isEqualTo(users.get(1).getName());
  }

  @Test
  @DisplayName("Test for equals() and hashCode()")
  void testEqualsAndHashCode_returnsOk_WhenSuccessful() {
    UserEntity user1 = new UserEntity(1L, "John", "test1@jUnit.com", 25, "username", "12345",
        LocalDate.now().atStartOfDay(), "Admin", null, null, false, null);
    UserEntity user2 = new UserEntity(1L, "John", "test1@jUnit.com", 25, "username", "12345",
        LocalDate.now().atStartOfDay(), "Admin", null, null, false, null);
    UserEntity user3 = new UserEntity(2L, "Jane", "test3@jUnit.com", 25, "username", "12345",
        LocalDate.now().atStartOfDay(), "Admin", null, null, false, null);

    Assertions.assertThat(user1.equals(user2)).isTrue();
    Assertions.assertThat(user1.hashCode()).hasSameHashCodeAs(user2.hashCode());

    Assertions.assertThat(user1.equals(user3)).isFalse();
    Assertions.assertThat(user1.hashCode()).isNotEqualTo(user3.hashCode());
  }


  @Test
  @DisplayName("Test for toString()")
  void testToString_returnsOk_WhenSuccessful() {
    UserEntity user = new UserEntity(1L, "spring", "junit5@example.com", 30, "username", "12345",
        LocalDate.now().atStartOfDay(), "admin", LocalDate.now().atStartOfDay(), null, false, null);

    String actualToString = user.toString();

    Assertions.assertThat(actualToString)
        .contains("UserEntity")
        .contains("id=1")
        .contains("name=spring")
        .contains("email=junit5@example.com")
        .contains("age=30")
        .contains("username=username")
        .contains("password=12345")
        .contains("createdAt=" + LocalDate.now().toString())
        .contains("createdBy=admin")
        .doesNotContain("lastModifiedAt" + LocalDate.now().toString());
  }


  @Test
  @DisplayName("Test object creation using Lombok @Builder")
  void testObjectCreationUsingBuilder_ReturnsOk_WhenSuccessful() {
    UserEntity user = UserEntity.builder().id(1L).name("spring").email("testeWithJunit5@test.com")
        .age(25)
        .username("username").createdAt(LocalDate.now().atStartOfDay()).build();

    Assertions.assertThat(user).isNotNull();
  }
}
