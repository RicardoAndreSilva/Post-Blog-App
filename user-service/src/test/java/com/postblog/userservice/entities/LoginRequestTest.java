package com.postblog.userservice.entities;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class LoginRequestTest {

  @Test
  @DisplayName("Test LoginRequest constructor and getters")
  void testLoginRequestConstructorAndGetters_ReturnsOk_WhenSuccessful() {
    String password = "password";

    LoginRequest loginRequest = new LoginRequest(password);
    Assertions.assertThat(loginRequest.getPassword()).isEqualTo(password);
  }
}