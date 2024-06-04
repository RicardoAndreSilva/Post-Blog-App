package com.postblog.postservice.service;

import static com.postblog.postservice.utils.Constants.FAILED_TO_CREATE_POST;
import static com.postblog.postservice.utils.Constants.FAILED_TO_GET_POSTS;
import static com.postblog.postservice.utils.Constants.FAILED_TO_UPDATE_POST;
import static com.postblog.postservice.utils.Constants.INTERNAL_SERVER_ERROR;
import static com.postblog.postservice.utils.Constants.NOT_FOUND;
import static com.postblog.postservice.utils.Constants.POST_NOT_FOUND;

import com.postblog.postservice.entities.PostEntity;
import com.postblog.postservice.entities.PostResponse;
import com.postblog.postservice.exceptions.HttpException;
import com.postblog.postservice.repository.PostRepository;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for operations related to posts.
 */
@Service
public class PostService {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  @Qualifier("modelMapperBeanPost")
  private ModelMapper mapper;

  /**
   * Get a post by its ID.
   *
   * @param id The ID of the post.
   * @return The corresponding post.
   * @throws HttpException if the post is not found.
   */
  public PostResponse getPostById(Long id) {
    Optional<PostEntity> post = postRepository.findById(id);

    return post.map(p -> mapper.map(p, PostResponse.class))
        .orElseThrow(() -> new HttpException(POST_NOT_FOUND, NOT_FOUND));
  }

  /**
   * Create a new post.
   *
   * @param postEntity The post to be created.
   * @throws HttpException if the post already exists.
   */

  public void createPost(PostEntity postEntity) {
    try {
      postRepository.save(postEntity);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_CREATE_POST, INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Update a post by its ID.
   *
   * @param postId       The ID of the post to be updated.
   * @param postToUpdate The updated data of the post.
   * @throws HttpException if the post is not found or cannot be updated.
   */
  @Transactional
  public void updatePostById(Long postId, PostEntity postToUpdate) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new HttpException(POST_NOT_FOUND, NOT_FOUND));

    post.setAuthor(postToUpdate.getAuthor());
    post.setCategories(postToUpdate.getCategories());
    post.setTitle(postToUpdate.getTitle());
    post.setLastModifiedDate(postToUpdate.getLastModifiedDate());
    post.setContent(postToUpdate.getContent());

    try {
      postRepository.save(post);
      mapper.map(post, PostResponse.class);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_UPDATE_POST, INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get all posts.
   *
   * @return A list of all posts.
   * @throws HttpException if there is a failure to get the posts.
   */
  public List<PostResponse> getAllPosts() {
    try {
      List<PostEntity> postList = postRepository.findAll();
      return postList.stream().map(post -> mapper.map(post, PostResponse.class))
          .toList();
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_GET_POSTS, INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Delete a post by its ID.
   *
   * @param postId The ID of the post to be deleted.
   * @throws HttpException if the post is not found.
   */

  public void deletePostById(Long postId) {
    if (!postRepository.existsById(postId)) {
      throw new HttpException(POST_NOT_FOUND, NOT_FOUND);
    }
    postRepository.deleteById(postId);
  }
}
