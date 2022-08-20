package com.khwish.backend.models;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "wallets")
public class Wallet extends Timestamp {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "amount")
    private double amount;

    public Wallet() {
    }

    public Wallet(UUID userId, double amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public UUID getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public boolean addAmount(double updateAmount) {
        if (updateAmount >= 0) {
            this.amount = this.amount + updateAmount;
            return true;
        } else {
            return false;
        }
    }

    public boolean deductAmount(double updateAmount) {
        if (updateAmount >= 0 && updateAmount <= this.amount) {
            this.amount = this.amount - updateAmount;
            return true;
        } else {
            return false;
        }
    }
}
