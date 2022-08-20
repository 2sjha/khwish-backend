package com.khwish.backend.instamojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("user")
    private String user;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("buyer_name")
    private String buyerName;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("purpose")
    private String purpose;

    @JsonProperty("status")
    private String status;

    @JsonProperty("payments")
    private List<Object> payments = null;

    @JsonProperty("send_sms")
    private Boolean sendSms;

    @JsonProperty("send_email")
    private Boolean sendEmail;

    @JsonProperty("sms_status")
    private String smsStatus;

    @JsonProperty("email_status")
    private String emailStatus;

    @JsonProperty("shorturl")
    private Object shorturl;

    @JsonProperty("longurl")
    private String longurl;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    @JsonProperty("webhook")
    private String webhook;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("modified_at")
    private String modifiedAt;

    @JsonProperty("resource_uri")
    private String resourceUri;

    @JsonProperty("allow_repeated_payments")
    private Boolean allowRepeatedPayments;

    @JsonProperty("mark_fulfilled")
    private Boolean markFulfilled;

    public PaymentResponse() {
    }

    public String getLongurl() {
        return longurl;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", amount='" + amount + '\'' +
                ", purpose='" + purpose + '\'' +
                ", status='" + status + '\'' +
                ", payments=" + payments +
                ", sendSms=" + sendSms +
                ", sendEmail=" + sendEmail +
                ", smsStatus='" + smsStatus + '\'' +
                ", emailStatus='" + emailStatus + '\'' +
                ", shorturl=" + shorturl +
                ", longurl='" + longurl + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", webhook='" + webhook + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", modifiedAt='" + modifiedAt + '\'' +
                ", resourceUri='" + resourceUri + '\'' +
                ", allowRepeatedPayments=" + allowRepeatedPayments +
                ", markFulfilled=" + markFulfilled +
                '}';
    }
}