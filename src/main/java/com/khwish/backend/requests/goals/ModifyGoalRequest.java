package com.khwish.backend.requests.goals;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ModifyGoalRequest extends AddGoalRequest {

    @JsonProperty("goal_id")
    private UUID goalId;

    public ModifyGoalRequest() {
    }

    public UUID getGoalId() {
        return goalId;
    }
}