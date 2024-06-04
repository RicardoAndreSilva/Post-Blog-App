package com.postblog.postservice.entities;


import java.time.LocalDateTime;
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
public class CommentResponse {

  private Long id;
  private String content;
  private Long authorId;
  private LocalDateTime createAt;
  private PostResponse post;
}
