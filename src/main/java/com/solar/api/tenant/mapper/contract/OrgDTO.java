package com.solar.api.tenant.mapper.contract;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgDTO {

    private OrgDetailDTO orgDetailDTO;
    private List<PhysicalLocationDTO> physicalLocationDTOList;

}
