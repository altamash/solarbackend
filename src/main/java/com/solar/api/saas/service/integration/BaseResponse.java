package com.solar.api.saas.service.integration;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> implements Serializable {
    private int code;
    private String message;
    private T data;
    private List<String> messages;

    public static BaseResponse unprocessable(Object... arg) {
        return BaseResponse.builder()
                .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .message(arg.length > 0 ? (String) arg[0] : null).build();
    }

    public static BaseResponse ok(Object... arg) {
        return BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(arg.length > 0 ? (String) arg[0] : null).build();
    }

    public static BaseResponse ok(String message, Object data) {
        return BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .data(data)
                .message(message).build();
    }

    public static BaseResponse created(Object... arg) {
        return BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(arg.length > 0 ? (String) arg[0] : null).build();
    }

    public static BaseResponse notFound(Object... arg) {
        return BaseResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(arg.length > 0 ? (String) arg[0] : null).build();
    }

    public static BaseResponse error(Object... arg) {
        return BaseResponse.builder()
                .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .message(arg.length > 0 ? (String) arg[0] : null).build();
    }

    public static BaseResponse error(int code, String message) {
        return BaseResponse.builder()
                .code(code)
                .message(message).build();
    }
}
