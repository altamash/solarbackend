package com.solar.api.tenant.mapper.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationResponseDTO {
    public Long id;
    public String name;
    public String type;
    public Long subType;
    public Integer noOfUnits;
    public String status;
    public List<UnitDTO> units;
}
