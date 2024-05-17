package com.postblog.userservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
    SecurityContextHolder.getContext().setAuthentication(null);
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    return ResponseEntity.noContent().build();
  }
}


