package com.solar.api.tenant.mapper.extended.project.list;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListProcessDTO {
    private Long id;
    private Long listId;
    private String listName;
    private String category;//assetlist,checklist,contract
    private String subCategory;
    private Boolean enabled;
    private String notes;
    private String assignedTo;
    private Long roleId;
    private String rolesW;
}
