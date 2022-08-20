package com.khwish.backend.responses.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khwish.backend.responses.base.BaseResponse;

import java.util.UUID;

public class GoalThanksResponse extends BaseResponse {

    @JsonProperty("goal_name")
    private String goalName;

    @JsonProperty("event_id")
    private UUID eventId;

    @JsonProperty("event_creator_name")
    private String eventCreatorName;

    public GoalThanksResponse(Boolean success, String message, int statusCode, String goalName, UUID eventId, String eventCreatorName) {
        super(success, message, statusCode);
        this.goalName = goalName;
        this.eventId = eventId;
        this.eventCreatorName = eventCreatorName;
    }
}
