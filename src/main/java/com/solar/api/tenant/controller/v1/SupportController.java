package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.support.*;
import com.solar.api.tenant.model.support.SupportRequestHead;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.SupportRequestHeadService;
import com.solar.api.tenant.service.SupportRequestHistoryService;
import com.solar.api.tenant.service.SupportStatusWorkflowService;
import com.solar.api.tenant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SupportController")
@RequestMapping(value = "/support")
public class SupportController {

    @Autowired
    private SupportRequestHeadService supportRequestHeadService;
    @Autowired
    private SupportRequestHistoryService supportRequestHistoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private SupportStatusWorkflowService supportStatusWorkflowService;

    /*
     * Support
     * Request
     * Head
     */

    /**
     * @param supportRequestHeadDTO
     * @return
     */
    @PostMapping("/request/add")
    public SupportRequestHeadDTO add(@RequestBody SupportRequestHeadDTO supportRequestHeadDTO, Long id) {

        User userData = userService.findById(supportRequestHeadDTO.getAccountId());
        supportRequestHeadDTO.setFirstName(userData.getFirstName());
        supportRequestHeadDTO.setLastName(userData.getLastName());
        userData.getRoles().forEach(val -> {
            supportRequestHeadDTO.setRole(val.getName());
        });
        return SupportRequestHeadMapper.toSupportRequestHeadDTO(
                supportRequestHeadService.addOrUpdate(SupportRequestHeadMapper.toSupportRequestHead(supportRequestHeadDTO)));
    }

    /**
     * @param supportRequestHeadDTO
     * @return
     */
    @PutMapping("/request/edit")
    public SupportRequestHeadDTO update(@RequestBody SupportRequestHeadDTO supportRequestHeadDTO) {
        return SupportRequestHeadMapper.toSupportRequestHeadDTO(
                supportRequestHeadService.addOrUpdate(SupportRequestHeadMapper.toSupportRequestHead(supportRequestHeadDTO)));
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/requestHead/{id}")
    public SupportRequestHeadDTO findById(@PathVariable Long id) {
        return SupportRequestHeadMapper.toSupportRequestHeadDTO(supportRequestHeadService.findById(id));
    }

    @GetMapping("/requestByUser/{userId}")
    public List<SupportRequestHeadDTO> findByUserId(@PathVariable Long userId) {
        return SupportRequestHeadMapper.toSupportRequestHeadDTOs(supportRequestHeadService.findByAccountId(userId));
    }

    @GetMapping("/requestBySubscription/{subscriptionId}")
    public List<SupportRequestHeadDTO> findBySubscriptionId(@PathVariable Long subscriptionId) {
        return SupportRequestHeadMapper.toSupportRequestHeadDTOs(supportRequestHeadService.findBySubscriptionId(subscriptionId));
    }

    @PostMapping("/search")
    public List<SupportRequestHeadDTO> search(@RequestBody SupportRequestHeadSearchDTO supportRequestHeadSearchDTO) {
        if (supportRequestHeadSearchDTO.getSr_id() != null) {
            SupportRequestHead searchBySupportId =
                    supportRequestHeadService.findById(supportRequestHeadSearchDTO.getSr_id());
            if (searchBySupportId != null) {
                System.out.println(searchBySupportId);
                return SupportRequestHeadMapper.toSupportRequestHeadDTOs(Arrays.asList(supportRequestHeadService.findById(supportRequestHeadSearchDTO.getSr_id())));
            } else {
                return null;
            }
        }
        if (supportRequestHeadSearchDTO.getSubscriptionId() != null) {
            List<SupportRequestHead> searchBySubscriptionId =
                    supportRequestHeadService.findBySubscriptionId(supportRequestHeadSearchDTO.getSubscriptionId());
            System.out.println(searchBySubscriptionId);
            if (searchBySubscriptionId != null) {
                return SupportRequestHeadMapper.toSupportRequestHeadDTOs(supportRequestHeadService.findBySubscriptionId(supportRequestHeadSearchDTO.getSubscriptionId()));
            } else {
                return null;
            }
        }
        if (supportRequestHeadSearchDTO.getUserAccountId() != null) {
            List<SupportRequestHead> searchByUserId =
                    supportRequestHeadService.findByAccountId(supportRequestHeadSearchDTO.getUserAccountId());
            System.out.println(searchByUserId);
            if (searchByUserId != null) {
                return SupportRequestHeadMapper.toSupportRequestHeadDTOs(supportRequestHeadService.findByAccountId(supportRequestHeadSearchDTO.getUserAccountId()));
            } else {
                return null;
            }
        }
        if (supportRequestHeadSearchDTO.getStatus() != "") {
            List<SupportRequestHead> searchByStatus =
                    supportRequestHeadService.findByStatus(supportRequestHeadSearchDTO.getStatus());
            System.out.println(searchByStatus);
            if (searchByStatus != null) {
                return SupportRequestHeadMapper.toSupportRequestHeadDTOs(supportRequestHeadService.findByStatus(supportRequestHeadSearchDTO.getStatus()));
            } else {
                return null;
            }
        }
        return SupportRequestHeadMapper.toSupportRequestHeadDTOs(supportRequestHeadService.findAllFetchSupportRequestHistories());
    }

    /**
     * @return
     */
    @GetMapping("/getAllRequests")
    public List<SupportRequestHeadDTO> findAll() {
        return SupportRequestHeadMapper.toSupportRequestHeadDTOs(supportRequestHeadService.findAllFetchSupportRequestHistories());
    }

    /*
     * Support
     * Request
     * History
     */

    /**
     * @param supportRequestHistoryDTO
     * @return
     */
    @PostMapping("/requestHistory/add")
    public SupportRequestHistoryDTO add(@RequestBody SupportRequestHistoryDTO supportRequestHistoryDTO) {
        User userData = userService.findById(supportRequestHistoryDTO.getResponderUserId());
        supportRequestHistoryDTO.setFirstName(userData.getFirstName());
        supportRequestHistoryDTO.setLastName(userData.getLastName());
        userData.getRoles().forEach(val -> {
            supportRequestHistoryDTO.setRole(val.getName());
        });
        return SupportRequestHistoryMapper.toSupportRequestHistoryDTO(
                supportRequestHistoryService.addOrUpdate(SupportRequestHistoryMapper.toSupportRequestHistory(supportRequestHistoryDTO)));
    }

    /**
     * @param supportRequestHistoryDTO
     * @return
     */
    @PutMapping("/requestHistory/edit")
    public SupportRequestHistoryDTO update(@RequestBody SupportRequestHistoryDTO supportRequestHistoryDTO) {
        return SupportRequestHistoryMapper.toSupportRequestHistoryDTO(
                supportRequestHistoryService.addOrUpdate(SupportRequestHistoryMapper.toSupportRequestHistory(supportRequestHistoryDTO)));
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/getRequestHistory/{id}")
    public SupportRequestHistoryDTO findHistoryById(@PathVariable Long id) {
        return SupportRequestHistoryMapper.toSupportRequestHistoryDTO(supportRequestHistoryService.findById(id));
    }

    @GetMapping("/requestHistory/byId/{id}")
    public List<SupportRequestHistoryDTO> findByResponderId(@PathVariable Long id) {
        return SupportRequestHistoryMapper.toSupportRequestHistoryDTOs(supportRequestHistoryService.findByResponderUserId(id));
    }

    /**
     * @return
     */
    @GetMapping("/getAllRequestsHistory")
    public List<SupportRequestHistoryDTO> findAllHistory() {
        return SupportRequestHistoryMapper.toSupportRequestHistoryDTOs(supportRequestHistoryService.findAll());
    }

    /*
     * Support
     * Status
     * Workflow
     */

    /**
     * @param supportStatusWorkflowDTOs
     * @return
     */
    @PostMapping("/statusWorklflow/add")
    public List<SupportStatusWorkflowDTO> addWorkflows(@RequestBody List<SupportStatusWorkflowDTO> supportStatusWorkflowDTOs) {
        return SupportStatusWorkflowMapper.toSupportStatusWorkflowDTOs(
                supportStatusWorkflowService.add(SupportStatusWorkflowMapper.toSupportStatusWorkflows(supportStatusWorkflowDTOs)));
    }

    /**
     * @return
     */
    @GetMapping("/getAllStatusWorkflows")
    public List<SupportStatusWorkflowDTO> findAllStatusWorkflows() {
        return SupportStatusWorkflowMapper.toSupportStatusWorkflowDTOs(supportStatusWorkflowService.findAll());
    }
}
