package com.postblog.dataintegration.service.controller;

import com.postblog.dataintegration.service.services.UserServiceProxy;
import com.postblog.userservice.entities.UserEntity;
import com.postblog.userservice.entities.UserResponse;
import com.postblog.userservice.exceptions.HttpException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Proxy Operations", description = "Endpoints for user proxy management")
@RequestMapping("/api")
@RestController
public class UserControllerProxy {

  @Autowired
  private UserServiceProxy userServiceProxy;

  @GetMapping("/users/{userId}")
  @Operation(summary = "Get user details by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User found"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
    try {
      return userServiceProxy.getUserById(userId);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @PostMapping("/users")
  @Operation(summary = "Create a new user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created"),
      @ApiResponse(responseCode = "400", description = "Invalid request")
  })
  public ResponseEntity<UserResponse> createUser(@RequestBody UserEntity user) {
    try {
      return userServiceProxy.createUser(user);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @PutMapping("/users/{userId}")
  @Operation(summary = "Update an existing user by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User updated"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<Void> updateUser(@PathVariable Long userId, @RequestBody UserEntity user) {
    try {
      return userServiceProxy.updateUser(userId, user);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @DeleteMapping("/users/{userId}")
  @Operation(summary = "Delete a user by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User deleted"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    try {
      return userServiceProxy.deleteUser(userId);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }
}
