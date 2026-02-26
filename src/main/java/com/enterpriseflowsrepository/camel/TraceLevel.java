package com.enterpriseflowsrepository.camel;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Type of trace level.
 */
public enum TraceLevel {

  /** Normal INFO trace. */
  INFO,

  /** Signal a non-blocking issue : WARNING trace. */
  WARNING,

  /** Signal a blocking issue : ERROR trace. */
  ERROR,

  /** Signal a finalized, successful operation : SUCCESS trace. */
  SUCCESS;

  /**
   * Convert a string to a TraceLevel enum value. The comparison is case-insensitive.
   * @param level The string representation of the trace level (e.g., "info", "warning", "error", "success").
   * @return The corresponding TraceLevel enum value.
   * @throws IllegalArgumentException if the input string does not match any TraceLevel value.
   */
  public static @NotNull TraceLevel fromString(@NotNull String level) {
    for (TraceLevel traceLevel : TraceLevel.values()) {
      if (traceLevel.name().equalsIgnoreCase(level)) {
        return traceLevel;
      }
    }
    throw new IllegalArgumentException("Invalid trace level: '" + level + "'. Expected one of: info, warning, error, success.");
  }

  /**
   * Get the expected EFR format.
   * @return a EFR-friendly level format.
   */
  public @NotNull String toStatus() {
    return name().toLowerCase();
  }

}
