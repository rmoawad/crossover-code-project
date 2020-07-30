package com.company.resourceapi.exceptions;

import org.slf4j.helpers.MessageFormatter;

public class NotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final String message;

  public NotFoundException(String message) {
    this.message = message;
  }

  public NotFoundException(Class class1, long id) {
    this.message = MessageFormatter.format("Can't find entity for {} with id:{} ", class1.getSimpleName(), id)
        .getMessage();
  }

  @Override
  public String getMessage() {
    return message;
  }
}