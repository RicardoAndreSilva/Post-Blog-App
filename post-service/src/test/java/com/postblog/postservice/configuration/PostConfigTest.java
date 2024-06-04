package com.postblog.postservice.configuration;

import com.postblog.postservice.service.PostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class PostConfigTest {

  @Autowired
  private PostConfig postConfig;

  @Test
  @DisplayName("Test postBean creation")
  void testPostBeanCreation_ReturnsOk_WhenSuccessful() {
    PostService postService = postConfig.postServiceBean();

    Assertions.assertThat(postService).isNotNull();
  }

  @Test
  @DisplayName("Test modelMapperBean creation")
  void testModelMapperBeanCreationPosts_ReturnsOk_WhenSuccessful() {
    ModelMapper modelMapper = postConfig.modelMapperBeanPost();

    Assertions.assertThat(modelMapper).isNotNull();
  }
}