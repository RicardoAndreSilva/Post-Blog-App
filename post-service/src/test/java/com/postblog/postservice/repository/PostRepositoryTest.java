package com.postblog.postservice.repository;


import com.postblog.postservice.entities.PostEntity;
import com.postblog.postservice.utils.EntityCreator;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@DisplayName("Test for Post repository")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  @Test
  @DisplayName("Test for save post when successful")
  void save_persistPostTest_WhenSuccessful() {
    PostEntity postToBeSaved = EntityCreator.createSamplePost();
    PostEntity postSaved = this.postRepository.save(postToBeSaved);

    Assertions.assertThat(postSaved).isNotNull();
    Assertions.assertThat(postSaved.getTitle()).isEqualTo(postToBeSaved.getTitle());
    Assertions.assertThat(postSaved.getContent()).isEqualTo(postToBeSaved.getContent());

  }

  @Test
  @DisplayName("Test for update post when successful")
  void update_persistPostTest_WhenSuccessful() {
    PostEntity postToBeSaved = EntityCreator.createSamplePostToBeSaved();
    PostEntity postSaved = this.postRepository.save(postToBeSaved);
    postSaved.setAuthor("jUnit");
    postSaved.setTitle("Updated Title");
    postSaved.setContent("Updated Content");

    PostEntity postUpdated = this.postRepository.save(postSaved);

    Assertions.assertThat(postUpdated).isNotNull();
    Assertions.assertThat(postUpdated.getId()).isEqualTo(postSaved.getId());
    Assertions.assertThat(postUpdated.getTitle()).isEqualTo(postSaved.getTitle());
    Assertions.assertThat(postUpdated.getContent()).isEqualTo(postSaved.getContent());

  }

  @Test
  @DisplayName("Test for delete when successful")
  void delete_persistPostTest_WhenSuccessful() {
    PostEntity postToBeSaved = EntityCreator.createSamplePostToBeSaved();
    PostEntity postSaved = this.postRepository.save(postToBeSaved);

    this.postRepository.delete(postSaved);

    Optional<PostEntity> postOptional = this.postRepository.findById(postSaved.getId());

    Assertions.assertThat(postOptional).isEmpty();
  }

  @Test
  @DisplayName("Test for get post by id when successful")
  void findById_persistPostTest_WhenSuccessful() {
    PostEntity postToBeSaved = EntityCreator.createSamplePostToBeSaved();
    PostEntity postSaved = this.postRepository.save(postToBeSaved);
    Long postId = postSaved.getId();

    Optional<PostEntity> postOptional = this.postRepository.findById(postSaved.getId());

    Assertions.assertThat(postOptional).as("post Id" + postId).isPresent();
    Assertions.assertThat(postOptional.get().getId()).isEqualTo(postId);
  }

  @Test
  @DisplayName("Test for get all posts when successful")
  void findAll_persistPostTest_WhenSuccessful() {
    PostEntity postToBeSaved1 = EntityCreator.createSamplePost();
    PostEntity postToBeSaved2 = EntityCreator.createSamplePost2();

    PostEntity postSaved1 = this.postRepository.save(postToBeSaved1);
    PostEntity postSaved2 = this.postRepository.save(postToBeSaved2);

    List<PostEntity> posts = this.postRepository.findAll();

    Assertions.assertThat(posts).isNotNull().hasSize(2).contains(postSaved1, postSaved2);
  }
}
