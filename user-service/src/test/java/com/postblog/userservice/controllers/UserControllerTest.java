package com.postblog.userservice.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.postblog.userservice.entities.LoginRequest;
import com.postblog.userservice.entities.UserEntity;
import com.postblog.userservice.entities.UserResponse;
import com.postblog.userservice.exceptions.HttpException;
import com.postblog.userservice.services.UserService;
import com.postblog.userservice.utils.UserCreator;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class UserControllerTest {

  @InjectMocks
  private UserController userController;

  @Mock
  private UserService userServiceMock;

  @Mock
  private ModelMapper mapper;

  @BeforeEach
  void setUp_userServiceMock_for() {
    ModelMapper mapper2 = new ModelMapper();
    UserResponse userResponse = mapper2.map(UserCreator.createValidUser(), UserResponse.class);

    when(userServiceMock.getAllUsers()).thenReturn(List.of(userResponse));
    BDDMockito.doNothing().when(userServiceMock).deleteUserById(anyLong());
    BDDMockito.doNothing().when(userServiceMock).createUser(any());
    when(userServiceMock.updateUserById(any(Long.class), any())).thenReturn(userResponse);
    when(userServiceMock.getUserById(anyLong())).thenReturn(userResponse);
    //---Authentication---
    BDDMockito.doNothing().when(userServiceMock).loginUser(anyLong(), any(LoginRequest.class));
    BDDMockito.doNothing().when(userServiceMock).logoutUser(anyLong());
    BDDMockito.doNothing().when(userServiceMock).isUserLoggedIn(anyLong());
  }


  @Test
  @DisplayName("Test for getAllUsers returns list of users when successful")
  void testGetAllUsers_returnsListOfUsers_WhenSuccessful() {
    ResponseEntity<List<UserResponse>> responseEntity = userController.getAllUsers();

    Assertions.assertThat(responseEntity).isNotNull();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<UserResponse> userResponses = responseEntity.getBody();
    Assertions.assertThat(userResponses).isNotNull().isNotEmpty();

    UserResponse user = userResponses.get(0);
    UserEntity mockUser = UserCreator.createValidUser();

    Assertions.assertThat(user.getName())
        .isEqualTo(mockUser.getName());
    Assertions.assertThat(user.getEmail())
        .isEqualTo(mockUser.getEmail());
    Assertions.assertThat(user.getId())
        .isEqualTo(mockUser.getId());
    Assertions.assertThat(user.getAge())
        .isEqualTo(mockUser.getAge());
    Assertions.assertThat(user.getUsername())
        .isEqualTo(mockUser.getUsername());
    Assertions.assertThat(user.getCreatedAt())
        .isEqualTo(mockUser.getCreatedAt());
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during getAllUsers ")
  void testGetAllUsersUsers_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    UserEntity userEntity = UserCreator.createValidUpdatedUser();

    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(userServiceMock)
        .getAllUsers();

    ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  @DisplayName("Test for getUserById returns user when successful")
  void testGetUserById_returnsUser_WhenSuccessful() {
    UserEntity expectedUser = UserCreator.createValidUser();

    ResponseEntity<UserResponse> responseEntity = userController.getUserDetails(1L);

    Assertions.assertThat(responseEntity).isNotNull();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponse userResponse = responseEntity.getBody();

    Assertions.assertThat(userResponse).isNotNull();
    Assertions.assertThat(userResponse.getId()).isEqualTo(expectedUser.getId());
    Assertions.assertThat(userResponse.getName()).isEqualTo(expectedUser.getName());
    Assertions.assertThat(userResponse.getEmail()).isEqualTo(expectedUser.getEmail());
    Assertions.assertThat(userResponse.getAge()).isEqualTo(expectedUser.getAge());
    Assertions.assertThat(userResponse.getUsername()).isEqualTo(expectedUser.getUsername());
    Assertions.assertThat(userResponse.getUsername()).isEqualTo(expectedUser.getUsername());
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during user getById")
  void testGetUserById_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {

    UserEntity userEntity = UserCreator.createUserToBeSaved();

    when(userServiceMock.getUserById(anyLong())).thenThrow(
        new HttpException("User not found", HttpStatus.NOT_FOUND.value()));

    ResponseEntity<UserResponse> response = userController.getUserDetails(userEntity.getId());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("Test for save user when successful")
  void testSaveUser_ReturnsOk_WhenSuccessful() {
    ResponseEntity<String> userResponseEntity = userController.saveUser(
        UserCreator.createValidUser());

    Assertions.assertThat(userResponseEntity).isNotNull();
    Assertions.assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(userResponseEntity.getBody()).isEqualTo("User created");

    Mockito.verify(userServiceMock, Mockito.times(1)).createUser(any());
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during user save")
  void testSaveUser_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    UserEntity userEntity = UserCreator.createValidUser();

    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(userServiceMock)
        .createUser(any());

    ResponseEntity<String> response = userController.saveUser(userEntity);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }


  @Test
  @DisplayName("Test for update user when successful")
  void testUpdateUser_ReturnsOk_WhenSuccessful() {
    Assertions.assertThatCode(() -> {
      userController.updateUser(1L, UserCreator.createValidUpdatedUser());
    }).doesNotThrowAnyException();

    ResponseEntity<String> entity = userController.updateUser(2L,
        UserCreator.createValidUpdatedUser());

    Assertions.assertThat(entity).isNotNull();
    Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during user update")
  void testUpdateUser_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    UserEntity userEntity = UserCreator.createValidUpdatedUser();

    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(userServiceMock)
        .updateUserById(userEntity.getId(), userEntity);

    ResponseEntity<String> response = userController.updateUser(userEntity.getId(), userEntity);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }


  @Test
  @DisplayName("delete removes user successful")
  void testDeleteUser_ReturnsOK_WhenSuccessful() {
    Assertions.assertThatCode(() -> userController.deleteUser(1L))
        .doesNotThrowAnyException();

    ResponseEntity<Void> entity = userController.deleteUser(1L);

    Assertions.assertThat(entity).isNotNull();
    Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during user delete")
  void testDeleteUser_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    UserEntity userEntity = UserCreator.createValidUpdatedUser();

    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(userServiceMock)
        .deleteUserById(userEntity.getId());

    ResponseEntity<Void> response = userController.deleteUser(userEntity.getId());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }


  @Test
  @DisplayName("Test for checking password when correct")
  void testCheckPassword_ReturnsTrue_WhenCorrectPassword() {
    UserEntity userEntity = UserCreator.createValidUser();

    when(userServiceMock.checkPassword(userEntity.getEmail(), "correctPassword")).thenReturn(true);

    ResponseEntity<Boolean> responseEntity = userController.checkPassword(userEntity.getEmail(),
        Map.of("password", "correctPassword"));

    Assertions.assertThat(responseEntity).isNotNull();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEntity.getBody()).isTrue();
  }

  @Test
  @DisplayName("Test for checking password when incorrect")
  void testCheckPassword_ReturnsFalse_WhenIncorrectPassword() {
    UserEntity userEntity = UserCreator.createValidUser();

    when(userServiceMock.checkPassword(userEntity.getEmail(), "incorrectPassword")).thenReturn(
        false);

    ResponseEntity<Boolean> responseEntity = userController.checkPassword(userEntity.getEmail(),
        Map.of("password", "incorrectPassword"));

    Assertions.assertThat(responseEntity).isNotNull();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    Assertions.assertThat(responseEntity.getBody()).isFalse();
  }

  @Test
  @DisplayName("Test for loginUser returns 'User logged in' when successful")
  void testLoginUser_ReturnsOk_WhenSuccessful() {
    LoginRequest request = new LoginRequest("correctPassword");

    ResponseEntity<String> responseEntity = userController.loginUser(1L, request);

    Assertions.assertThat(responseEntity).isNotNull();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEntity.getBody()).isEqualTo("User logged in");
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during loginUser")
  void testLoginUser_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    LoginRequest request = new LoginRequest("incorrectPassword");

    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(userServiceMock)
        .loginUser(anyLong(), any(LoginRequest.class));

    ResponseEntity<String> response = userController.loginUser(1L, request);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  @DisplayName("Test for logoutUser returns 'User logged out' when successful")
  void testLogoutUser_ReturnsOk_WhenSuccessful() {
    ResponseEntity<String> responseEntity = userController.logoutUser(1L);

    Assertions.assertThat(responseEntity).isNotNull();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEntity.getBody()).isEqualTo("User logged out");
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during logoutUser")
  void testLogoutUser_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(userServiceMock)
        .logoutUser(anyLong());

    ResponseEntity<String> response = userController.logoutUser(1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  @DisplayName("Test for isUserLoggedIn returns 'User is logged in' when successful")
  void testIsUserLoggedIn_ReturnsOk_WhenSuccessful() {
    ResponseEntity<String> responseEntity = userController.isUserLoggedIn(1L);

    Assertions.assertThat(responseEntity).isNotNull();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEntity.getBody()).isEqualTo("User is logged in");
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during isUserLoggedIn")
  void testIsUserLoggedIn_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(userServiceMock)
        .isUserLoggedIn(anyLong());

    ResponseEntity<String> response = userController.isUserLoggedIn(1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}



