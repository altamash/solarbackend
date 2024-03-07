package com.solar.api.tenant.mapper.contract;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationDetailDTO {

    private Long id;
    private String unitName;
    private String details;
    private Long parentId;
    private Long orgDetailId;
    private Long entityRoleId;
    private String entityName;
    private Long unitTypeId;
    private String parentName; //it should parent org id
    private String unitSubType; //not present in payload
    private PhysicalLocationDTO physicalLocationDto;
    private List<PhysicalLocationDTO> physicalLocationDTOList;

    //    private Site site;
//    private List<OrganizationDetail> organizationDetailList;
    private List<LinkedContractDTO> linkedContracts;
    private String logoImage;
    private String status;

    @Override
    public String toString() {
        return "OrganizationDetailDTO{" +
                "id=" + id +
                ", unitName='" + unitName + '\'' +
                ", details='" + details + '\'' +
                ", parentId=" + parentId +
                ", entityRoleId=" + entityRoleId +
                ", unitTypeId=" + unitTypeId +
                ", parentName='" + parentName + '\'' +
                ", unitSubType='" + unitSubType + '\'' +
                '}';
    }

}
