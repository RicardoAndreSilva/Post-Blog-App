package com.postblog.postservice.entities;

import static java.util.Optional.empty;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.postblog.postservice.repository.PostRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class PostEntityTest {

  @InjectMocks
  private PostEntity postEntity;
  @Mock
  private PostRepository postRepository;

  @BeforeEach
  public void setUp() {
    LocalDateTime now = LocalDateTime.now();
    Set<Category> categories = EnumSet.of(Category.ENTERTAINMENT);
    PostEntity postEntity = new PostEntity(1L, "Test post", "Content of the post", "Author1",
        categories, now, "CreatorName", "LastModifierName", now, null
    );
  }

  @Test
  @DisplayName("Test for save post when successful")
  void testSavePost_returnsOk_WhenSuccessful() {
    when(postRepository.save(postEntity)).thenReturn(postEntity);

    PostEntity savedPost = postRepository.save(postEntity);

    Assertions.assertThat(postEntity.getTitle()).isEqualTo(savedPost.getTitle());
    Assertions.assertThat(postEntity.getContent()).isEqualTo(savedPost.getContent());
    Assertions.assertThat(postEntity.getAuthor()).isEqualTo(savedPost.getAuthor());
    Assertions.assertThat(postEntity.getId()).isEqualTo(savedPost.getId());
    Assertions.assertThat(postEntity.getCreateAt()).isEqualTo(savedPost.getCreateAt());
  }

  @Test
  @DisplayName("Test for update post when successful")
  void testUpdatePost_returnsOk_WhenSuccessful() {
    postEntity.setTitle("Updated post");

    when(postRepository.save(postEntity)).thenReturn(postEntity);

    PostEntity updatedPost = postRepository.save(postEntity);

    Assertions.assertThat(postEntity.getTitle()).isEqualTo(updatedPost.getTitle());
  }

  @Test
  @DisplayName("Test for get post by id when successful")
  void testFindByIdPost_returnsOk_WhenSuccessful() {
    when(postRepository.findById(postEntity.getId())).thenReturn(Optional.of(postEntity));

    Optional<PostEntity> foundPost = postRepository.findById(postEntity.getId());

    Assertions.assertThat(postEntity.getId()).isEqualTo(foundPost.get().getId());
  }

  @Test
  @DisplayName("Test for get post by id returns empty optional when post not found")
  void testFindByIdPost_returnsEmptyOptional_WhenPostNotFound() {
    when(postRepository.findById(anyLong())).thenReturn(empty());

    Optional<PostEntity> foundPost = postRepository.findById(5L);

    Assertions.assertThat(foundPost).isEmpty();
  }

  @Test
  @DisplayName("Test for delete post when successful")
  void testDeletePost_returnsOk_WhenSuccessful() {
    postRepository.delete(postEntity);

    Optional<PostEntity> deletedPost = postRepository.findById(postEntity.getId());

    Assertions.assertThat(deletedPost).isNotPresent();
  }

  @Test
  @DisplayName("Test for get all posts when successful")
  void testGetAllPosts_returnsOk_WhenSuccessful() {
    LocalDateTime now = LocalDateTime.now();
    Set<Category> categories = EnumSet.of(Category.ENTERTAINMENT);
    PostEntity postEntity2 = new PostEntity(1L, "Test post", "Content of the post", "Author1",
        categories, now, "CreatorName", "LastModifierName", now, null
    );

    when(postRepository.findAll()).thenReturn(Arrays.asList(postEntity, postEntity2));

    List<PostEntity> posts = postRepository.findAll();

    Assertions.assertThat(posts).hasSize(2);
    Assertions.assertThat(postEntity.getTitle()).isEqualTo(posts.get(0).getTitle());
    Assertions.assertThat(postEntity2.getTitle()).isEqualTo(posts.get(1).getTitle());
  }

  @Test
  @DisplayName("Test for equals() and hashCode()")
  void testEqualsAndHashCode_returnsOk_WhenSuccessful() {
    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    Set<Category> categories = EnumSet.of(Category.ENTERTAINMENT);
    PostEntity post1 = new PostEntity(1L, "Test post", "Content of the post", "Author1",
        categories, now, "CreatorName", "LastModifierName", now, null
    );
    PostEntity post2 = new PostEntity(1L, "Test post", "Content of the post", "Author1",
        categories, now, "CreatorName", "LastModifierName", now, null
    );

    PostEntity post3 = new PostEntity(2L, "Test post", "Content of the post", "Author1",
        categories, now, "CreatorName", "LastModifierName", now, null
    );

    Assertions.assertThat(post1).isEqualTo(post2);
    Assertions.assertThat(post1.hashCode()).hasSameHashCodeAs(post2.hashCode());

    Assertions.assertThat(post1).isNotEqualTo(post3);
    Assertions.assertThat(post1.hashCode()).isNotEqualTo(post3.hashCode());
  }

  @Test
  @DisplayName("Test for toString()")
  void testToString_returnsOk_WhenSuccessful() {
    LocalDateTime now = LocalDateTime.now();
    Set<Category> categories = EnumSet.of(Category.ENTERTAINMENT);
    PostEntity post = new PostEntity(1L, "Test post", "Content of the post", "Author1",
        categories, now, "CreatorName", "LastModifierName", now, null
    );
    Assertions.assertThat(post.toString())
        .contains("PostEntity")
        .contains("id=1")
        .contains("title=Test post")
        .contains("content=Content of the post")
        .contains("author=Author1")
        .contains(
            "createAt=" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @Test
  @DisplayName("Test object creation using Lombok @Builder")
  void testObjectCreationUsingBuilder_ReturnsOk_WhenSuccessful() {
    PostEntity post = PostEntity.builder()
        .id(1L)
        .title("Title")
        .content("Content")
        .author("Author")
        .createAt(LocalDateTime.now())
        .build();

    Assertions.assertThat(post).isNotNull();
  }
}
