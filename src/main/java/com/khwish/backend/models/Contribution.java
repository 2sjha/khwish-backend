package com.khwish.backend.models;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "contributions")
public class Contribution extends Timestamp {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "contributor_name")
    private String contributorName;

    @Column(name = "goal_id")
    private UUID goalId;

    @Column(name = "amount")
    private double amount;

    public Contribution() {
    }

    public Contribution(String contributorName, UUID goalId, double amount) {
        this.contributorName = contributorName;
        this.goalId = goalId;
        this.amount = amount;
    }

    public String getContributorName() {
        return contributorName;
    }

    public double getAmount() {
        return amount;
    }

    public UUID getGoalId() {
        return goalId;
    }
}