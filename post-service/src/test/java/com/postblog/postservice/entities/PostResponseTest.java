package com.postblog.postservice.entities;

import com.postblog.postservice.utils.EntityCreator;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostResponseTest {

  @Test
  @DisplayName("Test for equals() and hashCode()")
  void testEqualsAndHashCode_returnsOk_WhenSuccessful() {
    LocalDateTime now = LocalDateTime.now();
    Set<Category> categories = EnumSet.of(Category.ENTERTAINMENT);

    PostResponse post1 = new PostResponse(1L, "Title", "Content", "Author", categories, now);
    PostResponse post2 = new PostResponse(1L, "Title", "Content", "Author", categories, now);
    PostResponse post3 = new PostResponse(1L, "Test", "Content", "Author", categories, now);

    boolean equals1to2 = post1.equals(post2);
    boolean equals2to1 = post2.equals(post1);
    boolean equals1to3 = post1.equals(post3);
    boolean equals3to1 = post3.equals(post1);

    Assertions.assertThat(equals1to2 && equals2to1).isTrue();
    Assertions.assertThat(equals1to3 || equals3to1).isFalse();
    Assertions.assertThat(post1.hashCode()).hasSameHashCodeAs(post2.hashCode());
    Assertions.assertThat(post1.hashCode()).isNotEqualTo(post3.hashCode());
  }

  @Test
  @DisplayName("Test for toString()")
  void testToString_returnsOk_WhenSuccessful() {
    PostResponse post = EntityCreator.createValidSamplePost();

    String postExpected = post.toString();

    Assertions.assertThat(postExpected)
        .contains("1")
        .contains("Sample Post")
        .contains("This is test.")
        .contains(List.of("jUnit", "test"))
        .contains("1");
  }

  @Test
  @DisplayName("Test object creation using Lombok @Builder")
  void testObjectCreationUsingBuilder_ReturnsOk_WhenSuccessful() {
    PostResponse post = PostResponse.builder()
        .id(1L)
        .title("Title")
        .content("Content")
        .author(String.valueOf(2L))
        .build();

    Assertions.assertThat(post).isNotNull();
  }
}