package com.khwish.backend.responses.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khwish.backend.responses.base.BaseResponse;

import java.util.UUID;

public class LoginResponse extends BaseResponse {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("is_new_user")
    private boolean isNewUser;

    @JsonProperty("auth_token")
    private String authToken;

    @JsonProperty("auth_token_expires_at")
    private Long authTokenValidUpto;

    public LoginResponse(Boolean success, String message, int statusCode, UUID userId, String userName, boolean isNewUser, String authToken, Long authTokenValidUpto) {
        super(success, message, statusCode);
        this.userId = userId;
        this.isNewUser = isNewUser;
        this.userName = userName;
        this.authToken = authToken;
        this.authTokenValidUpto = authTokenValidUpto;
    }
}
