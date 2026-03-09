package com.enterpriseflowsrepository.camel.clients.oidc;

import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Empty trust manager to ignore SSL signatures.
 */
public class EmptyTrustManager extends X509ExtendedTrustManager {

  private static SSLContext context;

  /**
   * Get (or initialize) the empty SSL instance.
   * @return a non-null instance.
   */
  public static @NotNull SSLContext getEmptySSLContext() {
    try {
      if (context == null) {
        context = SSLContext.getInstance("SSL"); // OR TLS
        context.init(null, new TrustManager[]{new EmptyTrustManager()}, new SecureRandom());
      }
      return context;
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new RuntimeException("Could not initialize empty SSL context.", e);
    }
  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return new X509Certificate[0];
  }

  @Override
  public void checkClientTrusted(X509Certificate[] chain, String authType) {
    // empty method
  }

  @Override
  public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
    // empty method
  }

  @Override
  public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {
    // empty method
  }

  @Override
  public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {
    // empty method
  }

  @Override
  public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
    // empty method
  }

  @Override
  public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
    // empty method
  }
}