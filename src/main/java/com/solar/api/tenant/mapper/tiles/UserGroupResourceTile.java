package com.solar.api.tenant.mapper.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroupResourceTile {
    private Long userGroupId;
    private String userGroupName;
    private Long resourcesCount;
    private boolean status;
    private List<EntityGroupTile> entityGroupTiles;

}
