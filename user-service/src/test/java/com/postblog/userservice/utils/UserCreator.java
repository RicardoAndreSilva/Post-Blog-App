package com.postblog.userservice.utils;

import com.postblog.userservice.entities.UserEntity;
import com.postblog.userservice.entities.UserResponse;
import java.time.LocalDate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


public class UserCreator {


  public UserCreator(PasswordEncoder passwordEncoder) {
  }

  public static UserEntity createUserToBeSaved() {
    return UserEntity.builder()
        .age(25)
        .id(1L)
        .email("test55@hotmail.com")
        .name("jUnit")
        .username("test")
        .password("12345")
        .createdAt(LocalDate.now().atStartOfDay())
        .build();

  }

  public static UserResponse createUserToUserResponse() {
    return UserResponse.builder()
        .age(25)
        .email("teste55@hotmail.com")
        .name("jUnit")
        .username("test")
        .password("12345")
        .createdAt(LocalDate.now().atStartOfDay())
        .build();

  }

  public static UserEntity createValidUser() {
    return UserEntity.builder()
        .age(25)
        .email("teste55@hotmail.com")
        .name("jUnit")
        .username("test")
        .password("12345")
        .createdAt(LocalDate.now().atStartOfDay())
        .build();
  }

  public static UserEntity createValidUpdatedUser() {
    return UserEntity.builder()
        .id(1L)
        .age(25)
        .email("teste55@hotmail.com")
        .name("jUnit")
        .username("test")
        .password("12345")
        .createdAt(LocalDate.now().atStartOfDay())
        .build();
  }

  public static UserEntity createValidUserToEncryptPassword(
      BCryptPasswordEncoder bCryptPasswordEncoder) {
    return UserEntity.builder()
        .age(25)
        .id(1L)
        .email("teste55@hotmail.com")
        .name("john")
        .username("test")
        .password(bCryptPasswordEncoder.encode("12345"))
        .createdAt(LocalDate.now().atStartOfDay())
        .build();
  }
}
