package com.company.resourceapi.exceptions;

public class GeneralException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final String message;

  public GeneralException(String message) {
      this.message = message;
  }


  @Override
  public String getMessage() {
      return message;
  }
}