package com.postblog.postservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.postblog.postservice.entities.PostEntity;
import com.postblog.postservice.entities.PostResponse;
import com.postblog.postservice.exceptions.HttpException;
import com.postblog.postservice.service.PostService;
import com.postblog.postservice.utils.EntityCreator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

  @InjectMocks
  private PostController postController;

  @Mock
  private PostService postServiceMock;

  @Test
  @DisplayName("Test for retrieving all posts returns list of posts when successful")
  void testGetAllPosts_returnsListOfPosts_WhenSuccessful() {
    ResponseEntity<List<PostResponse>> responseEntity = postController.getAllPosts();
    Assertions.assertNotNull(responseEntity);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    List<PostResponse> postResponses = responseEntity.getBody();
    Assertions.assertNotNull(postResponses);
    Assertions.assertTrue(postResponses.isEmpty());
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during getAllPosts ")
  void testGetAllPosts_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(postServiceMock).getAllPosts();
    ResponseEntity<List<PostResponse>> response = postController.getAllPosts();
    Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  @DisplayName("Test for retrieving post by ID returns post when successful")
  void testGetPostById_returnsPost_WhenSuccessful() {
    when(postServiceMock.getPostById(anyLong())).thenReturn(EntityCreator.createValidSamplePost());
    ResponseEntity<PostResponse> responseEntity = postController.getPostDetails(1L);
    Assertions.assertNotNull(responseEntity);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    PostResponse postResponse = responseEntity.getBody();
    Assertions.assertNotNull(postResponse);
    Assertions.assertEquals(1L, postResponse.getId());
    Assertions.assertEquals("Sample Post", postResponse.getTitle());
    Assertions.assertEquals("This is test.", postResponse.getContent());
    Assertions.assertEquals(1L, postResponse.getId());
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during getPostById")
  void testGetPostById_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    when(postServiceMock.getPostById(anyLong())).thenThrow(
        new HttpException("Post not found", HttpStatus.NOT_FOUND.value()));
    ResponseEntity<PostResponse> response = postController.getPostDetails(1L);
    Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @DisplayName("Test for saving post when successful")
  void testSavePost_ReturnsOk_WhenSuccessful() {
    PostEntity postEntity = new PostEntity();
    ResponseEntity<String> postResponseEntity = postController.savePost(postEntity);
    Assertions.assertNotNull(postResponseEntity);
    Assertions.assertEquals(HttpStatus.CREATED, postResponseEntity.getStatusCode());
    Assertions.assertEquals("Post created", postResponseEntity.getBody());
    verify(postServiceMock, times(1)).createPost(any());
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during saving post")
  void testSavePost_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(postServiceMock).createPost(any());
    ResponseEntity<String> response = postController.savePost(new PostEntity());
    Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  @DisplayName("Test for updating post when successful")
  void testUpdatePost_ReturnsOk_WhenSuccessful() {
    Assertions.assertDoesNotThrow(() -> {
      postController.updatePost(1L, new PostEntity());
    });
    ResponseEntity<String> entity = postController.updatePost(2L, new PostEntity());
    Assertions.assertNotNull(entity);
    Assertions.assertEquals(HttpStatus.NO_CONTENT, entity.getStatusCode());
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during updating post")
  void testUpdatePost_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    PostEntity postEntity = new PostEntity();
    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(postServiceMock).updatePostById(postEntity.getId(), postEntity);
    ResponseEntity<String> response = postController.updatePost(postEntity.getId(), postEntity);
    Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  @DisplayName("Delete removes post successfully")
  void testDeletePost_ReturnsOK_WhenSuccessful() {
    Assertions.assertDoesNotThrow(() -> postController.deleteUser(1L));
    ResponseEntity<Void> entity = postController.deleteUser(1L);
    Assertions.assertNotNull(entity);
    Assertions.assertEquals(HttpStatus.NO_CONTENT, entity.getStatusCode());
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during deleting post")
  void testDeletePost_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(postServiceMock).deletePostById(anyLong());
    ResponseEntity<Void> response = postController.deleteUser(1L);
    Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }
}
