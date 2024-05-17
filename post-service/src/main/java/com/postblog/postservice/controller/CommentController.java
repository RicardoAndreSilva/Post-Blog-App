package com.postblog.postservice.controller;

import com.postblog.postservice.entities.CommentEntity;
import com.postblog.postservice.entities.CommentResponse;
import com.postblog.postservice.exceptions.HttpException;
import com.postblog.postservice.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
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

@RestController
@RequestMapping("/api/comments")
public class CommentController {

  @Autowired
  private CommentService commentService;

  /**
   * Retrieves the details of a comment based on the provided ID.
   *
   * @param commentId The ID of the comment to retrieve.
   * @return The details of the comment.
   */
  @GetMapping("/{commentId}")
  @Operation(summary = "Get comment details by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Comment found"),
      @ApiResponse(responseCode = "404", description = "Comment not found")
  })
  public ResponseEntity<CommentResponse> getCommentDetails(
      @PathVariable("commentId") Long commentId) {
    try {
      CommentResponse commentResponse = commentService.getCommentById(commentId);
      return ResponseEntity.ok(commentResponse);
    } catch (HttpException e) {
      return ResponseEntity.status(e.getStatusCode()).build();
    }
  }


  /**
   * Creates a new comment.
   *
   * @param commentEntity The comment entity object to be created.
   * @return A response indicating whether the comment was successfully created.
   */
  @PostMapping
  @Operation(summary = "Create a new comment")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Comment created"),
      @ApiResponse(responseCode = "400", description = "Invalid request")
  })
  public ResponseEntity<String> saveComment(@Valid @RequestBody CommentEntity commentEntity) {
    try {
      commentService.createComment(commentEntity);
      return ResponseEntity.status(HttpStatus.CREATED).body("Comment created");
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  /**
   * Updates an existing comment based on the provided ID.
   *
   * @param commentId     The ID of the comment to be updated.
   * @param updateComment The comment entity object with the updates to be applied.
   * @return A response indicating whether the comment was successfully updated.
   */
  @PutMapping("/{commentId}")
  @Operation(summary = "Update an existing comment by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Comment updated"),
      @ApiResponse(responseCode = "404", description = "Comment not found")
  })
  public ResponseEntity<String> updateComment(@Valid @PathVariable("commentId") Long commentId,
      @RequestBody CommentEntity updateComment) {
    try {
      commentService.updateCommentById(commentId, updateComment);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Comment updated");
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  /**
   * Retrieves a list of all comments.
   *
   * @return A list of all comments.
   */
  @GetMapping
  @Operation(summary = "Get all comments")
  @ApiResponse(responseCode = "200", description = "List of comments retrieved")
  public ResponseEntity<List<CommentResponse>> getAllComments() {
    try {
      List<CommentResponse> commentResponses = commentService.getAllComments();
      return ResponseEntity.ok(commentResponses);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  /**
   * Deletes a comment based on the provided ID.
   *
   * @param commentId The ID of the comment to be deleted.
   * @return A response indicating whether the comment was successfully deleted.
   */
  @DeleteMapping("/{commentId}")
  @Operation(summary = "Delete a comment by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Comment deleted"),
      @ApiResponse(responseCode = "404", description = "Comment not found")
  })
  public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
    try {
      commentService.deleteCommentById(commentId);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }
}
