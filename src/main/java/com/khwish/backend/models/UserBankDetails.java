package com.khwish.backend.models;


import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "user_bank_details")
public class UserBankDetails extends Timestamp {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "upi_vpa")
    private String upiVpa;

    @Column(name = "account_number")
    private Long accountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "account_holders_name")
    private String accountHoldersName;

    public UserBankDetails() {
    }

    public UserBankDetails(UUID userId, String upiVpa, Long accountNumber, String ifscCode, String accountHoldersName) {
        this.userId = userId;
        this.upiVpa = upiVpa;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.accountHoldersName = accountHoldersName;
    }

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

    public void setUpiVpa(String upiVpa) {
        this.upiVpa = upiVpa;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public void setAccountHoldersName(String accountHoldersName) {
        this.accountHoldersName = accountHoldersName;
    }
}
