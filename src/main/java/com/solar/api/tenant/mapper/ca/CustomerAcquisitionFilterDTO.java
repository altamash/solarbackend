package com.solar.api.tenant.mapper.ca;

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

public class CustomerAcquisitionFilterDTO {
    private List<SalesRepresentativeDTO> salesRepresentativeDTOList;
    private List<String> status;
    private List<String> zipCodes;
}
