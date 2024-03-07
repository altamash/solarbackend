package com.solar.api.saas.service.systemAttribute;

import com.solar.api.exception.NotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ESystemAttribute {

    SECRET_KEY("SECRET_KEY", "AES Symmetric Key"),
    PUBLIC_KEY("PUBLIC_KEY", "RSA Asymmetric Public Key"),
    PRIVATE_KEY("PRIVATE_KEY", "RSA Asymmetric Private Key");

    private String attributeKey;
    private String attributeDescription;

    ESystemAttribute(String attributeKey, String attributeDescription) {
        this.attributeKey = attributeKey;
        this.attributeDescription = attributeDescription;
    }

    public static ESystemAttribute get(String attributeKey) {
        return Arrays.stream(values()).filter(value -> attributeKey.equalsIgnoreCase(value.attributeKey)).findFirst()
                .orElseThrow(() -> new NotFoundException(ESystemAttribute.class, "attributeKey", attributeKey));
    }
}
