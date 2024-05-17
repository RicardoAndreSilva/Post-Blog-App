package com.postblog.postservice.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postblog.postservice.entities.CommentEntity;
import com.postblog.postservice.entities.CommentResponse;
import com.postblog.postservice.entities.PostEntity;
import com.postblog.postservice.repository.CommentRepository;
import com.postblog.postservice.repository.PostRepository;
import com.postblog.postservice.utils.EntityCreator;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class CommentControllerTestIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private ObjectMapper objectMapper;
  @LocalServerPort
  private int port;


  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

  @Test
  @DisplayName("GetAllComments returns empty list when no comments exist")
  void getAllComments_ReturnsEmptyList_WhenNoCommentsExist() {
    String url = createURLWithPort("/api/comments");
    ResponseEntity<List<CommentEntity>> response = testRestTemplate.exchange(
        url,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<CommentEntity>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEmpty();
  }

  @Test
  @DisplayName("SaveComment returns comment when successful")
  void saveComment_ReturnsComment_WhenSuccessful() {
    PostEntity post = postRepository.save(EntityCreator.createSamplePost());
    CommentEntity commentToSave = EntityCreator.createSampleCommentToBeSaved();

    commentToSave.setPost(post);

    String url = createURLWithPort("/api/comments");
    ResponseEntity<String> response = testRestTemplate.postForEntity(
        url,
        commentToSave,
        String.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Comment created", response.getBody());
  }


  @Test
  @DisplayName("DeleteComment returns no content when successful")
  void deleteComment_ReturnsNoContent_WhenSuccessful() {
    PostEntity post = postRepository.save(EntityCreator.createSamplePost());

    CommentEntity commentToSave = EntityCreator.createSampleCommentToBeSaved();
    commentToSave.setPost(post);
    CommentEntity savedComment = commentRepository.save(commentToSave);

    String url = createURLWithPort("/api/comments/" + savedComment.getId());
    ResponseEntity<Void> response = testRestTemplate.exchange(
        url,
        HttpMethod.DELETE,
        null,
        Void.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("Test for delete throws exception when NOT FOUND")
  void testDeleteComment_ReturnsNOT_FOUND_WhenNotFound() {
    String url = createURLWithPort("/api/comments/999");
    ResponseEntity<Void> response = testRestTemplate.exchange(
        url,
        HttpMethod.DELETE,
        null,
        Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("GetCommentById returns comment when successful")
  void getCommentById_ReturnsComment_WhenSuccessful() {
    PostEntity post = postRepository.save(EntityCreator.createSamplePost());
    CommentEntity commentToSave = commentRepository.save(
        EntityCreator.createSampleCommentToBeSaved());

    commentToSave.setPost(post);

    String url = createURLWithPort("/api/comments/" + commentToSave.getId());
    ResponseEntity<CommentResponse> response = testRestTemplate.exchange(
        url,
        HttpMethod.GET, null, CommentResponse.class);

    CommentResponse commentResponse = response.getBody();

    Assertions.assertThat(commentResponse).isNotNull();
    Assertions.assertThat(commentResponse.getCreateAt().truncatedTo(ChronoUnit.SECONDS))
        .isEqualTo(commentToSave.getCreateAt().truncatedTo(ChronoUnit.SECONDS));
  }


  @Test
  @DisplayName("Test for getCommentById throws exception when NOT FOUND")
  void testGetCommentById_ReturnsNOT_FOUND_WhenNotFound() {
    String url = createURLWithPort("/api/comments/999");
    ResponseEntity<CommentEntity> response = testRestTemplate.exchange(
        url,
        HttpMethod.GET, null, CommentEntity.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("UpdateComment returns comment when successful")
  void updateComment_ReturnsComment_WhenSuccessful() {
    PostEntity post = postRepository.save(EntityCreator.createSamplePost());
    CommentEntity commentToSave = commentRepository.save(
        EntityCreator.createSampleCommentToBeSaved());
    commentToSave.setPost(post);
    commentToSave.setContent("Updated content");

    String url = createURLWithPort("/api/comments/" + commentToSave.getId());
    ResponseEntity<CommentEntity> response = testRestTemplate.exchange(
        url,
        HttpMethod.PUT,
        new HttpEntity<>(commentToSave),
        CommentEntity.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
  }

  @Test
  @DisplayName("Test for updateComment throws exception when NOT FOUND")
  void testUpdateComment_ReturnsNOT_FOUND_WhenNotFound() {
    CommentEntity commentToUpdate = EntityCreator.createSampleCommentToBeSaved();
    commentToUpdate.setId(999L);

    String url = createURLWithPort("/api/comments/" + commentToUpdate.getId());
    ResponseEntity<CommentEntity> response = testRestTemplate.exchange(
        url,
        HttpMethod.PUT,
        new HttpEntity<>(commentToUpdate),
        CommentEntity.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("GetCommentsByPostId returns comments when successful")
  void getCommentsByPostId_ReturnsComments_WhenSuccessful() {
    PostEntity post = postRepository.save(EntityCreator.createSamplePost());
    CommentEntity comment1 = commentRepository.save(EntityCreator.createSampleCommentToBeSaved());
    comment1.setPost(post);

    String url = createURLWithPort("/api/comments/" + comment1.getId());

    ResponseEntity<CommentEntity> response = testRestTemplate.exchange(
        url,
        HttpMethod.GET, null, CommentEntity.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(
            Objects.requireNonNull(response.getBody()).getCreateAt().truncatedTo(ChronoUnit.SECONDS))
        .isEqualTo(comment1.getCreateAt().truncatedTo(ChronoUnit.SECONDS));
  }


  @Test
  @DisplayName("Test for getCommentsByPostId throws exception when POST NOT FOUND")
  void testGetCommentsByPostId_ReturnsNOT_FOUND_WhenPostNotFound() {
    String url = createURLWithPort("/api/posts/999/comments");
    ResponseEntity<Void> response = testRestTemplate.exchange(
        url,
        HttpMethod.GET, null, Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
