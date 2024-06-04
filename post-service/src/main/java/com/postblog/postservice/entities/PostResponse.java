package com.postblog.postservice.entities;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

  private Long id;
  private String title;
  private String content;
  private String author;
  private Set<Category> categories;
  private LocalDateTime createAt;

}

