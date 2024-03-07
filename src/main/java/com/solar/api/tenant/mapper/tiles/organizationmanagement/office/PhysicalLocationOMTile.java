package com.solar.api.tenant.mapper.tiles.organizationmanagement.office;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhysicalLocationOMTile {
    private Long locationId;
    private Long orgId;
    private Boolean isLeaf;
    private String locationName;
    private String locationCategory;
    private String locationType;
    private String businessUnit;
    private String timeZone;
    private String address;
    private String contactPersonName;
    private String contactPersonImage;
    private String groupBy;


}