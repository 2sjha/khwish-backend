package com.khwish.backend.requests.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddEventRequest implements Serializable {

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

    public AddEventRequest() {
    }

    public AddEventRequest(String name, String description, Long eventDate, Float locationLat, Float locationLong) {
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }

    public Float getLocationLat() {
        return locationLat;
    }

    public Float getLocationLong() {
        return locationLong;
    }
}