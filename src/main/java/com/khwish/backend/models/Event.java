package com.khwish.backend.models;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event extends Timestamp {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "location_lat")
    private Float locationLat;

    @Column(name = "location_long")
    private Float locationLong;

    @Column(name = "event_date")
    private Long eventDate;

    @Column(name = "closed")
    private boolean closed;

    public Event() {}

    public Event(UUID userId, String name, String description, Float locationLat, Float locationLong, Long eventDate) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
        this.eventDate = eventDate;
        this.closed = false;
    }

    public UUID getId() {
        return id;
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

    public Float getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(Float locationLat) {
        this.locationLat = locationLat;
    }

    public Float getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(Float locationLong) {
        this.locationLong = locationLong;
    }

    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}