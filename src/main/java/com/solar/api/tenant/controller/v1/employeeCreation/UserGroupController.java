package com.solar.api.tenant.controller.v1.employeeCreation;

import com.solar.api.tenant.mapper.tiles.UserGroupResourceTile;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.mapper.user.userGroup.EmployeeDTO;
import com.solar.api.tenant.mapper.user.userGroup.UserGroupDTO;
import com.solar.api.tenant.service.userGroup.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.user.userGroup.UserGroupMapper.toUserGroup;
import static com.solar.api.tenant.mapper.user.userGroup.UserGroupMapper.toUserGroupDTO;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("UserGroupController")
@RequestMapping(value = "/userGroup")
public class UserGroupController {

    @Autowired
    UserGroupService userGroupService;

    /**
     * User Group
     *
     * @param userGroupDto
     * @return
     */
    @PostMapping
    public UserGroupDTO addOrUpdate(@RequestBody UserGroupDTO userGroupDto) {
        return toUserGroupDTO(userGroupService.addOrUpdate(toUserGroup(userGroupDto)));
    }

    @GetMapping("/findByListIds")
    public List<DefaultUserGroupResponseDTO> findByIds(@PathVariable("ids") List<Long> longList) {
        return null;
    }

    @GetMapping("/{groupName}/name")
    public UserGroupDTO getUserGroupByName(@PathVariable("groupName") String groupName) {
        return toUserGroupDTO(userGroupService.getByGroupName(groupName));
    }

    @GetMapping("id/{id}")
    public UserGroupDTO getUserGroupById(@PathVariable("id") long id) {
        return toUserGroupDTO(userGroupService.findById(id));
    }

    @PatchMapping("groupId/{groupId}/status/{status}")
    public UserGroupDTO activateUserGroup(@PathVariable("groupId") Long groupId, @PathVariable("status") Boolean
            status) {
        return toUserGroupDTO(userGroupService.activateUserGroup(groupId, status));
    }

    @GetMapping("/getExistingResourcesByUserGroup/{groupId}")
    public List<EmployeeDTO> getExistingResourcesByUserGroup(@PathVariable("groupId") Long groupId) {
        return userGroupService.findExistingResourcesByGroup(groupId);
    }

    @GetMapping("/getAllByType/{groupType}")
    public List<UserGroupDTO> getAllUserGroup(@PathVariable("groupType") String groupType) {
        return userGroupService.getAllUserGroupByType(groupType);
    }

    @DeleteMapping("/delete/{id}")
    public UserGroupDTO deleteUserGroup(@PathVariable("id") long id) {
        return toUserGroupDTO(userGroupService.removeUserGroupById(id));
    }

    @GetMapping("/getAllByTypeWithResources/{groupType}")
    public List<UserGroupResourceTile> getAllUserGroupByTypeWithResources(@PathVariable("groupType") String groupType) {
        return userGroupService.getAllUserGroupByTypeResources(groupType);
    }

}
