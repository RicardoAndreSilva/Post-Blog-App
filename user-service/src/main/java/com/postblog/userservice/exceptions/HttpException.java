package com.postblog.userservice.exceptions;

/**
 * Custom exception to represent HTTP errors with an associated status code.
 */
public class HttpException extends RuntimeException {

  private final int statusCode;

  public HttpException(String message, int statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
