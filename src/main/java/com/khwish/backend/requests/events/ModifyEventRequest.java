package com.khwish.backend.requests.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ModifyEventRequest extends AddEventRequest {

    @JsonProperty("event_id")
    private UUID eventId;

    public ModifyEventRequest() {
    }

    public ModifyEventRequest(String name, String description, Long endDate, Float locationLat, Float locationLong, UUID eventId) {
        super(name, description, endDate, locationLat, locationLong);
        this.eventId = eventId;
    }

    public UUID getEventId() {
        return eventId;
    }
}