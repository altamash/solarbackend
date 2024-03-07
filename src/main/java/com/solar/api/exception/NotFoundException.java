package com.solar.api.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends SolarApiException {

    private Long id;
    private String sId;

    public NotFoundException(Class clazz, Long id) {
        super(clazz.getSimpleName() + " with id '" + id + "' not found.");
        this.id = id;
    }

    /**
     * For mongoDB IDs
     *
     * @param clazz
     * @param sId
     */
    public NotFoundException(Class clazz, String sId) {
        super(clazz.getSimpleName() + " with id '" + sId + "' not found.");
        this.sId = sId;
    }

    public NotFoundException(Class clazz, String name, String value) {
        super(clazz.getSimpleName() + " with '" + name + "' '" + value + "' not found.");
    }

    public NotFoundException(Class clazz1, String name, String value, Class clazz2, String name2, String value2) {
        super(clazz1.getSimpleName() + " with '" + name + "' '" + value + "' & " + clazz2.getSimpleName() +
                " with '" + name2 + "' '" + value2 + " not found.");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
