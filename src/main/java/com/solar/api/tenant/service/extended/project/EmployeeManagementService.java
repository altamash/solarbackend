package com.solar.api.tenant.service.extended.project;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.mapper.contract.EntityDetailDTO;
import com.solar.api.tenant.mapper.extended.project.EmployeeManagementDTO;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.extended.FunctionalRoles;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface EmployeeManagementService {
   EmployeeManagementDTO saveEmployeeManagement(EmployeeManagementDTO employeeManagementDTO, Long compKey, MultipartFile profilePic, String createdBy);
   List<EmployeeManagementDTO> findAllEmployee(String entityType);

   List<EmployeeManagementDTO> findActiveEmployees(String entityType);
   EntityDetailDTO uploadToStorage(MultipartFile file, Long entityId, Long compKey) throws URISyntaxException, IOException, StorageException;
   EntityDetailDTO updateToStorage(MultipartFile file, Long entityId, Long compKey) throws URISyntaxException, IOException, StorageException;
   EntityDetailDTO findByEntityId(Long entityId);
   FunctionalRoles findFunctionalRolesById(Long id);
   Entity findByIdAndEntityType(Long id, String entityType);

   Entity findByEntity(Long id);

   EmployeeDetail findByEmployeeDetailByEntityId(Long id);

   Map findEmployeeDetails(Long entityId, String entityType,Map response);

   Map disableEmployee(Map response,Long entityId,boolean disabled);

   Map deleteEmployee(Map response,Long entityId);

}
