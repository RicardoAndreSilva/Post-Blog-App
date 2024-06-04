package com.postblog.dataintegration.service.services;

import static com.postblog.dataintegration.service.utils.Constants.COMMENT_NOT_FOUND;
import static com.postblog.dataintegration.service.utils.Constants.FAILED_TO_CREATE_COMMENT;
import static com.postblog.dataintegration.service.utils.Constants.FAILED_TO_GET_COMMENTS;
import static com.postblog.dataintegration.service.utils.Constants.FAILED_TO_UPDATE_COMMENT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postblog.dataintegration.service.exceptions.HttpException;
import com.postblog.postservice.entities.CommentEntity;
import com.postblog.postservice.entities.CommentResponse;
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
public class CommentServiceProxy {

  @Value("${comment.service.url}")
  private String commentServiceUrl;

  @Value("${comment.service.url.with_id}")
  private String commentServiceUrlWithId;

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Retrieves comment details by ID.
   *
   * @param commentId The ID of the comment to retrieve.
   * @return ResponseEntity containing the comment details if successful.
   * @throws HttpException if the request to retrieve comment details fails.
   */
  public ResponseEntity<CommentResponse> getCommentById(Long commentId) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(commentServiceUrlWithId + "/" + commentId);
      CloseableHttpResponse response = httpClient.execute(request);
      String responseBody = EntityUtils.toString(response.getEntity());
      CommentResponse commentResponse = objectMapper.readValue(responseBody, CommentResponse.class);
      return new ResponseEntity<>(commentResponse, HttpStatus.OK);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_GET_COMMENTS, HttpStatus.NOT_FOUND.value());
    }
  }

  /**
   * Creates a new comment.
   *
   * @param comment The comment entity to create.
   * @return ResponseEntity containing the created comment details if successful.
   * @throws HttpException if the request to create the comment fails.
   */
  public ResponseEntity<CommentResponse> createComment(CommentEntity comment) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPost request = new HttpPost(commentServiceUrl);
      StringEntity entity = new StringEntity(objectMapper.writeValueAsString(comment));
      request.setEntity(entity);
      request.setHeader("Accept", "application/json");
      request.setHeader("Content-type", "application/json");
      CloseableHttpResponse response = httpClient.execute(request);
      String responseBody = EntityUtils.toString(response.getEntity());
      CommentResponse commentResponse = objectMapper.readValue(responseBody, CommentResponse.class);
      return new ResponseEntity<>(commentResponse, HttpStatus.CREATED);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_CREATE_COMMENT, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /**
   * Updates an existing comment by ID.
   *
   * @param commentId The ID of the comment to update.
   * @param comment   The updated comment entity.
   * @return ResponseEntity with no content if the update is successful.
   * @throws HttpException if the request to update the comment fails.
   */
  public ResponseEntity<Void> updateComment(Long commentId, CommentEntity comment) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPut request = new HttpPut(commentServiceUrlWithId + "/" + commentId);
      StringEntity entity = new StringEntity(objectMapper.writeValueAsString(comment));
      request.setEntity(entity);
      request.setHeader("Accept", "application/json");
      request.setHeader("Content-type", "application/json");
      httpClient.execute(request);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_UPDATE_COMMENT, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /**
   * Deletes a comment by ID.
   *
   * @param commentId The ID of the comment to delete.
   * @return ResponseEntity with no content if deletion is successful.
   * @throws HttpException if the request to delete the comment fails.
   */
  public ResponseEntity<Void> deleteComment(Long commentId) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpDelete request = new HttpDelete(commentServiceUrlWithId + "/" + commentId);
      httpClient.execute(request);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      throw new HttpException(COMMENT_NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }
}
