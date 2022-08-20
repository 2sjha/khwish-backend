package com.khwish.backend.models;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "goals")
public class Goal extends Timestamp {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "collected_amount")
    private double collectedAmount;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "closed")
    private boolean closed;

    private Goal() {
    }

    public Goal(UUID userId, UUID eventId, String name, String description, int collectedAmount, int totalAmount) {
        this.userId = userId;
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.collectedAmount = collectedAmount;
        this.totalAmount = totalAmount;
        this.closed = false;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
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

    public double getCollectedAmount() {
        return collectedAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCollectedAmount(double collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}