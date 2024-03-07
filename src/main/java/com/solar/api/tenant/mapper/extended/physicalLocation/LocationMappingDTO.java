package com.solar.api.tenant.mapper.extended.physicalLocation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationMappingDTO {

    private Long id;
    //private Long siteId;
    private Long locationId;
    private String primaryInd;
    private String status;
    private PhysicalLocationDTO physicalLocation;
    private Long sourceId;
    private String sourceType;

}
