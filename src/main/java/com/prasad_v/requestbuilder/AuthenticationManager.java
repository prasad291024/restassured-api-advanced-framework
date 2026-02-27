package com.prasad_v.requestbuilder;

import com.prasad_v.auth.AuthenticationFactory;
import com.prasad_v.auth.BasicAuthHandler;
import com.prasad_v.auth.OAuthHandler;
import com.prasad_v.auth.TokenManager;
import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.logging.CustomLogger;

/**
 * AuthenticationManager handles various authentication methods for API requests.
 * It integrates with the auth package to provide Basic, OAuth, and Token authentication.
 */
public class AuthenticationManager {

    private static final CustomLogger logger = new CustomLogger(AuthenticationManager.class);
    private final ConfigurationManager configManager;
    private final BasicAuthHandler basicAuthHandler;
    private final OAuthHandler oAuthHandler;

    /**
     * Constructor initializes authentication components
     */
    public AuthenticationManager() {
        configManager = ConfigurationManager.getInstance();
        basicAuthHandler = (BasicAuthHandler) AuthenticationFactory.getAuthHandler(AuthenticationFactory.AuthType.BASIC);
        oAuthHandler = (OAuthHandler) AuthenticationFactory.getAuthHandler(AuthenticationFactory.AuthType.OAUTH);
    }

    /**
     * Get a cached token or generate a new one if expired
     *
     * @param tokenKey Key to identify the token type
     * @return Authentication token
     */
    public String getToken(String tokenKey) {
        String token = TokenManager.getToken(tokenKey);
        if (token == null || token.isEmpty()) {
            token = refreshToken(tokenKey);
        }
        return token;
    }

    /**
     * Force refresh a token
     *
     * @param tokenKey Key to identify the token type
     * @return New authentication token
     */
    public String refreshToken(String tokenKey) {
        logger.info("Refreshing token for key: " + tokenKey);

        String tokenEndpoint = firstNonBlank(
                configManager.getConfigProperty("auth." + tokenKey + ".endpoint", ""),
                configManager.getConfigProperty("auth." + tokenKey + ".token.url", ""),
                configManager.getConfigProperty("auth.token.url", "")
        );
        String clientId = firstNonBlank(
                configManager.getConfigProperty("auth." + tokenKey + ".clientId", ""),
                configManager.getConfigProperty("auth." + tokenKey + ".client.id", ""),
                configManager.getConfigProperty("auth.client.id", "")
        );
        String clientSecret = firstNonBlank(
                configManager.getConfigProperty("auth." + tokenKey + ".clientSecret", ""),
                configManager.getConfigProperty("auth." + tokenKey + ".client.secret", ""),
                configManager.getConfigProperty("auth.client.secret", "")
        );
        String scope = firstNonBlank(
                configManager.getConfigProperty("auth." + tokenKey + ".scope", ""),
                configManager.getConfigProperty("auth.scope", "")
        );

        if (tokenEndpoint.isEmpty()) {
            logger.error("Token endpoint not configured for key: " + tokenKey);
            return "";
        }

        oAuthHandler.setTokenUrl(tokenEndpoint);
        oAuthHandler.setCredentials(clientId, clientSecret, scope);
        oAuthHandler.setGrantType(OAuthHandler.GrantType.CLIENT_CREDENTIALS);
        String newToken = oAuthHandler.getAccessToken();
        if (newToken != null && !newToken.isEmpty()) {
            TokenManager.storeToken(tokenKey, newToken, 3600);
            logger.info("Token refreshed successfully for key: " + tokenKey);
        } else {
            logger.error("Failed to refresh token for key: " + tokenKey);
        }

        return newToken;
    }

    /**
     * Get Basic Auth header value
     *
     * @param username Username
     * @param password Password
     * @return Basic auth header value
     */
    public String getBasicAuthHeader(String username, String password) {
        logger.debug("Generating Basic Auth header for user: " + username);
        return basicAuthHandler.getAuthorizationHeader(username, password);
    }

    /**
     * Get Basic Auth header value from configuration
     *
     * @param authKey Key to identify auth configuration
     * @return Basic auth header value
     */
    public String getBasicAuthHeaderFromConfig(String authKey) {
        String username = firstNonBlank(
                configManager.getConfigProperty("auth." + authKey + ".username", ""),
                configManager.getConfigProperty("auth.username", "")
        );
        String password = firstNonBlank(
                configManager.getConfigProperty("auth." + authKey + ".password", ""),
                configManager.getConfigProperty("auth.password", "")
        );

        if (username.isEmpty() || password.isEmpty()) {
            logger.error("Basic auth credentials not found for key: " + authKey);
            return "";
        }

        logger.debug("Generating Basic Auth header from config for key: " + authKey);
        return basicAuthHandler.getAuthorizationHeader(username, password);
    }

    /**
     * Get an OAuth token header value
     *
     * @param tokenKey Key to identify the token type
     * @return OAuth token header value
     */
    public String getOAuthHeader(String tokenKey) {
        String token = getToken(tokenKey);
        if (token == null || token.isEmpty()) {
            logger.error("Failed to get OAuth token for key: " + tokenKey);
            return "";
        }

        if (!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        return token;
    }

    /**
     * Clear cached token
     *
     * @param tokenKey Key to identify the token type
     */
    public void clearToken(String tokenKey) {
        TokenManager.removeToken(tokenKey);
        logger.info("Cleared token cache for key: " + tokenKey);
    }

    /**
     * Clear all cached tokens
     */
    public void clearAllTokens() {
        TokenManager.clearAllTokens();
        logger.info("Cleared all token caches");
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
