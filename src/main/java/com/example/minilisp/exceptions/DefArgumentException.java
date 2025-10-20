package com.example.minilisp.exceptions;

public class DefArgumentException extends RuntimeException {
  public DefArgumentException(String message) {
    super(
            "DefArgumentException" + message);
  }
}
