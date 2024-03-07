package com.solar.api.tenant.mapper.tiles.workorder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BusinessUnitInfoTile {
    private String entityName;
    private String phoneNumber;
    private String email;
    private String uri;
    private String managerName;

    public BusinessUnitInfoTile(String entityName, String phoneNumber, String email, String uri, String managerName) {
        this.entityName = entityName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.uri = uri;
        this.managerName = managerName;
    }
}
