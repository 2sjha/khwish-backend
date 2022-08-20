package com.khwish.backend.requests.instamojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class CreatePaymentRequest {

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("goal_id")
    private UUID goalId;

    public CreatePaymentRequest() {
    }

    public double getAmount() {
        return amount;
    }

    public UUID getGoalId() {
        return goalId;
    }
}
