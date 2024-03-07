package com.solar.api.saas.service.integration.mongo.response.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solar.api.tenant.model.subscription.SubscriptionType;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class Variant {

    @JsonProperty("_id")
    private Id id;
    private String title;
    @JsonProperty("product_group")
    private Group productGroup;
    private String name;
    //    private String version;
    private String status;
    @JsonProperty("last_update_by")
    private String lastUpdateBy;
    private String category;
    @JsonProperty("code")
    private String code;
    @JsonProperty("billing_model")
    private String billingModel;
    @JsonProperty("charging_code")
    private String chargingCode;
    @JsonProperty("billing_cycle") // equal to generate_cycle in subscription_type
    private Integer billingCycle; // TODO: Should be Integer
    @JsonProperty("billing_frequency")
    private Integer billingFrequency; // TODO: Should be Integer
    @JsonProperty("max_tenure")
    private String maxTenure;
    @JsonProperty("default_validation_rule_group")
    private String defaultValidationRuleGroup;
    @JsonProperty("tax_group")
    private String taxGroup;
    @JsonProperty("prepayment_code")
    private String prepaymentCode;
    private String description;
    @JsonProperty("variant_name")
    private String variantName;
    @JsonProperty("variant_alias")
    private String variantAlias;
    private Measure measures;
    @JsonProperty("last_update_date")
    private Date lastUpdateDate;
    @JsonProperty("pre_generate")
    private Boolean preGenerate;
    @JsonProperty("parser_code")
    private String parserCode;
    @JsonProperty("downloaded_product_date")
    private Date downloadedProductDate;
    @JsonProperty("base_platform_product_id")
    private BasePlatformProductId basePlatformProductId;
    //    @JsonProperty("data_blocks")
//    List<DataBlock> dataBlocks;
//    @JsonProperty("subscription_id")
//    private String subscriptionId;
    @JsonProperty("variant_active")
    private Boolean variantActive;

    @JsonProperty("site_physical_locId")
    private Integer sitePhysicalLocId;
    @JsonProperty("maging_physical_loc_id")
    private Integer magingPhysicalLocId;
    private String type;

    static SubscriptionType toSubscriptionType(Variant variant) {
        return SubscriptionType.builder()
                .collectionId(variant.id.getOid())
                .subscriptionName(variant.name)
                .primaryGroup(variant.code)
                .code(variant.code)
                .generateCycle(variant.billingCycle)
                .billingCycle(variant.billingFrequency)
                .alias(variant.description)
                .build();
    }

    public static List<SubscriptionType> toSubscriptionTypes(List<Variant> variants) {
        return variants.stream().map(v -> toSubscriptionType(v)).collect(Collectors.toList());
    }
}
