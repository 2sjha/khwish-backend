package com.khwish.backend.responses.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse implements Serializable {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonIgnore
    private Integer statusCode;

    public BaseResponse() {
    }

    public BaseResponse(Boolean success, String message, int statusCode) {
        this.success = success;
        this.message = message;
        this.statusCode = statusCode;
    }

    public Boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}