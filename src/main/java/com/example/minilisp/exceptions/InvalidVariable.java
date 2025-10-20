package com.example.minilisp.exceptions;

public class InvalidVariable extends RuntimeException {

  public InvalidVariable(String message) {
    super("Variable: " + message + " is invalid");
  }
}
