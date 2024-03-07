package com.solar.api.exception;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data @Component
public class ResponseObject {
    private String code;     // Return Code for decision making
    private boolean status;  // Return Status for decision making
    private String message;  //Return Message if required
    private Object data;    // Return Response Object


    public String responseObjectMethod() {
        return "API Response{" +
                "code='" + code + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public String responseObjectlevelCheck() {
        return "levelCheck Response{" +
                "code='" + code + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}