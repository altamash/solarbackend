package com.solar.api.tenant.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditResponse {

    private Long id;
    private long revision;
    private String userName;
    private String fullName;
    private String clientIp;
    private String operation;
    private List<PropertyValue> changedProperties;
    private long timestamp;
    private String dateTime;
//    private Object object;

    @Getter
    @Setter
//    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PropertyValue {
        private String propertyName;
        //        private String formattedPropertyName;
        private Object value;
        private Object previousValue;
    }
}
