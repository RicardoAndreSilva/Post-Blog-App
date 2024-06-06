package com.postblog.userservice.controllers;

import com.postblog.userservice.entities.LoginRequest;
import com.postblog.userservice.entities.UserEntity;
import com.postblog.userservice.entities.UserResponse;
import com.postblog.userservice.exceptions.HttpException;
import com.postblog.userservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import java.util.Map;
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


@Tag(name = "User Operations", description = "Endpoints for user management")
@RequestMapping("/api")
@RestController
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/users/{userId}/login")
  @Operation(summary = "Login a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User logged in"),
      @ApiResponse(responseCode = "404", description = "User logged in")
  })
  public ResponseEntity<String> loginUser(@PathVariable long userId,
      @RequestBody LoginRequest request) {
    try {
      userService.loginUser(userId, request);
      return ResponseEntity.ok("User logged in");
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).body(e.getMessage());
    }
  }

  @PostMapping("/users/{userId}/logout")
  @Operation(summary = "Logout a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User logged out"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<String> logoutUser(@PathVariable Long userId) {
    try {
      userService.logoutUser(userId);
      return ResponseEntity.ok("User logged out");
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).body(e.getMessage());
    }
  }


  @GetMapping("/users/{userId}/login")
  @Operation(summary = "checks if an user is logged in")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "user is logged in"),
      @ApiResponse(responseCode = "404", description = "user was not found"),
      @ApiResponse(responseCode = "422", description = "user is not logged in"),
  })
  public ResponseEntity<String> isUserLoggedIn(@PathVariable Long userId) {
    try {
      userService.isUserLoggedIn(userId);
      return ResponseEntity.ok("User is logged in");
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).body(e.getMessage());
    }
  }

  @GetMapping("/users/{userId}")
  @Operation(summary = "Get user details by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User found"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<UserResponse> getUserDetails(@PathVariable("userId") Long userId) {
    try {
      UserResponse userResponse = userService.getUserById(userId);
      return ResponseEntity.status(HttpStatus.OK).body(userResponse);
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
  @PermitAll
  public ResponseEntity<String> saveUser(@RequestBody UserEntity user) {
    try {
      userService.createUser(user);
      return ResponseEntity.status(HttpStatus.CREATED).body("User created");
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
  public ResponseEntity<String> updateUser(@PathVariable("userId") Long userId,
      @RequestBody UserEntity updatedUser) {
    try {
      userService.updateUserById(userId, updatedUser);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User updated");
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @GetMapping("/users")
  @Operation(summary = "Get all users")
  @ApiResponse(responseCode = "200", description = "List of users retrieved")
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    try {
      List<UserResponse> userResponses = userService.getAllUsers();
      return ResponseEntity.ok(userResponses);
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
  public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
    try {
      userService.deleteUserById(userId);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @PostMapping("/users/{email}/checkPassword")
  @Operation(summary = "Check if provided password matches the user's password")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password matches"),
      @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public ResponseEntity<Boolean> checkPassword(@PathVariable String email,
      @RequestBody Map<String, Object> body) {
    try {
      String providedPassword = (String) body.get("password");
      boolean isPasswordCorrect = userService.checkPassword(email, providedPassword);

      if (isPasswordCorrect) {
        return ResponseEntity.ok(true);
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
      }
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }
}