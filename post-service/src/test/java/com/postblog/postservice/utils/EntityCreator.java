package com.postblog.postservice.utils;

import com.postblog.postservice.entities.Category;
import com.postblog.postservice.entities.CommentEntity;
import com.postblog.postservice.entities.CommentResponse;
import com.postblog.postservice.entities.PostEntity;
import com.postblog.postservice.entities.PostResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

public class EntityCreator {

  public static PostEntity createSamplePost() {
    return PostEntity.builder()
        .id(1L)
        .title("Sample Post")
        .content("This is a test.")
        .categories(Collections.singleton(Category.TECHNOLOGY))
        .author("jUnit 5")
        .createAt(LocalDate.now().atStartOfDay())
        .build();
  }

  public static PostEntity createSamplePost2() {
    return PostEntity.builder()
        .id(2L)
        .title("Sample Post")
        .content("This is test.")
        .categories(Collections.singleton(Category.TECHNOLOGY))
        .author("jUnit 5")
        .createAt(LocalDate.now().atStartOfDay())
        .build();
  }

  public static PostResponse createValidSamplePost() {
    return PostResponse.builder()
        .id(1L)
        .title("Sample Post")
        .content("This is test.")
        .categories(Collections.singleton(Category.TECHNOLOGY))
        .author("jUnit 5")
        .build();
  }

  public static CommentResponse createValidTestComment() {
    return CommentResponse.builder()
        .id(1L)
        .content("This is test.")
        .authorId(1L)
        .createAt(LocalDateTime.now())
        .build();
  }

  public static CommentEntity createSampleCommentToBeSaved() {
    PostEntity post = EntityCreator.createSamplePostToBeSaved();
    return CommentEntity.builder()
        .id(1L)
        .content("This is test.")
        .createAt(LocalDateTime.now())
        .authorId(1L)
        .post(post)
        .build();
  }

  public static CommentEntity createSampleCommentToBeSaved2() {
    PostEntity post = EntityCreator.createSamplePostToBeSaved();
    return CommentEntity.builder()
        .content("This is a test.")
        .createAt(LocalDateTime.now())
        .authorId(1L)
        .post(post)
        .build();
  }


  public static PostEntity createSamplePostToBeSaved() {
    PostEntity post = new PostEntity();
    post.setId(1L);
    post.setAuthor("junit");
    post.setContent("test");
    post.setTitle("jUnit");
    return post;
  }
}
