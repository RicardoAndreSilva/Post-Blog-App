package com.postblog.postservice.entities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.postblog.postservice.repository.CommentRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CommentEntityTest {

  @InjectMocks
  private CommentEntity commentEntity;

  @Mock
  private CommentRepository commentRepository;

  @BeforeEach
  public void setUp() {
    commentEntity = new CommentEntity(1L, "Test comment", 1L, LocalDateTime.now(), "", "", null,
        null);
  }

  @Test
  @DisplayName("Test for save comment when successful")
  void testSaveComment_returnsOk_WhenSuccessful() {
    when(commentRepository.save(commentEntity)).thenReturn(commentEntity);

    CommentEntity savedComment = commentRepository.save(commentEntity);

    Assertions.assertThat(commentEntity.getContent()).isEqualTo(savedComment.getContent());
    Assertions.assertThat(commentEntity.getAuthorId()).isEqualTo(savedComment.getAuthorId());
    Assertions.assertThat(commentEntity.getId()).isEqualTo(savedComment.getId());
    Assertions.assertThat(commentEntity.getCreateAt()).isEqualTo(savedComment.getCreateAt());
  }

  @Test
  @DisplayName("Test for update comment when successful")
  void testUpdateComment_returnsOk_WhenSuccessful() {
    commentEntity.setContent("Updated comment");

    when(commentRepository.save(commentEntity)).thenReturn(commentEntity);

    CommentEntity updatedComment = commentRepository.save(commentEntity);

    Assertions.assertThat(commentEntity.getContent()).isEqualTo(updatedComment.getContent());
  }

  @Test
  @DisplayName("Test for get comment by id when successful")
  void testFindByIdComment_returnsOk_WhenSuccessful() {
    when(commentRepository.findById(commentEntity.getId())).thenReturn(Optional.of(commentEntity));

    Optional<CommentEntity> foundComment = commentRepository.findById(commentEntity.getId());

    assertTrue(foundComment.isPresent());
    Assertions.assertThat(commentEntity.getId()).isEqualTo(foundComment.get().getId());
  }

  @Test
  @DisplayName("Test for get comment by id returns empty optional when comment not found")
  void testFindByIdComment_returnsEmptyOptional_WhenCommentNotFound() {
    when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

    Optional<CommentEntity> foundComment = commentRepository.findById(5L);

    Assertions.assertThat(foundComment).isEmpty();
  }

  @Test
  @DisplayName("Test for delete comment when successful")
  void testDeleteComment_returnsOk_WhenSuccessful() {
    commentRepository.delete(commentEntity);
    Optional<CommentEntity> deletedComment = commentRepository.findById(commentEntity.getId());
    assertFalse(deletedComment.isPresent());
  }

  @Test
  @DisplayName("Test for get all comments when successful")
  void testGetAllComments_returnsOk_WhenSuccessful() {
    CommentEntity commentEntity2 = new CommentEntity(2L, "Another comment", 1L,
        LocalDateTime.now(), "", "", null, null);

    when(commentRepository.findAll()).thenReturn(Arrays.asList(commentEntity, commentEntity2));

    List<CommentEntity> comments = commentRepository.findAll();

    Assertions.assertThat(comments).hasSize(2);
    Assertions.assertThat(commentEntity.getContent()).isEqualTo(comments.get(0).getContent());
    Assertions.assertThat(commentEntity2.getContent()).isEqualTo(comments.get(1).getContent());
  }

  @Test
  @DisplayName("Test for equals() and hashCode()")
  void testEqualsAndHashCode_returnsOk_WhenSuccessful() {
    LocalDateTime now = LocalDateTime.now();
    CommentEntity comment1 = new CommentEntity(1L, "Test comment", 1L, now, "", "", null, null);
    CommentEntity comment2 = new CommentEntity(1L, "Test comment", 1L, now, "", "", null, null);
    CommentEntity comment3 = new CommentEntity(2L, "Another comment", 1L,
        LocalDateTime.now(), "", "", null, null);

    Assertions.assertThat(comment1.equals(comment2)).isTrue();
    Assertions.assertThat(comment1.hashCode()).hasSameHashCodeAs(comment2.hashCode());

    Assertions.assertThat(comment1.equals(comment3)).isFalse();
    Assertions.assertThat(comment1.hashCode()).isNotEqualTo(comment3.hashCode());
  }

  @Test
  @DisplayName("Test for toString()")
  void testToString_returnsOk_WhenSuccessful() {
    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    CommentEntity comment = new CommentEntity(1L, "Test comment", 1L, now, "", "", null, null);
    Assertions.assertThat(comment.toString())
        .contains("CommentEntity")
        .contains("id=1")
        .contains("content=Test comment")
        .contains("authorId=1")
        .contains("createAt=" + now);
  }

  @Test
  @DisplayName("Test object creation using Lombok @Builder")
  void testObjectCreationUsingBuilder_ReturnsOk_WhenSuccessful() {
    CommentEntity comment = CommentEntity.builder()
        .id(1L)
        .content("Content")
        .authorId(1L)
        .createAt(LocalDateTime.now())
        .build();

    Assertions.assertThat(comment).isNotNull();
  }
}
