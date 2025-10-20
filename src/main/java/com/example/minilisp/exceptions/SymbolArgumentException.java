package com.example.minilisp.exceptions;

public class SymbolArgumentException extends RuntimeException {
  public SymbolArgumentException(String message) {

    super("SymbolArgumentException" + message);
  }
}
