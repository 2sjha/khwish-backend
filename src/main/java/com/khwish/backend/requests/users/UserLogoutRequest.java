package com.khwish.backend.requests.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.UUID;

public class UserLogoutRequest implements Serializable {

    @JsonProperty("user_id")
    private UUID userId;

    public UUID getUserId() {
        return userId;
    }
}