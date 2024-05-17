package com.postblog.userservice.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.postblog.userservice.entities.UserEntity;
import com.postblog.userservice.repository.UserRepository;
import com.postblog.userservice.utils.UserCreator;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class UserControllerTestIT {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private UserRepository userRepository;

  @LocalServerPort
  private int port;

  @Autowired
  private ModelMapper mapper;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

  @Test
  @DisplayName("GetAllUsers returns empty list when no users exist")
  void getAllUsers_ReturnsEmptyList_WhenNoUsersExist() {
    String url = createURLWithPort("/api/users");
    ResponseEntity<String> response = testRestTemplate.exchange(
        url,
        HttpMethod.GET, null, String.class);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    List<UserEntity> users = null;
    try {
      users = objectMapper.readValue(response.getBody(), new TypeReference<List<UserEntity>>() {
      });
    } catch (Exception e) {
      e.printStackTrace();
    }

    Assertions.assertThat(users)
        .isNotNull()
        .isEmpty();
  }

  @Test
  @DisplayName("GetAllUsers returns users when successful")
  void getAllUsers_ReturnsUsers_WhenSuccessful() {
    Map<Long, UserEntity> savedUsers = new HashMap<>();
    savedUsers.put(1L, userRepository.save(UserCreator.createValidUser()));
    savedUsers.put(2L, userRepository.save(UserCreator.createUserToBeSaved()));
    savedUsers.put(3L, userRepository.save(UserCreator.createValidUpdatedUser()));

    String url = createURLWithPort("/api/users");
    ResponseEntity<String> response = testRestTemplate.exchange(
        url,
        HttpMethod.GET, null, String.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getBody()).isNotNull().isNotEmpty();

    String responseBody = response.getBody();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    try {
      List<UserEntity> users = objectMapper.readValue(responseBody,
          new TypeReference<List<UserEntity>>() {
          });

      for (UserEntity actualUser : users) {
        assertTrue(savedUsers.containsKey(actualUser.getId()),
            "User with ID " + actualUser.getId() + " not found in saved users");
        UserEntity expectedUser = savedUsers.get(actualUser.getId());

        assertEquals(expectedUser.getAge(), actualUser.getAge());
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getCreatedAt().truncatedTo(ChronoUnit.SECONDS),
            actualUser.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("Save returns user when successful")
  void save_ReturnsUser_WhenSuccessful() {
    String url = createURLWithPort("/api/users");
    ResponseEntity<String> userResponseEntity = testRestTemplate.postForEntity(
        url,
        UserCreator.createUserToBeSaved(),
        String.class);

    Assertions.assertThat(userResponseEntity).isNotNull();
    Assertions.assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(userResponseEntity.getBody()).isEqualTo("User created");
  }

  @Test
  @DisplayName("Delete returns user when successful")
  void delete_ReturnsUser_WhenSuccessful() {
    UserEntity user = userRepository.save(UserCreator.createUserToBeSaved());
    String url = createURLWithPort("/api/users/" + user.getId());
    ResponseEntity<Void> resp = testRestTemplate.exchange(
        url,
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        Void.class);

    Assertions.assertThat(resp).isNotNull();
    Assertions.assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("Test for delete throws exception when NOT FOUND")
  void testDeleteUser_ReturnsNOT_FOUND_WhenNotFound() {
    String url = createURLWithPort("/api/users/5");
    ResponseEntity<Void> res = testRestTemplate.exchange(
        url,
        HttpMethod.DELETE,
        null,
        Void.class);

    Assertions.assertThat(res.getStatusCode())
        .as("Check that the status code is NOT_FOUND").isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("GetByIdUser returns user when successful")
  void getByIdUser_ReturnsUser_WhenSuccessful() {
    UserEntity userEntity = userRepository.save(UserCreator.createUserToBeSaved());
    String url = createURLWithPort("/api/users/" + userEntity.getId());
    ResponseEntity<UserEntity> res = testRestTemplate.exchange(
        url,
        HttpMethod.GET, null, UserEntity.class);

    Assertions.assertThat(res.getBody()).isNotNull();
    Assertions.assertThat(res.getBody().getId()).isEqualTo(userEntity.getId());
    Assertions.assertThat(res.getBody().getAge()).isEqualTo(userEntity.getAge());
    Assertions.assertThat(res.getBody().getEmail()).isEqualTo(userEntity.getEmail());
    Assertions.assertThat(res.getBody().getName()).isEqualTo(userEntity.getName());
    Assertions.assertThat(res.getBody().getUsername()).isEqualTo(userEntity.getUsername());
    Assertions.assertThat(res.getBody().getCreatedAt().truncatedTo(ChronoUnit.SECONDS))
        .isEqualTo(userEntity.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
  }

  @Test
  @DisplayName("Test for getById throws exception when NOT FOUND")
  void testGetUserById_ReturnsNOT_FOUND_WhenNotFound() {
    String url = createURLWithPort("/api/users/5");
    ResponseEntity<UserEntity> res = testRestTemplate.getForEntity(
        url,
        UserEntity.class);

    Assertions.assertThat(res.getStatusCode())
        .as("Check that the status code is NOT_FOUND").isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("Update returns user when successful")
  void updateUser_ReturnsUser_WhenSuccessful() {
    UserEntity userEntityToSave = userRepository.save(UserCreator.createValidUser());
    String url = createURLWithPort("/api/users/" + userEntityToSave.getId());
    ResponseEntity<Void> res = testRestTemplate.exchange(
        url,
        HttpMethod.PUT, new HttpEntity<>(userEntityToSave), Void.class);

    UserEntity updatedUser = userRepository.findById(userEntityToSave.getId()).orElse(null);

    Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    Assertions.assertThat(updatedUser).isNotNull();
    Assertions.assertThat(userEntityToSave).isNotNull();
    Assertions.assertThat(userEntityToSave.getId()).isEqualTo(updatedUser.getId());
    Assertions.assertThat(userEntityToSave.getAge()).isEqualTo(updatedUser.getAge());
    Assertions.assertThat(userEntityToSave.getEmail()).isEqualTo(updatedUser.getEmail());
    Assertions.assertThat(userEntityToSave.getName()).isEqualTo(updatedUser.getName());
    Assertions.assertThat(userEntityToSave.getUsername()).isEqualTo(updatedUser.getUsername());
    Assertions.assertThat(userEntityToSave.getCreatedAt().truncatedTo(ChronoUnit.SECONDS))
        .isEqualTo(updatedUser.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
  }

  @Test
  @DisplayName("Check password returns true when correct")
  void checkPassword_ReturnsTrue_WhenCorrectPassword() {
    UserEntity userEntity = userRepository.save(
        UserCreator.createValidUserToEncryptPassword(bCryptPasswordEncoder));

    String url = createURLWithPort("/api/users/" + userEntity.getEmail() + "/checkPassword");
    ResponseEntity<Boolean> responseEntity = testRestTemplate.postForEntity(
        url,
        Map.of("password", "12345"),
        Boolean.class);

    Assertions.assertThat(responseEntity).isNotNull();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEntity.getBody()).isTrue();
  }
}
