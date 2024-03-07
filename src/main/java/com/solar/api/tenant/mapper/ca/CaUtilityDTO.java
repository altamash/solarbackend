package com.solar.api.tenant.mapper.ca;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaUtilityDTO {
    private Long id;
    private String accountHolderName;
    private Long utilityProviderId;
    private String premise;
    private Long averageMonthlyBill;
    private Long entityId;
    private LocalDateTime createdAt;
    private String referenceId;
    private List<PhysicalLocationDTO> physicalLocations;
    private  Boolean isChecked;
    private List<String> fileUrls;
    private Boolean isPrimary;
    private String utilityPortalName;
    private String passCode;
    private String portalAccessAllowed;
}
