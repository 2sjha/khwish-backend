package com.khwish.backend.responses.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khwish.backend.responses.base.BaseResponse;

import java.io.Serializable;
import java.util.ArrayList;

public class WalletActivitiesResponse extends BaseResponse {

    @JsonProperty("wallet_amount")
    private Double walletAmount;

    @JsonProperty("activities")
    private ArrayList<WalletActivity> activities;

    public WalletActivitiesResponse(Boolean success, String message, int statusCode, Double walletAmount,
                                    ArrayList<WalletActivity> activities) {
        super(success, message, statusCode);
        this.walletAmount = walletAmount;
        this.activities = activities;
    }

    public static class WalletActivity implements Serializable {

        // +1 for positive activity & -1 for negative activity
        @JsonProperty("type")
        private int activityType;

        @JsonProperty("amount")
        private Double amount;

        @JsonProperty("time")
        private Long time;

        @JsonProperty("message")
        private String message;

        public WalletActivity(int activityType, Double amount, Long time, String message) {
            this.activityType = activityType;
            this.amount = amount;
            this.time = time;
            this.message = message;
        }
    }
}
