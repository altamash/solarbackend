package com.solar.api.tenant.mapper.extended.project;

import com.solar.api.tenant.mapper.contract.EntityDTO;
import com.solar.api.tenant.mapper.extended.document.DocuLibraryDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.mapper.user.userGroup.EntityRoleDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeManagementDTO {

    private EntityDTO entityDTO;
    private EmployeeDetailDTO employeeDetailDTO;
    private UserDTO userDTO;
    private PhysicalLocationDTO physicalLocationDTO;
    private DocuLibraryDTO docuLibraryDTO;
    private String profileURL;
    private List<EntityRoleDTO> entityRoleDTOs;
    private Long roleCount;
}
