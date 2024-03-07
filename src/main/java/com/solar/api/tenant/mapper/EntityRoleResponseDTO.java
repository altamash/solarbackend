package com.solar.api.tenant.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityRoleResponseDTO {
    public Long entityRoleId;
    public Long entityId;
    public Long functionalRoleId;
    public String entityName;
    public String functionalRoleName;
    public String uri;
}
