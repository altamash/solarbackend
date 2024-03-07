package com.solar.api.tenant.mapper.workOrder;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoCustomerDetailWoDTO {
    private String subId;
    private String status;
    private String subLocation; //It is maging_physical_loc_id in mongo document
    private String siteLocation; //It is site_physical_locId in mongo document
    private String subName; //It is Product Name in mongo document
    private String subAlias; //It is Variant Alias in mongo document
    private String accountId;
    private Long entityId;
    private String entityName;
    private String firstName;
    private String lastName;
    private String region;
    private String customerType;
    private String profileUrl;
    private String variantId;
    private String variantName; //It is Variant Name in mongo document
    private String createdAt; //It is Created at date of measure CN in mongo document
    private String defaultValue; //It is Subscription start date (S_SSDT) in mongo document
    private String subValueCN; //It is Subscription Name in mongo document
    private String gardenSrc;
    private String premiseNo;
}

