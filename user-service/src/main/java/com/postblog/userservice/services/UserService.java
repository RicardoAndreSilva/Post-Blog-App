package com.postblog.userservice.services;


import static com.postblog.userservice.utils.Constants.EMAIL_NOT_FOUND;
import static com.postblog.userservice.utils.Constants.FAILED_TO_CREATE_USER;
import static com.postblog.userservice.utils.Constants.FAILED_TO_GET_USERS;
import static com.postblog.userservice.utils.Constants.FAILED_TO_UPDATE_USER;
import static com.postblog.userservice.utils.Constants.INTERNAL_SERVER_ERROR;
import static com.postblog.userservice.utils.Constants.NOT_FOUND;
import static com.postblog.userservice.utils.Constants.USERNAME_NOT_FOUND;
import static com.postblog.userservice.utils.Constants.USER_ALREADY_EXISTS;
import static com.postblog.userservice.utils.Constants.USER_NOT_FOUND;

import com.postblog.userservice.entities.UserEntity;
import com.postblog.userservice.entities.UserResponse;
import com.postblog.userservice.exceptions.HttpException;
import com.postblog.userservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * Service class responsible for managing users.
 */
@Service
public class UserService {

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ModelMapper mapper;

  /**
   * Retrieves user details by ID.
   *
   * @param id The ID of the user.
   * @return User details.
   * @throws HttpException if user is not found.
   */
  public UserResponse getUserById(Long id) {
    Optional<UserEntity> user = userRepository.findById(id);

    return user.map(u -> mapper.map(u, UserResponse.class))
        .orElseThrow(() -> new HttpException(USER_NOT_FOUND, NOT_FOUND));
  }

  /**
   * Creates a new user.
   *
   * @param newUser The new user data.
   * @throws HttpException if user already exists or creation fails.
   */

  public void createUser(UserEntity newUser) {
    Optional<UserEntity> existingUser = userRepository.findByEmail(newUser.getEmail());
    if (existingUser.isPresent()) {
      throw new HttpException(USER_ALREADY_EXISTS, HttpStatus.CONFLICT.value());
    }
    try {
      BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
      String encryptedPassword = bCryptPasswordEncoder.encode(newUser.getPassword());
      newUser.setPassword(encryptedPassword);

      userRepository.save(newUser);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_CREATE_USER, INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * Updates an existing user by ID.
   *
   * @param userId       The ID of the user to be updated.
   * @param userToUpdate The updated user data.
   * @return Updated user details.
   * @throws HttpException if user is not found or update fails.
   */
  public UserResponse updateUserById(Long userId, UserEntity userToUpdate) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new HttpException(USER_NOT_FOUND, NOT_FOUND));

    user.setName(userToUpdate.getName());
    user.setAge(userToUpdate.getAge());
    user.setEmail(userToUpdate.getEmail());

    try {
      userRepository.save(user);
      return mapper.map(user, UserResponse.class);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_UPDATE_USER, INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get all users.
   *
   * @return A list of all users.
   * @throws HttpException If unable to retrieve users.
   */
  public List<UserResponse> getAllUsers() {
    try {
      List<UserEntity> usersList = userRepository.findAll();
      return usersList.stream()
          .map(user -> mapper.map(user, UserResponse.class))
          .toList();
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_GET_USERS, INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * Deletes a user by ID.
   *
   * @param userId The ID of the user to be deleted.
   * @throws HttpException if user is not found.
   */

  public void deleteUserById(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new HttpException(USER_NOT_FOUND, NOT_FOUND);
    }
    userRepository.deleteById(userId);
  }

  /**
   * Checks if the provided password matches the user's password.
   *
   * @param email            The email of the user.
   * @param providedPassword The password provided for verification.
   * @return True if the password matches, otherwise false.
   * @throws HttpException if email is not found.
   */

  public boolean checkPassword(String email, String providedPassword) {
    Optional<UserEntity> user = userRepository.findByEmail(email);
    if (user != null && user.isPresent()) {
      UserEntity entity = user.get();
      return bCryptPasswordEncoder.matches(providedPassword, entity.getPassword());
    } else {
      throw new HttpException(EMAIL_NOT_FOUND, NOT_FOUND);
    }
  }

  /**
   * Find a user by username.
   *
   * @param username Username to search for.
   * @return The found user (if exists).
   * @throws HttpException If the user is not found.
   */
  public UserResponse getUserByUsername(String username) {
    Optional<UserEntity> user = Optional.ofNullable(userRepository.findByUsername(username));
    return user.map(u -> mapper.map(u, UserResponse.class))
        .orElseThrow(() -> new HttpException(USERNAME_NOT_FOUND, HttpStatus.NOT_FOUND.value()));
  }

  /**
   * Fetches a user by username and returns their roles.
   *
   * @param username The username of the user to be fetched.
   * @return The found user along with their roles.
   * @throws HttpException If the user is not found.
   */
  public UserEntity getUserByUsernameWithRoles(String username) {
    UserEntity user = userRepository.findByUsernameFetchRoles(username);
    if (user == null) {
      throw new HttpException(USERNAME_NOT_FOUND, HttpStatus.NOT_FOUND.value());
    }
    return user;
  }
}
