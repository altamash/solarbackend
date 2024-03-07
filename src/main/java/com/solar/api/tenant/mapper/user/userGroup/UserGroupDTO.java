package com.solar.api.tenant.mapper.user.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.userGroup.EntityGroup;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroupDTO {

    private Long id;
    private String userGroupName;
    private String userGroupType;
    private boolean status;
    private boolean isActive;
    private String refType;
    private String refId;
    private String parentId;
    private String createdBy;
    private String updatedBy;
    private Long userCount;

    private boolean isDeleted;

    public UserGroupDTO(Long id, String userGroupName, String userGroupType, boolean status,
                        boolean isActive, String refType, String refId, String parentId, String createdBy,
                        String updatedBy, Long userCount) {
        this.id = id;
        this.userGroupName = userGroupName;
        this.userGroupType = userGroupType;
        this.status = status;
        this.isActive = isActive;
        this.refType = refType;
        this.refId = refId;
        this.parentId = parentId;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.userCount = userCount;
    }
}
