package com.solar.api;

import com.solar.api.tenant.model.APIResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Data
@Component
public class ResponseEntityResult {

    public ResponseEntity<?> responseEntity(APIResponse responseObject) {
        switch (responseObject.getCode()) {
            case 200:
                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(responseObject);
            case 204:
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .contentType(MediaType.APPLICATION_JSON).body(responseObject);
            case 400:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON).body(responseObject);
            case 401:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON).body(responseObject);
            case 403:
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON).body(responseObject);
            case 404:
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON).body(responseObject);
            case 500:
                responseObject.setData(null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON).body(responseObject);
            default:
                responseObject.setCode(0);
                responseObject.setData(null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON).body(responseObject);
        }
    }

}