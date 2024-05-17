package com.postblog.dataintegration.service.services;

import static com.postblog.dataintegration.service.utils.Constants.FAILED_TO_CREATE_USER;
import static com.postblog.dataintegration.service.utils.Constants.FAILED_TO_GET_USERS;
import static com.postblog.dataintegration.service.utils.Constants.FAILED_TO_UPDATE_USER;
import static com.postblog.dataintegration.service.utils.Constants.USER_NOT_FOUND;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postblog.dataintegration.service.exceptions.HttpException;
import com.postblog.userservice.entities.UserEntity;
import com.postblog.userservice.entities.UserResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class for proxying user-related operations to another service.
 */
@Service
public class UserServiceProxy {

  @Value("${user.service.url}")
  private String userServiceUrl;

  @Value("${user.service.url.with_id}")
  private String userServiceUrlWithId;

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Retrieves user details by ID.
   *
   * @param userId The ID of the user to retrieve.
   * @return ResponseEntity containing the user details if successful.
   * @throws HttpException if the request to retrieve user details fails.
   */
  public ResponseEntity<UserResponse> getUserById(Long userId) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(userServiceUrlWithId + "/" + userId);
      CloseableHttpResponse response = httpClient.execute(request);
      String responseBody = EntityUtils.toString(response.getEntity());
      UserResponse userResponse = objectMapper.readValue(responseBody, UserResponse.class);
      return new ResponseEntity<>(userResponse, HttpStatus.OK);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_GET_USERS,HttpStatus.NOT_FOUND.value() );
    }
  }

  /**
   * Creates a new user.
   *
   * @param user The user entity to create.
   * @return ResponseEntity containing the created user details if successful.
   * @throws HttpException if the request to create the user fails.
   */
  public ResponseEntity<UserResponse> createUser(UserEntity user) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPost request = new HttpPost(userServiceUrl);
      StringEntity entity = new StringEntity(objectMapper.writeValueAsString(user));
      request.setEntity(entity);
      request.setHeader("Accept", "application/json");
      request.setHeader("Content-type", "application/json");
      CloseableHttpResponse response = httpClient.execute(request);
      String responseBody = EntityUtils.toString(response.getEntity());
      UserResponse userResponse = objectMapper.readValue(responseBody, UserResponse.class);
      return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_CREATE_USER, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /**
   * Updates an existing user by ID.
   *
   * @param userId The ID of the user to update.
   * @param user   The updated user entity.
   * @return ResponseEntity with no content if the update is successful.
   * @throws HttpException if the request to update the user fails.
   */
  public ResponseEntity<Void> updateUser(Long userId, UserEntity user) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPut request = new HttpPut(userServiceUrlWithId + "/" + userId);
      StringEntity entity = new StringEntity(objectMapper.writeValueAsString(user));
      request.setEntity(entity);
      request.setHeader("Accept", "application/json");
      request.setHeader("Content-type", "application/json");
      httpClient.execute(request);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_UPDATE_USER, HttpStatus.NOT_FOUND.value());
    }
  }

  /**
   * Deletes a user by ID.
   *
   * @param userId The ID of the user to delete.
   * @return ResponseEntity with no content if deletion is successful.
   * @throws HttpException if the request to delete the user fails.
   */
  public ResponseEntity<Void> deleteUser(Long userId) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpDelete request = new HttpDelete(userServiceUrlWithId + "/" + userId);
      httpClient.execute(request);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      throw new HttpException(USER_NOT_FOUND, HttpStatus.NOT_FOUND.value());
    }
  }
}
