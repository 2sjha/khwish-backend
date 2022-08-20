package com.khwish.backend.requests.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class UserOnBoardingRequest {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("date_of_birth")
    private Long dateOfBirth;

    @JsonProperty("bank_details")
    private BankDetails bankDetails;

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getDateOfBirth() {
        return dateOfBirth;
    }

    public BankDetails getBankDetails() {
        return bankDetails;
    }

    public static class BankDetails {

        @JsonProperty("upi_vpa")
        private String upiVpa;

        @JsonProperty("account_number")
        private Long accountNumber;

        @JsonProperty("ifsc_code")
        private String ifscCode;

        @JsonProperty("account_holders_name")
        private String accountHoldersName;

        public String getUpiVpa() {
            return upiVpa;
        }

        public Long getAccountNumber() {
            return accountNumber;
        }

        public String getIfscCode() {
            return ifscCode;
        }

        public String getAccountHoldersName() {
            return accountHoldersName;
        }
    }
}
