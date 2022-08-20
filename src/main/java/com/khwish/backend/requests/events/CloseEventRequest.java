package com.khwish.backend.requests.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.UUID;

public class CloseEventRequest implements Serializable {

    @JsonProperty("event_id")
    private UUID eventId;

    public CloseEventRequest() {
    }

    public UUID getEventId() {
        return eventId;
    }
}