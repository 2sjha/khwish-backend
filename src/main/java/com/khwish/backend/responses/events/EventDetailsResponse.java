package com.khwish.backend.responses.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khwish.backend.responses.goals.GoalDetailsResponse;
import com.khwish.backend.responses.base.BaseResponse;

import java.util.ArrayList;
import java.util.UUID;

public class EventDetailsResponse extends BaseResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("event_date")
    private Long eventDate;

    @JsonProperty("location_lat")
    private Float locationLat;

    @JsonProperty("location_long")
    private Float locationLong;

    @JsonProperty("collected_amount")
    private double collectedAmount;

    @JsonProperty("total_amount")
    private double totalAmount;

    @JsonProperty("invite_message")
    private String inviteMessage;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("goals_details")
    private ArrayList<GoalDetailsResponse> goalDetails;

    public EventDetailsResponse(Boolean success, String message, int statusCode, UUID id, String name, String description,
                                Long eventDate, Float locationLat, Float locationLong, double collectedAmount, double totalAmount,
                                String inviteMessage, Long createdAt, ArrayList<GoalDetailsResponse> goalDetails) {
        super(success, message, statusCode);
        this.id = id;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
        this.collectedAmount = collectedAmount;
        this.totalAmount = totalAmount;
        this.inviteMessage = inviteMessage;
        this.createdAt = createdAt;
        this.goalDetails = goalDetails;
    }
}