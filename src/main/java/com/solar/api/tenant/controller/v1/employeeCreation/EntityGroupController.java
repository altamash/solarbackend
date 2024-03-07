package com.solar.api.tenant.controller.v1.employeeCreation;

import com.solar.api.tenant.mapper.user.userGroup.EntityGroupCustomDTO;
import com.solar.api.tenant.mapper.user.userGroup.EntityGroupDTO;
import com.solar.api.tenant.service.userGroup.EntityGroupService;
import com.solar.api.tenant.service.userGroup.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.solar.api.tenant.mapper.user.userGroup.EntityGroupMapper.*;

/**
 * Date : 30th Nov, 2022
 *
 * @Shariq This controller is only to fetch resources withing the resource group
 * <p>
 * UserGroup will set for id:1 by default on create new EntityGroup
 * @status default value is true
 */

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("EntityGroupController")
@RequestMapping(value = "/entityGroup")
public class EntityGroupController {

    @Autowired
    EntityGroupService entityGroupService;

    @PostMapping
    public EntityGroupDTO add(@RequestBody EntityGroupDTO entityGroupDTO) {
        return toEntityGroupDTO(entityGroupService.addOrUpdate(toEntityGroup(entityGroupDTO)));
    }

    @GetMapping("/{id}")
    public EntityGroupDTO findById(@PathVariable Long id) {
        return toEntityGroupDTO(entityGroupService.findById(id));
    }

    @GetMapping("/status/{status}")
    public List<EntityGroupDTO> findById(@PathVariable boolean status) {
        return toEntityGroupDTOs(entityGroupService.findByStatus(status));
    }

    @GetMapping("/userGroup/{id}")
    public List<EntityGroupDTO> getResources(@PathVariable Long id) {
        return toEntityGroupDTOs(entityGroupService.findByUserGroup(id));
    }

    @PostMapping("/addResourcesInGroup")
    public ResponseEntity<Map> addResourcesInTheGroup(@RequestBody EntityGroupCustomDTO entityGroupCustomDTO) {
        Map map = entityGroupService.addResourcesInTheGroup(entityGroupCustomDTO.getWorkOrderName(), entityGroupCustomDTO.getRefType(), entityGroupCustomDTO.getRefId(), entityGroupCustomDTO.getProjectId(),
                entityGroupCustomDTO.getEntityGroupDTOList());
        return ResponseEntity.ok(map);
    }

    /**
     * Description : This will return list of resources for w/o which are not attached from project to w/o
     * This api is moved from ResourcesConsumedController due to changed in return data
     * Created by : Sana Siraj
     * Updated by : Kashif Ali
     * Update Date : 26 Dec, 2022
     */

    @GetMapping("getAllUnAttachedResources/{parentRefId}/{parentRefType}")
    public List<EntityGroupDTO> getAllUnAttachedResources(@PathVariable String parentRefId, @PathVariable String parentRefType) {
        return toEntityGroupDTOs(entityGroupService.getUnattachedEntityGroup(parentRefId, parentRefType));
    }
}
