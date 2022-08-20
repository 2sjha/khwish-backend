package com.khwish.backend.requests.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class UserLoginOAuthRequest implements Serializable {

    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("name")
    private String name;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    public UserLoginOAuthRequest() {
    }

    public String getIdToken() {
        return idToken;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
}
