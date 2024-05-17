package com.postblog.postservice.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.postblog.postservice.entities.PostEntity;
import com.postblog.postservice.repository.PostRepository;
import com.postblog.postservice.utils.EntityCreator;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
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
class PostControllerTestIT {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private PostRepository postRepository;

  @LocalServerPort
  private int port;

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

  @Test
  @DisplayName("GetAllPosts returns empty list when no posts exist")
  void getAllPosts_ReturnsEmptyList_WhenNoPostsExist() {
    String url = createURLWithPort("/api/posts");
    ResponseEntity<String> response = testRestTemplate.exchange(
        url,
        HttpMethod.GET, null, String.class);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      List<PostEntity> posts = objectMapper.readValue(response.getBody(),
          new TypeReference<List<PostEntity>>() {
          });
      Assertions.assertThat(posts).isNotNull().isEmpty();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }


  @Test
  @DisplayName("GetAllPosts returns posts when successful")
  void getAllPosts_ReturnsPosts_WhenSuccessful() {
    Map<Long, PostEntity> savedPosts = new HashMap<>();
    savedPosts.put(1L, postRepository.save(EntityCreator.createSamplePost()));
    savedPosts.put(2L, postRepository.save(EntityCreator.createSamplePost()));

    String url = createURLWithPort("/api/posts");
    ResponseEntity<String> response = testRestTemplate.exchange(
        url,
        HttpMethod.GET, null, String.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getBody()).isNotNull().isNotEmpty();

    String responseBody = response.getBody();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    try {
      List<PostEntity> posts = objectMapper.readValue(responseBody,
          new TypeReference<List<PostEntity>>() {
          });

      for (PostEntity actualPost : posts) {
        assertTrue(savedPosts.containsKey(actualPost.getId()),
            "Post with ID " + actualPost.getId() + " not found in saved posts");
        PostEntity expectedPost = savedPosts.get(actualPost.getId());

        assertEquals(expectedPost.getTitle(), actualPost.getTitle());
        assertEquals(expectedPost.getContent(), actualPost.getContent());
        assertEquals(expectedPost.getAuthor(), actualPost.getAuthor());
        assertEquals(expectedPost.getId(), actualPost.getId());
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("Save returns post when successful")
  void save_ReturnsPost_WhenSuccessful() {
    String url = createURLWithPort("/api/posts");
    ResponseEntity<String> postResponseEntity = testRestTemplate.postForEntity(
        url,
        EntityCreator.createValidSamplePost(),
        String.class);

    Assertions.assertThat(postResponseEntity).isNotNull();
    Assertions.assertThat(postResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(postResponseEntity.getBody()).isEqualTo("Post created");
  }

  @Test
  @DisplayName("Delete returns post when successful")
  void delete_ReturnsPost_WhenSuccessful() {
    PostEntity post = postRepository.save(EntityCreator.createSamplePost());
    String url = createURLWithPort("/api/posts/" + post.getId());
    ResponseEntity<Void> resp = testRestTemplate.exchange(
        url,
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        Void.class);

    Assertions.assertThat(resp).isNotNull();
    Assertions.assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("Test for delete throws exception when NOT FOUND")
  void testDeletePost_ReturnsNOT_FOUND_WhenNotFound() {
    String url = createURLWithPort("/api/posts/5");
    ResponseEntity<Void> res = testRestTemplate.exchange(
        url,
        HttpMethod.DELETE,
        null,
        Void.class);

    Assertions.assertThat(res.getStatusCode())
        .as("Check that the status code is NOT_FOUND").isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("GetByIdPost returns post when successful")
  void getByIdPost_ReturnsPost_WhenSuccessful() {
    PostEntity postEntity = postRepository.save(EntityCreator.createSamplePost());
    String url = createURLWithPort("/api/posts/" + 1);
    ResponseEntity<PostEntity> res = testRestTemplate.exchange(
        url,
        HttpMethod.GET, null, PostEntity.class);

    Assertions.assertThat(res.getBody()).isNotNull();
    Assertions.assertThat(res.getBody().getId()).isEqualTo(postEntity.getId());
    Assertions.assertThat(res.getBody().getTitle()).isEqualTo(postEntity.getTitle());
    Assertions.assertThat(res.getBody().getContent()).isEqualTo(postEntity.getContent());
    Assertions.assertThat(res.getBody().getAuthor()).isEqualTo(postEntity.getAuthor());
  }

  @Test
  @DisplayName("Test for getById throws exception when NOT FOUND")
  void testGetPostById_ReturnsNOT_FOUND_WhenNotFound() {
    String url = createURLWithPort("/api/posts/5");
    ResponseEntity<PostEntity> res = testRestTemplate.getForEntity(
        url,
        PostEntity.class);

    Assertions.assertThat(res.getStatusCode())
        .as("Check that the status code is NOT_FOUND").isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("Update returns post when successful")
  void testUpdatePost_ReturnsOK_WhenSuccessful() {
    PostEntity postEntityToSave = postRepository.save(EntityCreator.createSamplePost());
    String url = createURLWithPort("/api/posts/" + postEntityToSave.getId());
    ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
        url,
        HttpMethod.PUT, new HttpEntity<>(postEntityToSave), Void.class);

    PostEntity updatedPost = postRepository.findById(postEntityToSave.getId()).orElse(null);

    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    Assertions.assertThat(updatedPost).isNotNull();
    Assertions.assertThat(postEntityToSave).isNotNull();
    Assertions.assertThat(postEntityToSave.getId()).isEqualTo(updatedPost.getId());
    Assertions.assertThat(postEntityToSave.getTitle()).isEqualTo(updatedPost.getTitle());
    Assertions.assertThat(postEntityToSave.getContent()).isEqualTo(updatedPost.getContent());
    Assertions.assertThat(postEntityToSave.getAuthor()).isEqualTo(updatedPost.getAuthor());
  }


  @Test
  @DisplayName("Update returns post when successful")
  void updatePost_ReturnsPost_WhenSuccessful() {
    PostEntity postEntityToSave = postRepository.save(EntityCreator.createSamplePost());
    String url = createURLWithPort("/api/posts/" + postEntityToSave.getId());
    ResponseEntity<Void> res = testRestTemplate.exchange(
        url,
        HttpMethod.PUT, new HttpEntity<>(postEntityToSave), Void.class);

    PostEntity updatedPost = postRepository.findById(postEntityToSave.getId()).orElse(null);

    Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    Assertions.assertThat(updatedPost).isNotNull();
    Assertions.assertThat(postEntityToSave).isNotNull();
    Assertions.assertThat(postEntityToSave.getId()).isEqualTo(updatedPost.getId());
    Assertions.assertThat(postEntityToSave.getTitle()).isEqualTo(updatedPost.getTitle());
    Assertions.assertThat(postEntityToSave.getContent()).isEqualTo(updatedPost.getContent());
    Assertions.assertThat(postEntityToSave.getAuthor()).isEqualTo(updatedPost.getAuthor());
    Assertions.assertThat(postEntityToSave.getCreateAt().truncatedTo(ChronoUnit.SECONDS))
        .isEqualTo(updatedPost.getCreateAt().truncatedTo(ChronoUnit.SECONDS));
  }
}
