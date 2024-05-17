package com.postblog.postservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.postblog.postservice.entities.CommentEntity;
import com.postblog.postservice.entities.CommentResponse;
import com.postblog.postservice.exceptions.HttpException;
import com.postblog.postservice.service.CommentService;
import com.postblog.postservice.utils.EntityCreator;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CommentControllerTest {

  @InjectMocks
  private CommentController commentController;

  @Mock
  private CommentService commentServiceMock;

  @Test
  @DisplayName("Test for getAllComments returns list of comments when successful")
  void testGetAllComments_returnsListOfComments_WhenSuccessful() {
    CommentResponse commentResponse = EntityCreator.createValidTestComment();
    CommentEntity commentEntity = EntityCreator.createSampleCommentToBeSaved();

    when(commentServiceMock.getAllComments()).thenReturn(List.of(commentResponse));

    ResponseEntity<List<CommentResponse>> responseEntity = commentController.getAllComments();

    Assertions.assertThat(responseEntity).isNotNull();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<CommentResponse> commentResponses = responseEntity.getBody();

    assert commentResponses != null;
    CommentResponse comment = commentResponses.get(0);

    Assertions.assertThat(commentResponses).isNotNull().isNotEmpty();
    Assertions.assertThat(comment.getContent()).isEqualTo(commentEntity.getContent());
    Assertions.assertThat(comment.getAuthorId()).isEqualTo(commentEntity.getAuthorId());
    Assertions.assertThat(comment.getId()).isEqualTo(commentEntity.getId());
    Assertions.assertThat(comment.getCreateAt().truncatedTo(ChronoUnit.SECONDS))
        .isEqualTo(commentEntity.getCreateAt().truncatedTo(ChronoUnit.SECONDS));
  }

  @Test
  @DisplayName("Test for getCommentById returns comment when successful")
  void testGetCommentById_returnsComment_WhenSuccessful() {
    CommentResponse commentResponse = EntityCreator.createValidTestComment();
    CommentEntity commentEntity = EntityCreator.createSampleCommentToBeSaved();

    when(commentServiceMock.getCommentById(anyLong())).thenReturn(commentResponse);

    ResponseEntity<CommentResponse> responseEntity = commentController.getCommentDetails(1L);

    CommentResponse actualCommentResponse = responseEntity.getBody();

    Assertions.assertThat(responseEntity).isNotNull();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(actualCommentResponse).isNotNull();
    Assertions.assertThat(actualCommentResponse.getId()).isEqualTo(commentEntity.getId());
    Assertions.assertThat(actualCommentResponse.getContent())
        .isEqualTo(commentEntity.getContent());
    Assertions.assertThat(actualCommentResponse.getAuthorId())
        .isEqualTo(commentEntity.getAuthorId());
    Assertions.assertThat(actualCommentResponse.getCreateAt())
        .isEqualTo(commentEntity.getCreateAt());
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during comment getById")
  void testGetCommentById_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    CommentEntity commentEntity = EntityCreator.createSampleCommentToBeSaved();

    when(commentServiceMock.getCommentById(anyLong())).thenThrow(
        new HttpException("Comment not found", HttpStatus.NOT_FOUND.value()));

    ResponseEntity<CommentResponse> response = commentController.getCommentDetails(
        commentEntity.getId());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("Test for save comment when successful")
  void testSaveComment_ReturnsOk_WhenSuccessful() {
    ResponseEntity<String> commentResponseEntity = commentController.saveComment(
        EntityCreator.createSampleCommentToBeSaved());

    Assertions.assertThat(commentResponseEntity).isNotNull();
    Assertions.assertThat(commentResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(commentResponseEntity.getBody()).isEqualTo("Comment created");

    Mockito.verify(commentServiceMock, Mockito.times(1)).createComment(any());
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during comment save")
  void testSaveComment_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    CommentEntity commentEntity = EntityCreator.createSampleCommentToBeSaved();

    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(commentServiceMock)
        .createComment(any());

    ResponseEntity<String> response = commentController.saveComment(commentEntity);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  @DisplayName("Test for update comment when successful")
  void testUpdateComment_ReturnsOk_WhenSuccessful() {
    Assertions.assertThatCode(() -> {
      commentController.updateComment(1L, EntityCreator.createSampleCommentToBeSaved());
    }).doesNotThrowAnyException();

    ResponseEntity<String> entity = commentController.updateComment(2L,
        EntityCreator.createSampleCommentToBeSaved());

    Assertions.assertThat(entity).isNotNull();
    Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during comment update")
  void testUpdateComment_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    CommentEntity commentEntity = EntityCreator.createSampleCommentToBeSaved();

    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(commentServiceMock)
        .updateCommentById(commentEntity.getId(), commentEntity);

    ResponseEntity<String> response = commentController.updateComment(commentEntity.getId(),
        commentEntity);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  @DisplayName("delete removes comment successful")
  void testDeleteComment_ReturnsOK_WhenSuccessful() {
    Assertions.assertThatCode(() -> commentController.deleteComment(1L))
        .doesNotThrowAnyException();

    ResponseEntity<Void> entity = commentController.deleteComment(1L);

    Assertions.assertThat(entity).isNotNull();
    Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("Test controller returns correct status code when HttpException is thrown during comment delete")
  void testDeleteComment_ReturnsCorrectStatusCode_WhenHttpExceptionThrown() {
    CommentEntity commentEntity = EntityCreator.createSampleCommentToBeSaved();

    doThrow(new HttpException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .when(commentServiceMock)
        .deleteCommentById(commentEntity.getId());

    ResponseEntity<Void> response = commentController.deleteComment(commentEntity.getId());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
