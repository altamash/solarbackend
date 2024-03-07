package com.solar.api.tenant.mapper.subscription.subscriptionType;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionTypeDTO {

    private Long id;
    private String subscriptionName;
    private String primaryGroup;
    private String code;
    private Integer generateCycle;
    private Integer billingCycle;
    private String alias; //(optional)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> codes;

    public SubscriptionTypeDTO(Long id, String subscriptionName, String primaryGroup, String code, Integer generateCycle, Integer billingCycle, String alias, LocalDateTime createdAt, LocalDateTime updatedAt, List<String> codes) {
        this.id = id;
        this.subscriptionName = subscriptionName;
        this.primaryGroup = primaryGroup;
        this.code = code;
        this.generateCycle = generateCycle;
        this.billingCycle = billingCycle;
        this.alias = alias;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.codes = codes;
    }

    public SubscriptionTypeDTO(String primaryGroup, String code) {
        this.primaryGroup = primaryGroup;
        this.code = code;
    }

    public SubscriptionTypeDTO(String primaryGroup, List<String> codes) {
        this.primaryGroup = primaryGroup;
        this.codes = codes;
    }


}
