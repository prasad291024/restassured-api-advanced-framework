package com.prasad_v.auth;

/**
 * Factory class for creating different types of authentication handlers.
 * This class follows the Factory design pattern to provide appropriate
 * authentication handler based on the specified authentication type.
 */
public class AuthenticationFactory {

    /**
     * Authentication types supported by the factory
     */
    public enum AuthType {
        BASIC,
        OAUTH,
        API_KEY,
        BEARER_TOKEN,
        NO_AUTH
    }

    /**
     * Creates and returns an appropriate authentication handler based on the specified type
     *
     * @param authType The type of authentication required
     * @return An implementation of IAuthHandler interface
     */
    public static IAuthHandler getAuthHandler(AuthType authType) {
        switch (authType) {
            case BASIC:
                return new BasicAuthHandler();
            case OAUTH:
                return new OAuthHandler();
            case BEARER_TOKEN:
                return new BearerTokenHandler();
            case API_KEY:
                return new ApiKeyAuthHandler();
            case NO_AUTH:
            default:
                return new NoAuthHandler();
        }
    }

    /**
     * Backward-compatible resolver that accepts string auth type names.
     *
     * @param authType Authentication type as string (e.g. "oauth", "basic")
     * @return Matching auth handler, or NoAuthHandler for unknown values
     */
    public static IAuthHandler getAuthHandler(String authType) {
        if (authType == null || authType.trim().isEmpty()) {
            return getAuthHandler(AuthType.NO_AUTH);
        }

        switch (authType.trim().toLowerCase()) {
            case "basic":
                return getAuthHandler(AuthType.BASIC);
            case "oauth":
                return getAuthHandler(AuthType.OAUTH);
            case "api_key":
            case "apikey":
            case "api-key":
                return getAuthHandler(AuthType.API_KEY);
            case "bearer":
            case "bearer_token":
            case "bearer-token":
                return getAuthHandler(AuthType.BEARER_TOKEN);
            default:
                return getAuthHandler(AuthType.NO_AUTH);
        }
    }

    /**
     * Creates and returns a basic authentication handler with provided credentials
     *
     * @param username Username for basic authentication
     * @param password Password for basic authentication
     * @return BasicAuthHandler with pre-configured credentials
     */
    public static BasicAuthHandler getBasicAuthHandler(String username, String password) {
        BasicAuthHandler handler = new BasicAuthHandler();
        handler.setCredentials(username, password);
        return handler;
    }

    /**
     * Creates and returns an OAuth authentication handler with provided token
     *
     * @param clientId Client ID for OAuth authentication
     * @param clientSecret Client Secret for OAuth authentication
     * @param scope OAuth scope requested
     * @return OAuthHandler with pre-configured credentials
     */
    public static OAuthHandler getOAuthHandler(String clientId, String clientSecret, String scope) {
        OAuthHandler handler = new OAuthHandler();
        handler.setCredentials(clientId, clientSecret, scope);
        return handler;
    }

    /**
     * Interface for all authentication handlers
     */
    public interface IAuthHandler {
        /**
         * Adds authentication details to a request
         *
         * @param requestBuilder The request builder to add authentication to
         * @return Updated request builder with authentication applied
         */
        io.restassured.specification.RequestSpecification addAuth(io.restassured.specification.RequestSpecification requestBuilder);
    }

    /**
     * Implementation for API Key authentication
     */
    private static class ApiKeyAuthHandler implements IAuthHandler {
        private String apiKey;
        private String headerName = "X-API-Key";

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        @Override
        public io.restassured.specification.RequestSpecification addAuth(io.restassured.specification.RequestSpecification requestBuilder) {
            return requestBuilder.header(headerName, apiKey);
        }
    }

    /**
     * Implementation for Bearer Token authentication
     */
    private static class BearerTokenHandler implements IAuthHandler {
        private String token;

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public io.restassured.specification.RequestSpecification addAuth(io.restassured.specification.RequestSpecification requestBuilder) {
            return requestBuilder.header("Authorization", "Bearer " + token);
        }
    }

    /**
     * Implementation for no authentication
     */
    private static class NoAuthHandler implements IAuthHandler {
        @Override
        public io.restassured.specification.RequestSpecification addAuth(io.restassured.specification.RequestSpecification requestBuilder) {
            return requestBuilder; // No authentication needed
        }
    }
}
