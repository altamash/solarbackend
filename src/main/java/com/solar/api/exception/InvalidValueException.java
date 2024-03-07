package com.solar.api.exception;

import lombok.Getter;

@Getter
public class InvalidValueException extends SolarApiException {

    private Long id;

    public InvalidValueException(String field, String value) {
        super(value + " is not a valid " + field);
    }

    public InvalidValueException(String field) {
        super(field + " is required");
    }
}
