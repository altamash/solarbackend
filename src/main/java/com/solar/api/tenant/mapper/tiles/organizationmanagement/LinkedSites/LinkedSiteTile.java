package com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkedSiteTile {
    private String refId;
    private String gardenName;
    private String gardenType;
    private String gardenOwner;
    private String gardenRegistrationDate;
    private String gardenLiveDate;
    private String groupBy;

}
