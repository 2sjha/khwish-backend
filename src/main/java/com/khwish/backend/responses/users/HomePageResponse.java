package com.khwish.backend.responses.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.responses.events.EventDetailsResponse;

import java.util.ArrayList;

public class HomePageResponse extends BaseResponse {

    @JsonProperty("wallet_amount")
    private double walletAmount;

    @JsonProperty("events")
    private ArrayList<EventDetailsResponse> events;

    public HomePageResponse(Boolean success, String message, int statusCode) {
        super(success, message, statusCode);
    }

    public void setWalletAmount(double walletAmount) {
        this.walletAmount = walletAmount;
    }

    public void setEvents(ArrayList<EventDetailsResponse> events) {
        this.events = events;
    }
}
