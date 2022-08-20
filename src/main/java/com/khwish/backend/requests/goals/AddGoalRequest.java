package com.khwish.backend.requests.goals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddGoalRequest implements Serializable {

    @JsonProperty("event_id")
    private UUID eventId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    //TODO - collected amount not needed during goal creation, or maybe can be a feature if user has already collected some amount for it.
    @JsonProperty("collected_amount")
    private Integer collectedAmount;

    @JsonProperty("total_amount")
    private Integer totalAmount;

    public AddGoalRequest() {
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCollectedAmount() {
        return collectedAmount;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setCollectedAmount(Integer collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }
}