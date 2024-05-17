package com.postblog.postservice.entities;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentResponseTest {

  @Test
  @DisplayName("Test for equals() and hashCode()")
  void testEqualsAndHashCode_returnsOk_WhenSuccessful() {
    LocalDateTime now = LocalDateTime.now();
    CommentResponse comment1 = new CommentResponse(1L, "CommentResponseTest", 1L, now,
        null);
    CommentResponse comment2 = new CommentResponse(1L, "CommentResponseTest", 1L, now,
        null);

    Assertions.assertThat(comment1).isEqualTo(comment2);
    Assertions.assertThat(comment1.hashCode()).hasSameHashCodeAs(comment2.hashCode());
  }

  @Test
  @DisplayName("Test for toString()")
  void testToString_returnsOk_WhenSuccessful() {
    CommentResponse comment = new CommentResponse(1L, "This is test response.", 1L,
        LocalDateTime.now(), null);

    String commentExpected = comment.toString();

    Assertions.assertThat(commentExpected)
        .contains("1")
        .contains("This is test response.")
        .contains("1");
  }

  @Test
  @DisplayName("Test object creation using Lombok @Builder")
  void testObjectCreationUsingBuilder_ReturnsOk_WhenSuccessful() {
    CommentResponse comment = CommentResponse.builder()
        .id(1L)
        .content("Content")
        .authorId(1L)
        .createAt(LocalDateTime.now())
        .post(null)
        .build();

    Assertions.assertThat(comment).isNotNull();
  }
}