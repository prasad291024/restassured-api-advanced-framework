package com.prasad_v.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Auth {
    @JsonProperty("username")
    private String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("password")
    private String password;
}
