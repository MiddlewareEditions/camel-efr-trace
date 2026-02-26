package com.enterpriseflowsrepository.camel.exceptions;

import com.enterpriseflowsrepository.api.traces.ApiException;
import jakarta.ws.rs.ProcessingException;
import org.jetbrains.annotations.NotNull;

/**
 * Custom exception to wrap API-related exceptions in the EFR Camel application.
 */
public class EfrApiException extends RuntimeException {

  /**
   * Constructs a new exception with a message derived from the provided {@link ApiException}.
   * @param exception The ApiException that caused this exception.
   */
  public EfrApiException(@NotNull ApiException exception) {
    super("Runtime API exception: " + exception.getResponse().getStatus(), exception);
  }

  /**
   * Constructs a new exception with a message derived from the provided {@link ProcessingException}.
   * @param exception The ProcessingException that caused this exception.
   */
  public EfrApiException(@NotNull ProcessingException exception) {
    super("Processing API exception", exception);
  }

}
