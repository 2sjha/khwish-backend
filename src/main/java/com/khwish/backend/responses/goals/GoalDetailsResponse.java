package com.khwish.backend.responses.goals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khwish.backend.responses.base.BaseResponse;

import java.util.UUID;

public class GoalDetailsResponse extends BaseResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("collected_amount")
    private double collectedAmount;

    @JsonProperty("total_amount")
    private double totalAmount;

    @JsonProperty("created_at")
    private Long createdAt;


    public GoalDetailsResponse(UUID id, String name, String description, double collectedAmount, double totalAmount, Long createdAt) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.collectedAmount = collectedAmount;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }
}