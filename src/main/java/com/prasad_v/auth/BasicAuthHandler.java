package com.prasad_v.auth;

import io.restassured.specification.RequestSpecification;
import com.prasad_v.auth.AuthenticationFactory.IAuthHandler;
import com.prasad_v.logging.CustomLogger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Handles Basic Authentication for API requests.
 * Basic Authentication encodes username and password in base64 format
 * and sends it in the Authorization header.
 */
public class BasicAuthHandler implements IAuthHandler {

    private static final CustomLogger logger = new CustomLogger(BasicAuthHandler.class);

    private String username;
    private String password;
    private boolean isPreemptive = true;

    /**
     * Default constructor
     */
    public BasicAuthHandler() {
        // Default constructor
    }

    /**
     * Constructor with credentials
     *
     * @param username Username for basic authentication
     * @param password Password for basic authentication
     */
    public BasicAuthHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Sets the credentials for basic authentication
     *
     * @param username Username for basic authentication
     * @param password Password for basic authentication
     */
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Sets whether the basic authentication should be preemptive
     *
     * @param isPreemptive If true, sends auth header without waiting for 401 challenge
     */
    public void setPreemptive(boolean isPreemptive) {
        this.isPreemptive = isPreemptive;
    }

    /**
     * Gets the current username
     *
     * @return The username set for basic authentication
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the current password
     *
     * @return The password set for basic authentication
     */
    public String getPassword() {
        return password;
    }

    /**
     * Adds basic authentication to the request specification
     *
     * @param requestSpec The request specification to enhance with basic auth
     * @return The enhanced request specification
     */
    @Override
    public RequestSpecification addAuth(RequestSpecification requestSpec) {
        if (username == null || password == null) {
            logger.error("Basic authentication credentials not set. Username or password is null.");
            throw new IllegalStateException("Username and password must be set for Basic Authentication");
        }

        logger.debug("Adding Basic Authentication for user: " + username);

        if (isPreemptive) {
            return requestSpec.auth().preemptive().basic(username, password);
        } else {
            return requestSpec.auth().basic(username, password);
        }
    }

    /**
     * Validates if the credentials are set
     *
     * @return true if both username and password are set, false otherwise
     */
    public boolean isCredentialsSet() {
        return username != null && !username.isEmpty() && password != null;
    }

    /**
     * Returns a Basic authorization header for the provided credentials.
     */
    public String getAuthorizationHeader(String user, String pass) {
        String token = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes(StandardCharsets.UTF_8));
        return "Basic " + token;
    }

    /**
     * Clears the current credentials
     */
    public void clearCredentials() {
        this.username = null;
        this.password = null;
    }
}
