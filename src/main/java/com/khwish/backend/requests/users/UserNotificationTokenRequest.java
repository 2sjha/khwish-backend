package com.khwish.backend.requests.users;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserNotificationTokenRequest {

    @JsonProperty("notification_token")
    private String notificationToken;

    public UserNotificationTokenRequest() {
    }

    public String getNotificationToken() {
        return notificationToken;
    }
}
