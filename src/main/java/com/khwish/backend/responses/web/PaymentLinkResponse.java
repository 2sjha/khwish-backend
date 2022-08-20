package com.khwish.backend.responses.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khwish.backend.responses.base.BaseResponse;

public class PaymentLinkResponse extends BaseResponse {

    @JsonProperty("payment_url")
    private String paymentUrl;

    public PaymentLinkResponse(Boolean success, String message, int statusCode, String paymentUrl) {
        super(success, message, statusCode);
        this.paymentUrl = paymentUrl;
    }
}
