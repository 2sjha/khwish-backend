package com.khwish.backend.requests.goals;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.UUID;

public class CloseGoalRequest implements Serializable {

    @JsonProperty("event_id")
    private UUID eventId;

    @JsonProperty("goal_id")
    private UUID goalId;

    public CloseGoalRequest() {
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getGoalId() {
        return goalId;
    }
}