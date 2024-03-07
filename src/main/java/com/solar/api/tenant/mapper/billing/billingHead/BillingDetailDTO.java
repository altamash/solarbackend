package com.solar.api.tenant.mapper.billing.billingHead;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingDetailDTO {

    private Long id;
    private Long billingHeadId;
    private String rateCode;
    private Double value;
    private Integer lineSeqNo;
    private PortalAttributeValueTenantDTO portalAttribute;
    private String billingCode;
    private Date date;
    private Boolean addToBillAmount;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String productionMonth;
    private String description;
    private String lineAmount;
    private String kwhsValue;
}
