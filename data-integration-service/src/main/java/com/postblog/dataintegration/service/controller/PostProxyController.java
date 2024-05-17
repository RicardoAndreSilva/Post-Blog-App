package com.postblog.dataintegration.service.controller;

import com.postblog.dataintegration.service.exceptions.HttpException;
import com.postblog.dataintegration.service.services.PostServiceProxy;
import com.postblog.postservice.entities.PostEntity;
import com.postblog.postservice.entities.PostResponse;
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

@Tag(name = "Post Proxy Operations", description = "Endpoints for post proxy management")
@RequestMapping("/api")
@RestController
public class PostProxyController {

  @Autowired
  private PostServiceProxy postServiceProxy;

  @GetMapping("/posts/{postId}")
  @Operation(summary = "Get post details by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Post found"),
      @ApiResponse(responseCode = "404", description = "Post not found")
  })
  public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
    try {
      return postServiceProxy.getPostById(postId);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @PostMapping("/posts")
  @Operation(summary = "Create a new post")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Post created"),
      @ApiResponse(responseCode = "400", description = "Invalid request")
  })
  public ResponseEntity<PostResponse> createPost(@RequestBody PostEntity post) {
    try {
      return postServiceProxy.createPost(post);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @PutMapping("/posts/{postId}")
  @Operation(summary = "Update an existing post by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Post updated"),
      @ApiResponse(responseCode = "404", description = "Post not found")
  })
  public ResponseEntity<Void> updatePost(@PathVariable Long postId, @RequestBody PostEntity post) {
    try {
      return postServiceProxy.updatePost(postId, post);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }

  @DeleteMapping("/posts/{postId}")
  @Operation(summary = "Delete a post by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Post deleted"),
      @ApiResponse(responseCode = "404", description = "Post not found")
  })
  public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
    try {
      return postServiceProxy.deletePost(postId);
    } catch (HttpException e) {
      return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).build();
    }
  }
}


