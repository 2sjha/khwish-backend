package com.khwish.backend.requests.users;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WithdrawalRequest {

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("method")
    private String method;

    public double getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }
}
