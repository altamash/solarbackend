package com.solar.api.tenant.mapper.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityGroupTile {
    private Long entityGroupId;
    private String entityName;
    private String email;
    private String designation;
    private String contactNumber;
    private String employeeType;
    private Date joiningDate;
    private String imageURI;
    private Long entityRoleId;
    private Long entityId;

    public EntityGroupTile(Long entityGroupId, String entityName, String email, String designation, String contactNumber, String employeeType, Date joiningDate, String imageURI,
                            Long entityRoleId,Long entityId) {
        this.entityGroupId = entityGroupId;
        this.entityName = entityName;
        this.email = email;
        this.designation = designation;
        this.contactNumber = contactNumber;
        this.employeeType = employeeType;
        this.joiningDate = joiningDate;
        this.imageURI = imageURI;
        this.entityRoleId = entityRoleId;
        this.entityId = entityId;
    }
}
