package com.postblog.postservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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
import org.springframework.web.util.HtmlUtils;

@Data
@Builder
@ToString
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment_db")
@EntityListeners(AuditingEntityListener.class)
public class CommentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "content")
  @Size(max = 1000)
  @NotNull
  private String content;

  @Column(name = "author_id")
  private Long authorId;

  @Column(name = "create_at")
  @CreatedDate
  private LocalDateTime createAt;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String lastModifiedBy;

  @LastModifiedDate
  private LocalDateTime lastModifiedDate;

  @ManyToOne
  @NotNull
  @JoinColumn(name = "post_id", referencedColumnName = "id")
  private PostEntity post;

  public void sanitizeContent() {
    if (this.content != null) {
      this.content = HtmlUtils.htmlEscape(this.content);
    }
  }
}
