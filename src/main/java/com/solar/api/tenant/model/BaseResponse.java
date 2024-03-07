package com.solar.api.tenant.model;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class BaseResponse<T> implements Serializable {

    private Integer code;
    private String msg;
    private String message;
    private T data;


    public BaseResponse(String msg ,T data){
        this.msg = msg;
        this.data = data;
    }

    public BaseResponse(Integer code,String msg ,T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BaseResponse(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

}
