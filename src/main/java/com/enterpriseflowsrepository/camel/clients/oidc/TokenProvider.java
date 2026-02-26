package com.enterpriseflowsrepository.camel.clients.oidc;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for providing access tokens to be used in API calls. <br/>
 * Implementations can include caching logic to avoid unnecessary token retrievals.
 */
public interface TokenProvider {

  /**
   * Retrieves a valid access token. Implementations may cache the token and refresh it as needed.
   * @return A non-null access token string to be used in API calls.
   */
  @NotNull String getAccessToken();

}
