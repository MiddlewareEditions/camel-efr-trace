package com.enterpriseflowsrepository.camel;

import org.jetbrains.annotations.NotNull;
import java.util.Set;

/**
 * Type of trace level.
 */
public enum TraceLevel {

  /** Normal INFO trace. */
  INFO("info", "information", "i", "normal", "nominal"),

  /** Signal a non-blocking issue : WARNING trace. */
  WARNING("warning", "warn", "avertissement", "w"),

  /** Signal a blocking issue : ERROR trace. */
  ERROR("error", "erreur", "fatal", "err", "danger", "e"),

  /** Signal a finalized, successful operation : SUCCESS trace. */
  SUCCESS("success", "good", "s");

  private final Set<String> aliases;

  TraceLevel(@NotNull String @NotNull... aliases) {
    this.aliases = Set.of(aliases);
  }

  /**
   * Convert a string to a TraceLevel enum value. The comparison is case-insensitive.
   * @param level The string representation of the trace level (e.g., "info", "warning", "error", "success").
   * @return The corresponding TraceLevel enum value.
   * @throws IllegalArgumentException if the input string does not match any TraceLevel value.
   */
  public static @NotNull TraceLevel fromString(@NotNull String level) {
    for (TraceLevel traceLevel : TraceLevel.values()) {
      if (traceLevel.aliases.contains(level.toLowerCase())) {
        return traceLevel;
      }
    }
    throw new IllegalArgumentException("Invalid trace level: '" + level + "'. Expected one of: info, warning, error, success.");
  }

  /**
   * Get the expected EFR format.
   * @return an EFR-friendly level format.
   */
  public @NotNull String toStatus() {
    return name().toLowerCase();
  }

}
