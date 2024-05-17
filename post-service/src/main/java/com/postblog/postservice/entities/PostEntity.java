package com.postblog.postservice.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
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
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts_db")
public class PostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "title")
  @NotNull
  @Size(max = 100)
  private String title;

  @Column(name = "content")
  @NotNull
  @Size(max = 1000)
  private String content;

  @Column(name = "author")
  @NotNull
  private String author;

  @ElementCollection(targetClass = Category.class)
  @Enumerated(EnumType.STRING)
  private Set<Category> categories;

  @Column(name = "create_at")
  @CreatedDate
  private LocalDateTime createAt;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String lastModifiedBy;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime lastModifiedDate;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "post", cascade = CascadeType.ALL)
  private List<CommentEntity> comments;
}
