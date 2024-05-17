package com.postblog.dataintegration.service.services;

import static com.postblog.dataintegration.service.utils.Constants.FAILED_TO_CREATE_POST;
import static com.postblog.dataintegration.service.utils.Constants.FAILED_TO_GET_POSTS;
import static com.postblog.dataintegration.service.utils.Constants.FAILED_TO_UPDATE_POST;
import static com.postblog.dataintegration.service.utils.Constants.POST_NOT_FOUND;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postblog.dataintegration.service.exceptions.HttpException;
import com.postblog.postservice.entities.PostEntity;
import com.postblog.postservice.entities.PostResponse;
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

@Service
public class PostServiceProxy {

  @Value("${post.service.url}")
  private String postServiceUrl;

  @Value("${post.service.url.with_id}")
  private String postServiceUrlWithId;

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Retrieves post details by ID.
   *
   * @param postId The ID of the post to retrieve.
   * @return ResponseEntity containing the post details if successful.
   * @throws HttpException if the request to retrieve post details fails.
   */
  public ResponseEntity<PostResponse> getPostById(Long postId) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(postServiceUrlWithId + "/" + postId);
      CloseableHttpResponse response = httpClient.execute(request);
      String responseBody = EntityUtils.toString(response.getEntity());
      PostResponse postResponse = objectMapper.readValue(responseBody, PostResponse.class);
      return new ResponseEntity<>(postResponse, HttpStatus.OK);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_GET_POSTS, HttpStatus.NOT_FOUND.value());
    }
  }

  /**
   * Creates a new post.
   *
   * @param post The post entity to create.
   * @return ResponseEntity containing the created post details if successful.
   * @throws HttpException if the request to create the post fails.
   */
  public ResponseEntity<PostResponse> createPost(PostEntity post) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPost request = new HttpPost(postServiceUrl);
      StringEntity entity = new StringEntity(objectMapper.writeValueAsString(post));
      request.setEntity(entity);
      request.setHeader("Accept", "application/json");
      request.setHeader("Content-type", "application/json");
      CloseableHttpResponse response = httpClient.execute(request);
      String responseBody = EntityUtils.toString(response.getEntity());
      PostResponse postResponse = objectMapper.readValue(responseBody, PostResponse.class);
      return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_CREATE_POST, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /**
   * Updates an existing post by ID.
   *
   * @param postId The ID of the post to update.
   * @param post   The updated post entity.
   * @return ResponseEntity with no content if the update is successful.
   * @throws HttpException if the request to update the post fails.
   */
  public ResponseEntity<Void> updatePost(Long postId, PostEntity post) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPut request = new HttpPut(postServiceUrlWithId + "/" + postId);
      StringEntity entity = new StringEntity(objectMapper.writeValueAsString(post));
      request.setEntity(entity);
      request.setHeader("Accept", "application/json");
      request.setHeader("Content-type", "application/json");
      httpClient.execute(request);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_UPDATE_POST, HttpStatus.NOT_FOUND.value());
    }
  }

  /**
   * Deletes a post by ID.
   *
   * @param postId The ID of the post to delete.
   * @return ResponseEntity with no content if deletion is successful.
   * @throws HttpException if the request to delete the post fails.
   */
  public ResponseEntity<Void> deletePost(Long postId) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpDelete request = new HttpDelete(postServiceUrlWithId + "/" + postId);
      httpClient.execute(request);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      throw new HttpException(POST_NOT_FOUND, HttpStatus.NOT_FOUND.value());
    }
  }
}
