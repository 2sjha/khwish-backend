package com.khwish.backend.responses.users;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.khwish.backend.responses.base.BaseResponse;

public class UserProfileResponse extends BaseResponse {

    @JsonProperty("user_details")
    private UserDetails userDetails;

    @JsonProperty("bank_details")
    private BankDetails bankDetails;

    public UserProfileResponse(Boolean success, String message, int statusCode, UserDetails userDetails, BankDetails bankDetails) {
        super(success, message, statusCode);
        this.userDetails = userDetails;
        this.bankDetails = bankDetails;
    }

    public static class UserDetails {

        @JsonProperty("name")
        private String name;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone_number")
        private String phoneNumber;

        @JsonProperty("date_of_birth")
        private Long dateOfBirth;

        @JsonProperty("registered_via")
        private String registeredVia;

        public UserDetails(String name, String email, String phoneNumber, Long dateOfBirth, String registeredVia) {
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.dateOfBirth = dateOfBirth;
            this.registeredVia = registeredVia;
        }
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

        public BankDetails(String upiVpa, Long accountNumber, String ifscCode, String accountHoldersName) {
            this.upiVpa = upiVpa;
            this.accountNumber = accountNumber;
            this.ifscCode = ifscCode;
            this.accountHoldersName = accountHoldersName;
        }
    }
}
