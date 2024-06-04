package com.postblog.postservice.configuration;

import com.postblog.postservice.service.CommentService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CommentConfigTest {

  @Test
  @DisplayName("Test modelMapperBean creation")
  void testModelMapperBeanCreationPosts_ReturnsOk_WhenSuccessful() {
    CommentConfig commentConfig = new CommentConfig();
    ModelMapper modelMapper = commentConfig.modelMapperBeanComments();

    Assertions.assertThat(modelMapper).isNotNull();
  }

  @Test
  @DisplayName("Test commentBean creation")
  void testCommentBeanCreation_ReturnsOk_WhenSuccessful() {
    CommentConfig commentConfig = new CommentConfig();
    CommentService commentService = commentConfig.commentServiceBean();

    Assertions.assertThat(commentService).isNotNull();
  }
}
