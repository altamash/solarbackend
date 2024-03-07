package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeDTO;
import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeMapper;
import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeWrapperDTO;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("UserLevelPrivilegeController")
@RequestMapping(value = "/userLevelPrivilege")
public class UserLevelPrivilegeController {
    @Autowired
    private UserLevelPrivilegeService userPrivilegeService;

    @PostMapping
    public List<UserLevelPrivilegeDTO> add(@RequestHeader("Authorization") String authorization, @RequestBody UserLevelPrivilegeWrapperDTO userLevelPrivilegeWrapperDTO)
            throws Exception {
        return UserLevelPrivilegeMapper.toUserLevelPrivilegeDTOList(userPrivilegeService.add(authorization, userLevelPrivilegeWrapperDTO));
    }
}
