package com.postblog.postservice.repository;


import com.postblog.postservice.entities.CommentEntity;
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
@DisplayName("Test for Comment repository")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private PostRepository postRepository;

  @Test
  @DisplayName("Test for save comment when successful")
  void save_persistCommentTest_WhenSuccessful() {

    PostEntity postToBeSaved = EntityCreator.createSamplePost();
    PostEntity postSaved = this.postRepository.save(postToBeSaved);

    CommentEntity commentToBeSaved = EntityCreator.createSampleCommentToBeSaved();
    commentToBeSaved.setPost(postSaved);

    CommentEntity commentSaved = this.commentRepository.save(commentToBeSaved);

    Assertions.assertThat(commentSaved).isNotNull();
    Assertions.assertThat(commentSaved.getContent()).isEqualTo(commentToBeSaved.getContent());
  }

  @Test
  @DisplayName("Test for update comment when successful")
  void update_persistCommentTest_WhenSuccessful() {
    PostEntity postToBeSaved = EntityCreator.createSamplePost();
    PostEntity postSaved = this.postRepository.save(postToBeSaved);

    CommentEntity commentToBeSaved = EntityCreator.createSampleCommentToBeSaved();
    commentToBeSaved.setPost(postSaved);

    CommentEntity commentSaved = this.commentRepository.save(commentToBeSaved);

    commentSaved.setContent("Updated Content");

    CommentEntity commentUpdated = this.commentRepository.save(commentSaved);

    Assertions.assertThat(commentUpdated).isNotNull();
    Assertions.assertThat(commentUpdated.getId()).isEqualTo(commentSaved.getId());
    Assertions.assertThat(commentUpdated.getContent()).isEqualTo(commentSaved.getContent());
  }


  @Test
  @DisplayName("Test for delete when successful")
  void delete_persistCommentTest_WhenSuccessful() {
    PostEntity postToBeSaved = EntityCreator.createSamplePost();
    PostEntity postSaved = this.postRepository.save(postToBeSaved);

    CommentEntity commentToBeSaved = EntityCreator.createSampleCommentToBeSaved();
    commentToBeSaved.setPost(postSaved);

    CommentEntity commentSaved = this.commentRepository.save(commentToBeSaved);

    this.commentRepository.delete(commentSaved);
    Optional<CommentEntity> commentOptional = this.commentRepository.findById(commentSaved.getId());

    Assertions.assertThat(commentOptional).isEmpty();
  }


  @Test
  @DisplayName("Test for get comment by id when successful")
  void findById_persistCommentTest_WhenSuccessful() {
    PostEntity postToBeSaved = EntityCreator.createSamplePost();
    PostEntity postSaved = this.postRepository.save(postToBeSaved);

    CommentEntity commentToBeSaved = EntityCreator.createSampleCommentToBeSaved();
    commentToBeSaved.setPost(postSaved);

    CommentEntity commentSaved = this.commentRepository.save(commentToBeSaved);
    Long commentId = commentSaved.getId();
    Optional<CommentEntity> commentOptional = this.commentRepository.findById(commentSaved.getId());
    Assertions.assertThat(commentOptional).as("user Id" + commentId).isPresent();
    Assertions.assertThat(commentOptional.get().getId()).isEqualTo(commentId);

  }


  @Test
  @DisplayName("Test for get all comments when successful")
  void findAll_persistCommentTest_WhenSuccessful() {
    PostEntity postToBeSaved = EntityCreator.createSamplePost();
    PostEntity postSaved = this.postRepository.save(postToBeSaved);

    CommentEntity commentToBeSaved1 = EntityCreator.createSampleCommentToBeSaved();
    commentToBeSaved1.setPost(postSaved);
    CommentEntity commentSaved1 = this.commentRepository.save(commentToBeSaved1);

    List<CommentEntity> comments = this.commentRepository.findAll();

    Assertions.assertThat(comments).isNotNull().hasSize(1).contains(commentSaved1);
  }
}
