package com.solar.api.saas.mapper.attribute.systemAttribute;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemAttributeDTO implements Serializable {

    private Long id;
    private String attribute; //varchar(20) PK
    private String attributeDescription; //varchar(125)
    private String attributeKey; //varchar(45) PK
    private String attributeValue; //varchar(45) PK
    private byte[] attributeValueLob; //varchar(45) PK
    private String parentAttribute; //varchar(45)
    private String attributeType; //varchar(45)
    private String referenceObject; //varchar(45)
}
