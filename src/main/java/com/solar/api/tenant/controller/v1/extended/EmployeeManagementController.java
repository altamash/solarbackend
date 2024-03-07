package com.solar.api.tenant.controller.v1.extended;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.ResponseEntityResult;
import com.solar.api.tenant.mapper.contract.EntityDetailDTO;
import com.solar.api.tenant.mapper.extended.project.EmployeeManagementDTO;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.service.extended.project.EmployeeManagementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RequiredArgsConstructor
@RestController("EmployeeManagementController")
@RequestMapping(value = "/employeeManagement")
public class EmployeeManagementController {
    private final ResponseEntityResult responseEntityResult;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmployeeManagementService employeeManagementService;

    /**
     * Description: Api for saving and updating employee
     * Created By: Iraj
     * Updated By: Ibtehaj
     *
     * @param employeeManagementDTO
     * @param profilePic
     * @param createdBy
     * @param compKey
     * @return
     */
    @PostMapping("/save")
    public ObjectNode addEmployeeManagement(@RequestParam(value = "employeeManagementDTO", required = true) String employeeManagementDTO,
                                            @RequestParam(value = "profilePic", required = false) MultipartFile profilePic,
                                            @RequestParam(value = "createdBy", required = false) String createdBy,
                                            @RequestHeader("Comp-Key") Long compKey) {
        EmployeeManagementDTO employeeMang = null;
        try {
            employeeMang = new ObjectMapper().readValue(employeeManagementDTO, EmployeeManagementDTO.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        employeeManagementService.saveEmployeeManagement(employeeMang, compKey, profilePic, createdBy);
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        if (employeeMang.getEntityDTO().getId() != null) {
            messageJson.put("message", "Employee has been updated.");
        } else {
            messageJson.put("message", "Employee has been created.");
        }
        return messageJson;
    }

    @GetMapping("/findAllEmployee/{entityType}")
    public List<EmployeeManagementDTO> findAllEmployee(@PathVariable String entityType) {
        return employeeManagementService.findAllEmployee(entityType);
    }

    @GetMapping("/findActiveEmployees/{entityType}")
    public List<EmployeeManagementDTO> findActiveEmployees(@PathVariable String entityType) {
        return employeeManagementService.findActiveEmployees(entityType);
    }

    @PostMapping("/entityDetail/uploadProfilePicture/add")
    public ResponseEntity<?> uploadProfilePicture(@RequestHeader(name = "entityId")
                                                  @Pattern(regexp = "\\d+", message = "Entity ID must be numeric")
                                                  Long entityId,
                                                  @Size(max = 5 * 1024 * 1024, message = "File size must be less than 5MB")
                                                  @RequestPart("file") MultipartFile file,
                                                  @Pattern(regexp = "\\d+", message = "Comp-Key must be numeric")
                                                  @RequestHeader("Comp-Key") Long compKey) throws URISyntaxException, IOException, StorageException {

        try {
            if (file.isEmpty()) {
                return responseEntityResult.responseEntity(APIResponse.builder().code(400).error(null)
                        .message("File Is Required.").warning(null).data(null).build());
            }
            if (!file.getOriginalFilename().toLowerCase().endsWith("jpg")
                    && !file.getOriginalFilename().toLowerCase().endsWith("png")) {
                return responseEntityResult.responseEntity(APIResponse.builder().code(400).error(null)
                        .message("Allowd Extensions Are jpg & png.").warning(null).data(null).build());
            }
            EntityDetailDTO entityDetailDTO = employeeManagementService.uploadToStorage(file, entityId, compKey);
            if (entityDetailDTO == null) {
                return responseEntityResult.responseEntity(APIResponse.builder().code(404).data(null).error(null).warning(null)
                        .message("No Data Found For The Following Entity Id, Please Try A Valid Id: " + entityId).build());
            } else {
                return responseEntityResult.responseEntity(APIResponse.builder().code(200).message(null)
                        .data(entityDetailDTO).warning(null).error(null)
                        .build());
            }

        } catch (Exception exception) {
            return responseEntityResult.responseEntity(APIResponse.builder().code(500).error(exception.getMessage())
                    .message(null).warning(null).data(null).build());
        }
    }

    @PostMapping("/entityDetail/updateProfilePicture/add")
    public ResponseEntity<?> updateProfilePicture(@RequestHeader(name = "entityId")
                                                  @Pattern(regexp = "\\d+", message = "Entity ID must be numeric")
                                                  Long entityId,
                                                  @RequestPart(name = "file")
                                                  @Size(max = 5 * 1024 * 1024, message = "File size must be less than 5MB")
                                                  MultipartFile file,
                                                  @Pattern(regexp = "\\d+", message = "Comp-Key must be numeric")
                                                  @RequestHeader(name = "Comp-Key")
                                                  Long compKey) {
        try {
            if (file.isEmpty()) {
                return responseEntityResult.responseEntity(APIResponse.builder().code(400).error(null)
                        .message("File Is Required.").warning(null).data(null).build());
            }
            if (!file.getOriginalFilename().toLowerCase().endsWith("jpg")
                    && !file.getOriginalFilename().toLowerCase().endsWith("png")) {
                return responseEntityResult.responseEntity(APIResponse.builder().code(400).error(null)
                        .message("Allowd Extensions Are jpg & png.").warning(null).data(null).build());
            }
            EntityDetailDTO entityDetailDTO = employeeManagementService.updateToStorage(file, entityId, compKey);
            if (entityDetailDTO == null) {
                return responseEntityResult.responseEntity(APIResponse.builder().code(404).data(null).error(null).warning(null)
                        .message("No Data Found For The Following Entity Id, Please Try A Valid Id: " + entityId).build());
            } else {
                return responseEntityResult.responseEntity(APIResponse.builder().code(200).message(null).
                        warning(null).error(null).data(entityDetailDTO).build());
            }
        } catch (Exception exception) {
            return responseEntityResult.responseEntity(APIResponse.builder().code(500).error(exception.getMessage())
                    .message(null).warning(null).data(null).build());
        }
    }

    @GetMapping("/entityDetail/findByEntityId/{entityId}")
    public APIResponse findByEntityId(@PathVariable Long entityId) {
        return APIResponse.builder()
                .data(employeeManagementService.findByEntityId(entityId))
                .build();
    }

    /**
     * Description: Method to return employee details
     * Created By: Ibtehaj
     *
     * @param entityId
     * @param entityType
     * @return
     */
    @GetMapping("/v1/findEmployee/{entityId}/{entityType}")
    public Map findByEntityIdAndEntityId(@PathVariable Long entityId, @PathVariable String entityType) {
        Map response = new HashMap();
        if (entityId != null && entityType != null) {
            response = employeeManagementService.findEmployeeDetails(entityId, entityType, response);
        } else {
            response.put("data", null);
            response.put("message", "Parameters cannot be null");
            response.put("code", HttpStatus.PRECONDITION_FAILED);
        }
        return response;
    }

    /**
     * Description: Api to disable employee
     * Created By: Ibtehaj
     *
     * @param entityId
     * @param isDisable
     * @return
     */
    @PostMapping("/v1/disableEmployee/{entityId}/{isDisable}")
    public Map findByEntityIdAndEntityId(@PathVariable Long entityId, @PathVariable Boolean isDisable) {
        Map response = new HashMap();
        if (entityId != null && isDisable != null) {
            response = employeeManagementService.disableEmployee(response, entityId, isDisable);
        } else {
            response.put("data", null);
            response.put("message", "Parameters cannot be null");
            response.put("code", HttpStatus.PRECONDITION_FAILED);
        }
        return response;
    }

    /**
     * Description: Api to delete employee
     * Created By: Ibtehaj
     *
     * @param entityId
     * @return
     */
    @PostMapping("/v1/deleteEmployee/{entityId}")
    public Map findByEntityIdAndEntityId(@PathVariable Long entityId) {
        Map response = new HashMap();
        if (entityId != null) {
            response = employeeManagementService.deleteEmployee(response, entityId);
        } else {
            response.put("data", null);
            response.put("message", "Parameters cannot be null");
            response.put("code", HttpStatus.PRECONDITION_FAILED);
        }
        return response;
    }


}
