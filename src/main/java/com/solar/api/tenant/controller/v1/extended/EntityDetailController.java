package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.contract.EntityDetailDTO;
import com.solar.api.tenant.mapper.contract.EntityDetailMapper;
import com.solar.api.tenant.mapper.tiles.entityDetail.EntityDetailTile;
import com.solar.api.tenant.service.contract.EntityDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("EntityDetailController")
@RequestMapping(value = "/entityDetail")
public class EntityDetailController {

    @Autowired
    private EntityDetailService entityDetailService;

    @GetMapping("/findAllEntityDetails/{entityIds}")
    public List<EntityDetailDTO> findAllEntityDetail(@PathVariable List<Long> entityIds) {
        return EntityDetailMapper.toEntityDetailDTOList(entityDetailService.findAllByEntityIdIn(entityIds));
    }

    @GetMapping("/findEntityDetailById/{entityId}")
    public EntityDetailDTO findAllEntityDetailById(@PathVariable Long entityId) {
        return EntityDetailMapper.toEntityDetailDTO(entityDetailService.findByEntityId(entityId));
    }
    @GetMapping("/getImageByEntityId/{entityId}")
    public EntityDetailTile getImageByEntityId(@PathVariable Long entityId) {
        return entityDetailService.getImageByEntityId(entityId);
    }

    @GetMapping("/findProfilePictureByAcctIds")
    public List<EntityDetailTile> findProfilePictureByAcctIds(@RequestParam("acctIds") List<Long> acctIds) {
        return entityDetailService.findProfilePictureByAccountIds(acctIds);
    }

    @GetMapping("/findProfilePictureByEntityDetailIds")
    public List<EntityDetailTile> findProfilePictureByEntityDetailIds(@RequestParam("entityDetailIds") List<Long> entityDetailIds) {
        return entityDetailService.findProfilePictureByEntityDetailIds(entityDetailIds);
    }
}
