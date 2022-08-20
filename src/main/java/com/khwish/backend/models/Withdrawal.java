package com.khwish.backend.models;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "withdrawals")
public class Withdrawal extends Timestamp {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "method")
    private String method;

    public Withdrawal() {
    }

    public Withdrawal(UUID userId, double amount, String method) {
        this.userId = userId;
        this.amount = amount;
        this.method = method;
        this.paid = false;
    }

    public double getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }
}
