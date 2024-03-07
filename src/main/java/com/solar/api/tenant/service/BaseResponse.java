package com.solar.api.tenant.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> implements Serializable {

    private int code;
    private String message;
    private T data;
    private T response;
    private T errors;

    public BaseResponse(int code, String message, T data, T response, T errors) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.response = response;
        this.errors = errors;
    }

    public BaseResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}