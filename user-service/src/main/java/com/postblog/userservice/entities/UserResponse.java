package com.postblog.userservice.entities;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

  private Long id;
  private String name;
  private String email;
  private Integer age;
  private String username;
  private String password;
  private LocalDateTime createdAt;
  private String createdBy;
}


