package com.postblog.userservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Data
@Builder
@ToString
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Size(min = 3)
  @Column(name = "name")
  private String name;

  @Email(message = "Email invalid format", regexp = "^[a-zA-Z0-9.+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "age")
  private Integer age;

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private LocalDateTime createdAt;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @LastModifiedDate
  @Column(name = "last_modified_at", columnDefinition = "TIMESTAMP")
  private LocalDateTime lastModifiedAt;

  @LastModifiedBy
  @Column(name = "last_modified_by")
  private String lastModifiedBy;

  @Column(name = "registered", nullable = false)
  private boolean registered;

  @Column(name = "roles")
  private Set<String> roles = new HashSet<>();
}
