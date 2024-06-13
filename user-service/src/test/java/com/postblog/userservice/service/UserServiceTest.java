package com.postblog.userservice.service;

import static com.postblog.userservice.utils.Constants.USER_ALREADY_EXISTS;
import static com.postblog.userservice.utils.UserCreator.createValidUserToLogin;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.postblog.userservice.entities.LoginRequest;
import com.postblog.userservice.entities.Role;
import com.postblog.userservice.entities.UserEntity;
import com.postblog.userservice.entities.UserResponse;
import com.postblog.userservice.exceptions.HttpException;
import com.postblog.userservice.repository.RoleRepository;
import com.postblog.userservice.repository.UserRepository;
import com.postblog.userservice.services.UserService;
import com.postblog.userservice.utils.UserCreator;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepositoryMock;

  @Mock
  private ModelMapper mapper;

  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Mock
  private RoleRepository roleRepositoryMock;


  @Test
  @DisplayName("Test for getAllUsers returns list of users when successful")
  void testGetAllUsers_returnsListOfUsers_WhenSuccessful() {
    List<UserEntity> userList = List.of(UserCreator.createValidUser());
    when(userRepositoryMock.findAll()).thenReturn(userList);
    when(mapper.map(any(UserEntity.class), any())).thenReturn(
        UserCreator.createUserToUserResponse());

    List<UserResponse> users = userService.getAllUsers();

    Assertions.assertThat(users).isNotNull().isNotEmpty();
    UserResponse userResponse = users.get(0);
    assertUserResponseMatchesEntity(userResponse, userList.get(0));
  }

  private void assertUserResponseMatchesEntity(UserResponse userResponse, UserEntity userEntity) {
    Assertions.assertThat(userResponse).isNotNull();
    Assertions.assertThat(userResponse.getName()).isEqualTo(userEntity.getName());
    Assertions.assertThat(userResponse.getEmail()).isEqualTo(userEntity.getEmail());
    Assertions.assertThat(userResponse.getId()).isEqualTo(userEntity.getId());
    Assertions.assertThat(userResponse.getAge()).isEqualTo(userEntity.getAge());
    Assertions.assertThat(userResponse.getUsername()).isEqualTo(userEntity.getUsername());
    Assertions.assertThat(userResponse.getCreatedAt()).isEqualTo(userEntity.getCreatedAt());
  }

  @Test
  @DisplayName("Test for getAllUsers throws exception when an error occurs")
  void testGetAllUsers_Internal_Server_Error() {
    when(userRepositoryMock.findAll()).thenThrow(RuntimeException.class);

    HttpException exception = assertThrows(HttpException.class, () -> {
      userService.getAllUsers();
    });

    Assertions.assertThat(exception.getStatusCode()).isEqualTo(500);
    Assertions.assertThat(exception.getMessage()).isEqualTo("Failed to get users");
  }

  @Test
  @DisplayName("Test for getUserById returns user when successful")
  void testGetUserById_returnsUser_WhenSuccessful() {
    UserEntity userEntity = UserCreator.createValidUser();
    UserResponse expectedUserResponse = UserCreator.createUserToUserResponse();

    when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(userEntity));
    when(mapper.map(Mockito.any(), eq(UserResponse.class)))
        .thenReturn(expectedUserResponse);

    UserResponse userResponse = userService.getUserById(1L);

    Assertions.assertThat(userResponse).isNotNull();
    Assertions.assertThat(userResponse.getId()).isEqualTo(userEntity.getId());
    Assertions.assertThat(userResponse.getName()).isEqualTo(userEntity.getName());
    Assertions.assertThat(userResponse.getAge()).isEqualTo(userEntity.getAge());
    Assertions.assertThat(userResponse.getEmail()).isEqualTo(userEntity.getEmail());
    Assertions.assertThat(userResponse.getUsername()).isEqualTo(userEntity.getUsername());
    Assertions.assertThat(userResponse.getCreatedAt()).isEqualTo(userEntity.getCreatedAt());
  }

  @Test
  @DisplayName("Test for getUserById throws exception when user NOT FOUND")
  void testGetUserById_ReturnsNOT_FOUND_WhenNotFound() {
    when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());

    HttpException exception = assertThrows(HttpException.class, () -> {
      userService.getUserById(1L);
    });

    Assertions.assertThat(exception.getStatusCode()).isEqualTo(404);
    Assertions.assertThat(exception.getMessage()).isEqualTo("User not found");
  }

  @Test
  @DisplayName("Test for getUserById throws exception when user not found")
  void testGetUserById_ThrowsException_WhenUserNotFound() {
    when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(HttpException.class, () -> userService.getUserById(1L));
  }


  @Test
  @DisplayName("Test for save user throws INTERNAL_SERVER_ERROR")
  void testUpdateUserById_Internal_Server_Error_WhenSuccessful() {
    when(userRepositoryMock.existsById(anyLong())).thenReturn(false);

    UserEntity userToSave = UserCreator.createValidUser();

    when(userRepositoryMock.save(any(UserEntity.class))).thenThrow(RuntimeException.class);

    HttpException exception = assertThrows(HttpException.class, () -> {
      userService.createUser(userToSave);
    });

    Assertions.assertThat(exception.getMessage()).isEqualTo("Failed to create user");
    Assertions.assertThat(exception.getStatusCode()).isEqualTo(500);
  }

  @Test
  @DisplayName("Test for save user when successful")
  void testSaveUser_ReturnsOkWhenSuccessful() {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    when(userRepositoryMock.save(any(UserEntity.class))).thenReturn(UserCreator.createValidUser());

    Role validRole = new Role();
    validRole.setName("USER");

    when(roleRepositoryMock.findByName("USER")).thenReturn(Optional.of(validRole));

    UserEntity userToSave = UserCreator.createValidUserToEncryptPassword(bCryptPasswordEncoder);

    userService.createUser(userToSave);
    
    verify(userRepositoryMock, times(1)).save(userToSave);
  }


  @Test
  @DisplayName("Test for save user when CONFLICT")
  void testSaveUser_Conflict_WhenConflict() {
    UserEntity existingUser = UserCreator.createValidUser();
    when(userRepositoryMock.findByEmail(existingUser.getEmail())).thenReturn(
        Optional.of(existingUser));

    HttpException exception = assertThrows(HttpException.class, () -> {
      userService.createUser(existingUser);
    });

    Assertions.assertThat(exception.getMessage()).isEqualTo(USER_ALREADY_EXISTS);
    Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
  }

  @Test
  @DisplayName("Test updates user when successful")
  void testUpdatesUser_ReturnsOk_WhenSuccessful() {
    when(userRepositoryMock.save(any(UserEntity.class)))
        .thenReturn(UserCreator.createUserToBeSaved());
    when(userRepositoryMock.findById(anyLong()))
        .thenReturn(Optional.of(UserCreator.createUserToBeSaved()));

    Assertions.assertThatCode(
            () -> userService.updateUserById(UserCreator.createValidUpdatedUser().getId(),
                UserCreator.createUserToBeSaved()))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Test for updateUserById throws exception when Failed to update User")
  void testUpdateUser_ReturnsFailed_WhenFailed() {
    UserEntity userEntity = UserCreator.createUserToBeSaved();

    when(userRepositoryMock.findById(anyLong())).thenReturn(
        Optional.ofNullable(userEntity));
    when(userRepositoryMock.save(any(UserEntity.class))).thenThrow(RuntimeException.class);

    HttpException exception = assertThrows(HttpException.class, () -> {
      assert userEntity != null;
      userService.updateUserById(userEntity.getId(), UserCreator.createUserToBeSaved());
    });

    Assertions.assertThat(exception.getStatusCode()).isEqualTo(500);
    Assertions.assertThat(exception.getMessage()).isEqualTo("Failed to update User");
  }

  @Test
  @DisplayName("Test for updateUserById throws exception when NOT FOUND")
  void testUpdateUser_Not_Found_WhenNotFound() {
    UserEntity userEntity = UserCreator.createUserToBeSaved();

    when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());

    HttpException exception = assertThrows(HttpException.class, () -> {
      userService.updateUserById(userEntity.getId(), UserCreator.createUserToBeSaved());
    });

    Assertions.assertThat(exception.getStatusCode()).isEqualTo(404);
    Assertions.assertThat(exception.getMessage()).isEqualTo("User not found");
  }

  @Test
  @DisplayName("delete removes user successful")
  void testDeleteUser_ReturnsOK_WhenSuccessful() {
    when(userRepositoryMock.existsById(1L)).thenReturn(true);
    Mockito.doNothing().when(userRepositoryMock).delete(any(UserEntity.class));

    Assertions.assertThatCode(() -> userService.deleteUserById(1L))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Test for deleteUserById throws exception when NOT FOUND")
  void testDeleteUser_ReturnsNOT_FOUND_WhenNotFound() {

    when(userRepositoryMock.existsById(anyLong())).thenReturn(false);

    HttpException exception = assertThrows(HttpException.class, () -> {
      userService.deleteUserById(1L);
    });
    Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    Assertions.assertThat(exception.getMessage()).isEqualTo("User not found");
  }

  @Test
  @DisplayName("Check password returns true when password matches")
  void testCheckPassword_EmailFound_PasswordMatches_WhenSuccessful() throws Exception {
    UserRepository userRepositoryMock = mock(UserRepository.class);
    BCryptPasswordEncoder bCryptPasswordEncoderMock = mock(BCryptPasswordEncoder.class);
    when(bCryptPasswordEncoderMock.matches(any(CharSequence.class), anyString())).thenReturn(true);

    UserService userService = new UserService();
    Field userRepositoryField = UserService.class.getDeclaredField("userRepository");
    userRepositoryField.setAccessible(true);
    userRepositoryField.set(userService, userRepositoryMock);
    Field passwordEncoderField = UserService.class.getDeclaredField("bCryptPasswordEncoder");
    passwordEncoderField.setAccessible(true);
    passwordEncoderField.set(userService, bCryptPasswordEncoderMock);

    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("test@junit.com");
    userEntity.setPassword(
        "$2a$10$lyPfGw4/YJJoG4mgtiPP.u0d8/8k3LVZKnCGyqZUDS70vs5fQ1Zu6");
    when(userRepositoryMock.findByEmail(anyString())).thenReturn(Optional.of(userEntity));

    boolean result = userService.checkPassword("test@example.com", "password");

    assertTrue(result, "Expected password -> return true");
  }

  @Test
  @DisplayName("Check password throws exception when email not found")
  void testCheckPassword_EmailNotFound_WhenSuccessful() {
    when(userRepositoryMock.findByEmail(anyString())).thenReturn(Optional.empty());

    HttpException exception = assertThrows(HttpException.class, () -> {
      userService.checkPassword("nonexistent@example.com", "12345");
    });

    Assertions.assertThat(exception.getStatusCode()).isEqualTo(404);
    Assertions.assertThat(exception.getMessage()).isEqualTo("Email not found");
  }

  @Test
  @DisplayName("Test loginUser when successful")
  void testLoginUser_Successful() {
    UserEntity user = createValidUserToLogin();

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setPassword("password");

    when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(user));
    when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);

    userService.loginUser(1L, loginRequest);

    verify(userRepositoryMock, times(1)).findById(1L);
    verify(bCryptPasswordEncoder, times(1)).matches(any(), any());

    assertTrue(user.isRegistered());
  }

  @Test
  @DisplayName("Test loginUser throws exception when password is null")
  void testLoginUser_PasswordNull() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setPassword(null);

    HttpException exception = assertThrows(HttpException.class, () -> {
      userService.loginUser(1L, loginRequest);
    });

    Assertions.assertThat(HttpStatus.UNPROCESSABLE_ENTITY.value())
        .isEqualTo(exception.getStatusCode());
    Assertions.assertThat(exception.getMessage()).isEqualTo("Password cannot be null");
  }

  @Test
  @DisplayName("Test isUserLoggedIn when user is logged in")
  void testIsUserLoggedIn_UserLoggedIn() {
    UserResponse user = Mockito.mock(UserResponse.class);

    given(user.isRegistered()).willReturn(true);

    UserService userService = Mockito.mock(UserService.class);
    given(userService.getUserById(1L)).willReturn(user);

    userService.isUserLoggedIn(1L);
    Assertions.assertThat(user.isRegistered()).isTrue();
  }

  @Test
  @DisplayName("Test isUserLoggedIn throws exception when user is not logged in")
  void testIsUserLoggedIn_UserNotLoggedIn() {
    UserEntity user = UserCreator.createValidUserToLogin();

    when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

    assertThrows(HttpException.class, () -> userService.isUserLoggedIn(user.getId()),
        "User is not logged in");
  }

  @Test
  @DisplayName("Test logoutUser when successful")
  void testLogoutUser_Successful() {
    UserEntity user = UserCreator.createValidUserToLogin();
    user.setRegistered(true);
    when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(user));

    assertDoesNotThrow(() -> userService.logoutUser(1L));
    assertFalse(user.isRegistered());
  }

  @Test
  @DisplayName("Test logoutUser throws exception when user not found")
  void testLogoutUser_UserNotFound() {
    when(userRepositoryMock.findById(1L)).thenReturn(Optional.empty());

    HttpException exception = assertThrows(HttpException.class, () -> {
      userService.logoutUser(1L);
    });

    Assertions.assertThat(HttpStatus.NOT_FOUND.value()).isEqualTo(exception.getStatusCode());
    Assertions.assertThat(exception.getMessage()).isEqualTo("User not found");
  }
}
