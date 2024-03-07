package com.solar.api.tenant.mapper.tiles;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityFunctionalRoleTile {
    private Long entityRoleId;
    private Long entityId;
    private Long functionalRoleId;
    private String functionalRoleName;
    private String entityName;
    private String category;
    private String email;
    private String imageUri;

    private Long acctId;

    public EntityFunctionalRoleTile(Long entityRoleId, Long entityId, Long functionalRoleId, String functionalRoleName, String entityName, String category, String email, String imageUri) {
        this.entityRoleId = entityRoleId;
        this.entityId = entityId;
        this.functionalRoleId = functionalRoleId;
        this.functionalRoleName = functionalRoleName;
        this.entityName = entityName;
        this.category = category;
        this.email = email;
        this.imageUri = imageUri;
    }
    public EntityFunctionalRoleTile(Long entityRoleId, Long entityId, String entityName, String imageUri, Long acctId) {
        this.entityRoleId = entityRoleId;
        this.entityId = entityId;
        this.entityName = entityName;
        this.imageUri = imageUri;
        this.acctId = acctId;
    }
}
