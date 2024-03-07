package com.solar.api.tenant.mapper.tiles.organizationmanagement.office.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.user.SalesRepresentativeDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class PhysicalLocationOMFilterDTO {
    private List<String> locationCategory;
    private List<String> locationType;
    private List<String> businessUnit;
}
