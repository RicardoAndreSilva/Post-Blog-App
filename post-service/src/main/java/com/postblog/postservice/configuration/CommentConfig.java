package com.postblog.postservice.configuration;

import com.postblog.postservice.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommentConfig {

  @Bean
  public ModelMapper modelMapperBeanComments() {
    return new ModelMapper();
  }

  @Bean
  public CommentService commentServiceBean() {
    return new CommentService();
  }
}

