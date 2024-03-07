package com.solar.api.tenant.model.stage.monitoring;
import lombok.*;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantCustomerSubsDTO {
    private List<ExtDataStageDefinitionDTO> extDataStageDefinition;
    private String byProductCodes;
    private String byCustomerCodes;
}