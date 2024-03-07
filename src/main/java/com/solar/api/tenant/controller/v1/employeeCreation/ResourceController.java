package com.solar.api.tenant.controller.v1.employeeCreation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.user.userGroup.AddResourceTile;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.mapper.user.userGroup.UserGroupDTO;
import com.solar.api.tenant.service.userGroup.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.solar.api.tenant.mapper.user.userGroup.UserGroupMapper.*;

/**
 * Date : 1st Dec, 2022
 * Updated: 17th Jan, 2022
 *
 * @Shariq This controller is only to fetch resources withing project/task
 * @refId is string value refers to id in MongoDB
 * @refType is string value refers to the reference (project/task) in MongoDB
 * @status default value is true
 */

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ResourceController")
@RequestMapping(value = "/v1/resources")
public class ResourceController {

    @Autowired
    ResourceService resourceService;

    /**
     * Add Resources to Project, Task, WorkOrder
     *
     * @param addResourceTile
     * @return
     */
    @PostMapping("/add")
    public ObjectNode addResources(@RequestBody AddResourceTile addResourceTile) {
        return resourceService.addResources(addResourceTile.getUserGroup(), addResourceTile.getEntityRoleIds());
    }

    /**
     * Resources if already exist in workOrder/Project/Task
     * CreatedBy : Shariq
     *
     * @param refId
     * @param refType
     * @param status
     * @param isExist
     * @return
     */
    @GetMapping("/refId/{refId}/refType/{refType}/status/{status}/{isExist}")
    public List<DefaultUserGroupResponseDTO> findByRefIdAndRefTypeAndStatus(@PathVariable String refId, @PathVariable String refType,
                                                                            @PathVariable boolean status,
                                                                            @PathVariable String isExist) {
        return resourceService.findByRefIdAndRefTypeAndStatusToTile(refId, refType, status, isExist);
    }

    @PatchMapping("/id/{id}/status/{status}")
    public ResponseEntity<Map<String, String>> enableAndDisableResource(@PathVariable("id") Long id, @PathVariable("status") Boolean status) {
        Map response = resourceService.enableAndDisableResource(id, status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/entityGroupId/{entityGroupId}/status/{status}")
    public ResponseEntity<Map<String, String>> enableAndDisableResourceByEntityGroupId(@PathVariable("entityGroupId") Long entityGroupId, @PathVariable("status") Boolean status) {
        Map response = resourceService.enableAndDisableResourceByEntityGroupId(entityGroupId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parentId/{parentId}")
    public List<DefaultUserGroupResponseDTO> findByParentId(@PathVariable("parentId") String parentId) {
        return resourceService.findByParentIdTile(parentId);
    }
}
