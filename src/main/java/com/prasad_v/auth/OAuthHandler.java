package com.prasad_v.auth;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import com.prasad_v.auth.AuthenticationFactory.IAuthHandler;
import com.prasad_v.logging.CustomLogger;
import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.exceptions.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles OAuth 2.0 authentication for API requests.
 * Supports client credentials flow, authorization code flow,
 * and pre-configured access tokens.
 */
public class OAuthHandler implements IAuthHandler {

    private static final CustomLogger logger = new CustomLogger(OAuthHandler.class);

    // OAuth credentials
    private String clientId;
    private String clientSecret;
    private String scope;

    // Token information
    private String accessToken;
    private String refreshToken;
    private long expiresAt = 0;

    // OAuth endpoints
    private String tokenUrl;
    private String authUrl;
    private String redirectUri;

    // Grant types
    public enum GrantType {
        CLIENT_CREDENTIALS,
        AUTHORIZATION_CODE,
        PASSWORD,
        REFRESH_TOKEN
    }

    private GrantType grantType = GrantType.CLIENT_CREDENTIALS;

    /**
     * Default constructor
     */
    public OAuthHandler() {
        // Try to get OAuth endpoints from configuration
        try {
            ConfigurationManager configManager = ConfigurationManager.getInstance();
            this.tokenUrl = configManager.getProperty("oauth.token.url");
            this.authUrl = configManager.getProperty("oauth.auth.url");
            this.redirectUri = configManager.getProperty("oauth.redirect.uri");
        } catch (Exception e) {
            logger.warn("OAuth URLs not found in configuration. Please set them manually.");
        }
    }

    /**
     * Constructor with client credentials
     *
     * @param clientId OAuth client ID
     * @param clientSecret OAuth client secret
     * @param scope OAuth scope (can be null)
     */
    public OAuthHandler(String clientId, String clientSecret, String scope) {
        this();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
    }

    /**
     * Sets the credentials for OAuth authentication
     *
     * @param clientId OAuth client ID
     * @param clientSecret OAuth client secret
     * @param scope OAuth scope (can be null)
     */
    public void setCredentials(String clientId, String clientSecret, String scope) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
    }

    /**
     * Sets the grant type for OAuth flow
     *
     * @param grantType The OAuth grant type to use
     */
    public void setGrantType(GrantType grantType) {
        this.grantType = grantType;
    }

    /**
     * Sets the OAuth token URL
     *
     * @param tokenUrl The URL to request tokens from
     */
    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    /**
     * Sets the OAuth authorization URL (for authorization code flow)
     *
     * @param authUrl The authorization URL
     */
    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    /**
     * Sets the redirect URI (for authorization code flow)
     *
     * @param redirectUri The redirect URI after authorization
     */
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    /**
     * Directly sets an access token (skips the token request)
     *
     * @param accessToken The OAuth access token
     * @param expiresInSeconds Token expiration in seconds (from now)
     */
    public void setAccessToken(String accessToken, long expiresInSeconds) {
        this.accessToken = accessToken;
        this.expiresAt = System.currentTimeMillis() + (expiresInSeconds * 1000);
        TokenManager.storeToken("oauth_access_token", accessToken, expiresInSeconds);
    }

    /**
     * Sets a refresh token for obtaining new access tokens
     *
     * @param refreshToken The OAuth refresh token
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        TokenManager.storeToken("oauth_refresh_token", refreshToken, -1); // -1 means no expiration
    }

    /**
     * Adds OAuth authentication to the request specification
     *
     * @param requestSpec The request specification to enhance with OAuth authentication
     * @return The enhanced request specification
     */
    @Override
    public RequestSpecification addAuth(RequestSpecification requestSpec) {
        ensureValidAccessToken();
        logger.debug("Adding OAuth Bearer token to request");
        return requestSpec.header("Authorization", "Bearer " + accessToken);
    }

    /**
     * Ensures a valid access token is available, requesting a new one if needed
     */
    private void ensureValidAccessToken() {
        // If we have no token or it's expired (with 30s buffer)
        if (accessToken == null || System.currentTimeMillis() > (expiresAt - 30000)) {
            logger.debug("Access token missing or expired. Requesting new token.");

            // Try to get from token manager first
            accessToken = TokenManager.getToken("oauth_access_token");
            if (accessToken != null && !TokenManager.isTokenExpired("oauth_access_token")) {
                logger.debug("Using cached access token from TokenManager");
                expiresAt = TokenManager.getTokenExpiry("oauth_access_token");
                return;
            }

            // If we have a refresh token, try to use it
            if (refreshToken != null) {
                try {
                    refreshAccessToken();
                    return;
                } catch (Exception e) {
                    logger.warn("Failed to refresh token: " + e.getMessage());
                    // Fall through to request new token
                }
            }

            // Request brand new token
            requestNewAccessToken();
        }
    }

    /**
     * Requests a new access token using client credentials flow
     */
    private void requestNewAccessToken() {
        validateOAuthConfig();

        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", clientId);
        formParams.put("client_secret", clientSecret);

        switch (grantType) {
            case CLIENT_CREDENTIALS:
                formParams.put("grant_type", "client_credentials");
                if (scope != null && !scope.isEmpty()) {
                    formParams.put("scope", scope);
                }
                break;

            case PASSWORD:
                formParams.put("grant_type", "password");
                ConfigurationManager configManager = ConfigurationManager.getInstance();
                String username = configManager.getProperty("oauth.username");
                String password = configManager.getProperty("oauth.password");
                formParams.put("username", username);
                formParams.put("password", password);
                if (scope != null && !scope.isEmpty()) {
                    formParams.put("scope", scope);
                }
                break;

            default:
                throw new AuthenticationException("Unsupported grant type for direct token request: " + grantType);
        }

        sendTokenRequest(formParams);
    }

    /**
     * Refreshes an access token using a refresh token
     */
    private void refreshAccessToken() {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AuthenticationException("No refresh token available");
        }

        validateOAuthConfig();

        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", "refresh_token");
        formParams.put("refresh_token", refreshToken);
        formParams.put("client_id", clientId);
        formParams.put("client_secret", clientSecret);

        sendTokenRequest(formParams);
    }

    /**
     * Sends a token request to the token endpoint
     *
     * @param formParams The form parameters for the token request
     */
    private void sendTokenRequest(Map<String, String> formParams) {
        try {
            logger.info("Requesting OAuth token from: " + tokenUrl);

            Response response = RestAssured.given()
                    .contentType("application/x-www-form-urlencoded")
                    .formParams(formParams)
                    .post(tokenUrl);

            int statusCode = response.getStatusCode();
            if (statusCode != 200) {
                String errorBody = response.getBody().asString();
                logger.error("OAuth token request failed. Status code: " + statusCode + ", Response: " + errorBody);
                throw new AuthenticationException("Failed to obtain OAuth token. Status code: " + statusCode);
            }

            // Parse response
            accessToken = response.jsonPath().getString("access_token");

            // Get expiry if available
            Object expiresIn = response.jsonPath().get("expires_in");
            long expirySeconds = 3600; // Default to 1 hour
            if (expiresIn != null) {
                if (expiresIn instanceof Integer) {
                    expirySeconds = (Integer) expiresIn;
                } else if (expiresIn instanceof String) {
                    expirySeconds = Long.parseLong((String) expiresIn);
                }
            }

            // Set expiry time
            expiresAt = System.currentTimeMillis() + (expirySeconds * 1000);

            // Store in token manager
            TokenManager.storeToken("oauth_access_token", accessToken, expirySeconds);

            // Check for refresh token
            String newRefreshToken = response.jsonPath().getString("refresh_token");
            if (newRefreshToken != null && !newRefreshToken.isEmpty()) {
                this.refreshToken = newRefreshToken;
                TokenManager.storeToken("oauth_refresh_token", refreshToken, -1); // No expiry for refresh tokens
            }

            logger.info("Successfully obtained OAuth token. Expires in " + expirySeconds + " seconds");

        } catch (Exception e) {
            logger.error("Error requesting OAuth token: " + e.getMessage(), e);
            throw new AuthenticationException("Failed to obtain OAuth token: " + e.getMessage(), e);
        }
    }

    /**
     * Validates that the required OAuth configuration is available
     */
    private void validateOAuthConfig() {
        if (tokenUrl == null || tokenUrl.isEmpty()) {
            throw new AuthenticationException("OAuth token URL not configured");
        }

        if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
            throw new AuthenticationException("OAuth client credentials not configured");
        }
    }

    /**
     * Gets the current access token
     *
     * @return The OAuth access token
     */
    public String getAccessToken() {
        ensureValidAccessToken();
        return accessToken;
    }

    /**
     * Backward-compatible alias for getAccessToken().
     */
    public String getToken() {
        return getAccessToken();
    }

    /**
     * Generates an authorization URL for authorization code flow
     *
     * @param state Optional state parameter for CSRF protection
     * @return The authorization URL
     */
    public String getAuthorizationUrl(String state) {
        if (authUrl == null || authUrl.isEmpty()) {
            throw new AuthenticationException("OAuth authorization URL not configured");
        }

        if (clientId == null || clientId.isEmpty()) {
            throw new AuthenticationException("OAuth client ID not configured");
        }

        if (redirectUri == null || redirectUri.isEmpty()) {
            throw new AuthenticationException("OAuth redirect URI not configured");
        }

        StringBuilder url = new StringBuilder(authUrl);
        url.append("?response_type=code");
        url.append("&client_id=").append(clientId);
        url.append("&redirect_uri=").append(redirectUri);

        if (scope != null && !scope.isEmpty()) {
            url.append("&scope=").append(scope);
        }

        if (state != null && !state.isEmpty()) {
            url.append("&state=").append(state);
        }

        return url.toString();
    }

    /**
     * Exchanges an authorization code for tokens (authorization code flow)
     *
     * @param code The authorization code received from the auth server
     */
    public void exchangeAuthorizationCode(String code) {
        validateOAuthConfig();

        if (redirectUri == null || redirectUri.isEmpty()) {
            throw new AuthenticationException("OAuth redirect URI not configured");
        }

        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", "authorization_code");
        formParams.put("code", code);
        formParams.put("redirect_uri", redirectUri);
        formParams.put("client_id", clientId);
        formParams.put("client_secret", clientSecret);

        sendTokenRequest(formParams);
    }
}
