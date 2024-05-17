package com.postblog.postservice.service;

import static com.postblog.postservice.utils.Constants.COMMENT_NOT_FOUND;
import static com.postblog.postservice.utils.Constants.FAILED_TO_CREATE_COMMENT;
import static com.postblog.postservice.utils.Constants.FAILED_TO_GET_COMMENTS;
import static com.postblog.postservice.utils.Constants.FAILED_TO_UPDATE_COMMENT;
import static com.postblog.postservice.utils.Constants.INTERNAL_SERVER_ERROR;
import static com.postblog.postservice.utils.Constants.NOT_FOUND;

import com.postblog.postservice.entities.CommentEntity;
import com.postblog.postservice.entities.CommentResponse;
import com.postblog.postservice.exceptions.HttpException;
import com.postblog.postservice.repository.CommentRepository;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service class for managing comments.
 */
@Service
public class CommentService {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  @Qualifier("modelMapperBeanComments")
  private ModelMapper mapper;

  /**
   * Retrieves a comment by its ID.
   *
   * @param commentId The ID of the comment to retrieve.
   * @return The response containing the requested comment.
   * @throws HttpException If the comment is not found.
   */
  public CommentResponse getCommentById(Long commentId) {
    Optional<CommentEntity> comment = commentRepository.findById(commentId);
    return comment.map(p -> mapper.map(p, CommentResponse.class))
        .orElseThrow(() -> new HttpException(COMMENT_NOT_FOUND, NOT_FOUND));
  }

  /**
   * Creates a new comment.
   *
   * @param commentEntity The comment entity to be created.
   * @throws HttpException If an error occurs while creating the comment.
   */

  public void createComment(CommentEntity commentEntity) {
    try {
      commentRepository.save(commentEntity);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_CREATE_COMMENT, INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Updates an existing comment by its ID.
   *
   * @param commentId       The ID of the comment to be updated.
   * @param commentToUpdate The updated comment entity.
   * @throws HttpException If the comment is not found or an error occurs while updating it.
   */

  public void updateCommentById(Long commentId, CommentEntity commentToUpdate) {
    CommentEntity comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new HttpException(COMMENT_NOT_FOUND, NOT_FOUND));

    comment.setContent(commentToUpdate.getContent());
    try {
      commentRepository.save(comment);
      mapper.map(comment, CommentResponse.class);
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_UPDATE_COMMENT, INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Retrieves all comments.
   *
   * @return The list of all comments.
   * @throws HttpException If an error occurs while retrieving the comments.
   */
  public List<CommentResponse> getAllComments() {
    try {
      List<CommentEntity> commentEntityList = commentRepository.findAll();
      return commentEntityList.stream().map(comments -> mapper.map(comments, CommentResponse.class))
          .toList();
    } catch (Exception e) {
      throw new HttpException(FAILED_TO_GET_COMMENTS, INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Deletes a comment by its ID.
   *
   * @param commentId The ID of the comment to be deleted.
   * @throws HttpException If the comment is not found.
   */

  public void deleteCommentById(Long commentId) {
    if (!commentRepository.existsById(commentId)) {
      throw new HttpException(COMMENT_NOT_FOUND, NOT_FOUND);
    }
    commentRepository.deleteById(commentId);
  }
}
