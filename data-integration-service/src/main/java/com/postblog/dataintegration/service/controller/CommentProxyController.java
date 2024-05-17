package com.postblog.dataintegration.service.controller;

import com.postblog.dataintegration.service.exceptions.HttpException;
import com.postblog.dataintegration.service.services.CommentServiceProxy;
import com.postblog.postservice.entities.CommentEntity;
import com.postblog.postservice.entities.CommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing comment-related endpoints through a proxy service.
 */
@Tag(name = "Comment Proxy Operations", description = "Endpoints for comment proxy management")
@RequestMapping("/api")
@RestController
public class CommentProxyController {

  @Autowired
  private CommentServiceProxy commentServiceProxy; // Proxy service for handling comment-related operations

  /**
   * Retrieves comment details by ID.
   *
   * @param commentId The ID of the comment to retrieve.
   * @return ResponseEntity containing the comment details if successful, or an error response if not found.
   */
  @GetMapping("/comments/{commentId}")
  @Operation(summary = "Get comment details by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Comment found"),
      @ApiResponse(responseCode = "404", description = "Comment not found")
  })
  public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long commentId) {
    try {
      return commentServiceProxy.getCommentById(commentId);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  /**
   * Creates a new comment.
   *
   * @param comment The comment entity to create.
   * @return ResponseEntity containing the created comment details if successful, or an error response if creation fails.
   */
  @PostMapping("/comments")
  @Operation(summary = "Create a new comment")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Comment created"),
      @ApiResponse(responseCode = "400", description = "Invalid request")
  })
  public ResponseEntity<CommentResponse> createComment(@RequestBody CommentEntity comment) {
    try {
      return commentServiceProxy.createComment(comment);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  /**
   * Updates an existing comment by ID.
   *
   * @param commentId The ID of the comment to update.
   * @param comment   The updated comment entity.
   * @return ResponseEntity with no content if the update is successful, or an error response if update fails.
   */
  @PutMapping("/comments/{commentId}")
  @Operation(summary = "Update an existing comment by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Comment updated"),
      @ApiResponse(responseCode = "404", description = "Comment not found")
  })
  public ResponseEntity<Void> updateComment(@PathVariable Long commentId, @RequestBody CommentEntity comment) {
    try {
      return commentServiceProxy.updateComment(commentId, comment);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  /**
   * Deletes a comment by ID.
   *
   * @param commentId The ID of the comment to delete.
   * @return ResponseEntity with no content if deletion is successful, or an error response if deletion fails.
   */
  @DeleteMapping("/comments/{commentId}")
  @Operation(summary = "Delete a comment by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Comment deleted"),
      @ApiResponse(responseCode = "404", description = "Comment not found")
  })
  public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
    try {
      return commentServiceProxy.deleteComment(commentId);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }
}

