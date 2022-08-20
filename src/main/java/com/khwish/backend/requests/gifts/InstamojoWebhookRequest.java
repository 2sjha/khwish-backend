package com.khwish.backend.requests.gifts;

import org.springframework.web.bind.annotation.ModelAttribute;

public class InstamojoWebhookRequest {

    private Double amount;
    private String buyer;
    private String buyer_name;
    private String buyer_phone;
    private String currency;
    private Double fees;
    private String longurl;
    private String mac;
    private String payment_id;
    private String payment_request_id;
    private String purpose;
    private String shorturl;
    private String status;

    public Double getAmount() {
        return amount;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getBuyer_name() {
        return buyer_name;
    }

    public String getBuyer_phone() {
        return buyer_phone;
    }

    public String getCurrency() {
        return currency;
    }

    public Double getFees() {
        return fees;
    }

    public String getLongurl() {
        return longurl;
    }

    public String getMac() {
        return mac;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public String getPayment_request_id() {
        return payment_request_id;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getShorturl() {
        return shorturl;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "InstamojoFormRequest{" +
                "amount=" + amount +
                ", buyer='" + buyer + '\'' +
                ", buyer_name='" + buyer_name + '\'' +
                ", buyer_phone='" + buyer_phone + '\'' +
                ", currency='" + currency + '\'' +
                ", fees=" + fees +
                ", longurl='" + longurl + '\'' +
                ", mac='" + mac + '\'' +
                ", payment_id='" + payment_id + '\'' +
                ", payment_request_id='" + payment_request_id + '\'' +
                ", purpose='" + purpose + '\'' +
                ", shorturl='" + shorturl + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
