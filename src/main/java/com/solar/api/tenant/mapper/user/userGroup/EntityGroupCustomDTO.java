package com.solar.api.tenant.mapper.user.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityGroupCustomDTO {

    private List<EntityGroupDTO> entityGroupDTOList;
    private String refType;
    private String refId;
    private String projectId;
    private String workOrderName;


}
