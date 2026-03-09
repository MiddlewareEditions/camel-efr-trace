package com.enterpriseflowsrepository.camel.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Custom exception to wrap API-related exceptions in the EFR Camel application.
 */
public class EfrApiException extends RuntimeException {

  /**
   * Constructs a new exception.
   * @param exception The exception that caused this exception.
   */
  public EfrApiException(@NotNull Throwable exception) {
    super("Runtime API exception.", exception);
  }

  /**
   * New exception from an HTTP status.
   * @param status The HTTP status code that caused the exception.
   */
  public EfrApiException(int status) {
    super("EFR API exception. Unexpected status code: " + status);
  }

}
