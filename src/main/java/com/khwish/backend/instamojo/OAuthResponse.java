package com.khwish.backend.instamojo;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthResponse implements Serializable {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String bearerType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("error")
    private String error;

    public OAuthResponse() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
}
