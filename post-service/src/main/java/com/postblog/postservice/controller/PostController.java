package com.postblog.postservice.controller;

import com.postblog.postservice.entities.PostEntity;
import com.postblog.postservice.entities.PostResponse;
import com.postblog.postservice.exceptions.HttpException;
import com.postblog.postservice.service.PostService;
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
@RequestMapping("/api/posts")
public class PostController {

  @Autowired
  private PostService postService;
  
  @GetMapping("/{postId}")
  @Operation(summary = "Get post details by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Post found"),
      @ApiResponse(responseCode = "404", description = "Post not found")
  })
  public ResponseEntity<PostResponse> getPostDetails(@PathVariable("postId") Long postId) {
    try {
      PostResponse postResponse = postService.getPostById(postId);
      return ResponseEntity.status(HttpStatus.OK).body(postResponse);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @PostMapping
  @Operation(summary = "Create a new post")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Post created"),
      @ApiResponse(responseCode = "400", description = "Invalid request")
  })
  public ResponseEntity<String> savePost(@Valid @RequestBody PostEntity post) {
    try {
      postService.createPost(post);
      return ResponseEntity.status(HttpStatus.CREATED).body("Post created");
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @PutMapping("/{postId}")
  @Operation(summary = "Update an existing post by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Post updated"),
      @ApiResponse(responseCode = "400", description = "Invalid request")
  })
  public ResponseEntity<String> updatePost(@PathVariable("postId") Long postId,
      @RequestBody PostEntity updatePost) {
    try {
      postService.updatePostById(postId, updatePost);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Post updated");
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @GetMapping
  @Operation(summary = "Get all posts")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Posts found"),
      @ApiResponse(responseCode = "404", description = "No posts found")
  })
  public ResponseEntity<List<PostResponse>> getAllPosts() {
    try {
      List<PostResponse> postResponses = postService.getAllPosts();
      return ResponseEntity.ok(postResponses);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @DeleteMapping("/{postId}")
  @Operation(summary = "Delete a post by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Post deleted"),
      @ApiResponse(responseCode = "404", description = "Post not found")
  })
  public ResponseEntity<Void> deleteUser(@PathVariable("postId") Long postId) {
    try {
      postService.deletePostById(postId);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }
}
