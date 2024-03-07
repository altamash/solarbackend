package com.solar.api.tenant.controller.v1;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.tenant.mapper.customerSupport.ConversationHeadDTO;
import com.solar.api.tenant.mapper.workOrder.MongoCustomerDetailWoDTO;
import com.solar.api.tenant.service.WorkOrderService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("WorkOrderController")
@RequestMapping(value = "/workOrder")
public class WorkOrderController {
    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private DataExchange dataExchange;
    @Value("${app.mongoBaseUrl}")
    private String MONGO_BASE_URL;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/getAllCustomers/{orgId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> showAllCustomersAgainstOrg(@PathVariable("orgId") String orgId) {
        return new ResponseEntity(workOrderService.getCustomerList(Long.parseLong(orgId)), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/getAllOragnizations", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> getAllOragnizations() {
        return new ResponseEntity(workOrderService.getOrgList(), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/getAllCustomers", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> showAllCustomers() {
        return new ResponseEntity(workOrderService.getAllCustomerList(), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/getWorkOrderList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> getWorkOrderList(@RequestParam("projectId") String projectId,
                                                @RequestParam("sectionId") String sectionId,
                                                @RequestParam("orgId") Long orgId) {
        return new ResponseEntity(workOrderService.getWorkOrderList(projectId, sectionId, orgId), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/getCusTypeChart", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> getCusTypeChart(@RequestParam("projectId") String projectId,
                                               @RequestParam("sectionId") String sectionId,
                                               @RequestParam("orgId") Long orgId) {
        return new ResponseEntity(workOrderService.getCusTypeChart(projectId, sectionId, orgId), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/getWorkOrderDetail", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> getWorkOrderDetail(@RequestParam("projectId") String projectId,
                                                  @RequestParam("sectionId") String sectionId,
                                                  @RequestParam("workOrderId") String workOrderId) {
        return new ResponseEntity(workOrderService.getWorkOrderDetail(projectId, sectionId, workOrderId), HttpStatus.OK);
    }

    @PostMapping("/addOrUpdateWorkOrder/{projectId}/{reqType}")
    public ResponseEntity<Map> addUpdateWorkOrder(@PathVariable("projectId") String projectId,
                                                  @PathVariable("reqType") String reqType,
                                                  @RequestParam("sectionJson") String sectionJson,
                                                  @RequestParam(value = "resourcesJson", required = false) String resourcesJson,
                                                  @RequestHeader("Priv-Level") String privLevel) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        headers.put("Priv-Level", Arrays.asList(privLevel));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        BaseResponse response1 = null;
        Map response = new HashMap();
        try {
            if (sectionJson != null)
                response1 = workOrderService.addOrUpdateWorkOrder(sectionJson, reqType, MONGO_BASE_URL, privLevel, projectId, resourcesJson);

        } catch (URISyntaxException | IOException | StorageException e) {
            LOGGER.error(e.getMessage(), response1);
            response.put("data", null);
            response.put("message", e.getMessage());
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", null);
        response.put("message", response1.getMessage());
        response.put("code", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/getCustomerWithAllSubscriptionsBy/orgId/{orgId}/entityId/{entityId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> getCustomerWithAllSubscriptions(@PathVariable("orgId") String orgId, @PathVariable("entityId") String entityId) {
        return new ResponseEntity(workOrderService.getCustomerWithAllSubscriptionsByEntityAndOrgId(Long.parseLong(entityId), Long.parseLong(orgId)), HttpStatus.OK);
    }

    /**
     * @author : sana
     * Description: this api will check that either this customer subscription's garden associated to any distribution Centre or not
     * Component : Work order creation
     * Feature : workOrder creation from service management module
     */
    @PreAuthorize("checkAccess()")
    @PostMapping(value = "/validateTicketCustomer", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> validateTicketCustomer(@RequestBody MongoCustomerDetailWoDTO mongoCustomerDetailWoDTO) {
        return new ResponseEntity(workOrderService.validateTicketCustomer(mongoCustomerDetailWoDTO), HttpStatus.OK);
    }

    @GetMapping("/getSubscriptionsByEntityId/{entityId}")
    public Map getSubscriptionsByEntityId(@PathVariable Long entityId) {
        return workOrderService.getSubscriptionsByEntityId(entityId);
    }

    @GetMapping(value = "/showWorkOrderTemplate", produces = {MediaType.APPLICATION_JSON_VALUE})
    public com.solar.api.tenant.model.BaseResponse showAcquisitionTemplate(@RequestHeader("Priv-Level") String privLevel,
                                                                           @RequestHeader("Comp-Key") Long compKey) {
        return dataExchange.showWorkOrderTemplate(privLevel, compKey);
    }

    @ApiOperation("Used in work order detail screen")
    @GetMapping("/getCustomerAndGardenDetailListBySourceType")
    public BaseResponse getCustomerAndGardenDetailListBySourceType(@RequestParam("sourceType") String sourceType) {
        return workOrderService.getCustomerAndGardenDetailListBySourceType(sourceType);
    }

    @ApiOperation("Used in work order detail screen")
    @GetMapping("/getBusinessUnitInformation")
    public BaseResponse getBusinessUnitInformation(@RequestParam("workOrderId") String workOrderId) {
        return workOrderService.getBusinessUnitInformation(workOrderId);
    }

    @ApiOperation(value = "Used in Employee Management")
    @GetMapping("/getAvailableEmployeesForWorkOrder")
    public BaseResponse getAvailableEmployeesForWorkOrder(@RequestParam("businessUnitId") Long businessUnitId,
                                                          @RequestParam(name = "workOrderId", required = false) String workOrderId,
                                                          @RequestParam(name = "searchWord", required = false) String searchWord) {
        return workOrderService.getAvailableEmployeesForWorkOrder(businessUnitId, workOrderId, searchWord);
    }

    @ApiOperation(value = "Used in Work Order")
    @GetMapping("/getAssignedEmployeeWordOrderLists")
    public BaseResponse getAssignedEmployeeWordOrderLists(@RequestParam("workOrderId") String workOrderId,
                                                          @RequestParam(name = "searchWord", required = false) String searchWord,
                                                          @RequestParam("pageNumber") Integer pageNumber,
                                                          @RequestParam("pageSize") Integer pageSize) {
        return workOrderService.getAssignedEmployeeWordOrderLists(workOrderId, searchWord, pageSize, pageNumber);
    }

    @ApiOperation(value = "Close case Api to update the status of the ticket(close, resolved,deferred)")
    @PostMapping("/bulkCloseWorkOrder")
    public ResponseEntity<?> bulkCloseWorkOrder(@RequestBody List<ConversationHeadDTO> conversationHeadDTOS) {
        return workOrderService.resolveOrCloseWorkOrderList(conversationHeadDTOS);
    }


    @PostMapping("/addOrUpdateWorkOrderV2/{reqType}")
    public BaseResponse addOrUpdateWorkOrderV2(@PathVariable("reqType") String reqType,
                                               @RequestParam("sectionJson") String sectionJson,
                                               @RequestParam("businessUnitId") Long businessUnitId,
                                               @RequestParam(value = "entityRoleIds", required = false) String entityRoleIds,
                                               @RequestHeader("Priv-Level") String privLevel,
                                               @RequestHeader("Comp-Key") Long compKey,
                                               @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        return workOrderService.addOrUpdateWorkOrderV2(sectionJson, reqType, entityRoleIds, businessUnitId, privLevel, compKey, files);
    }

    @ApiOperation("Used in work order detail screen")
    @GetMapping("/getSubscriptionInformation")
    public BaseResponse getSubscriptionInformation(@RequestParam("workOrderId") String workOrderId) {
        return workOrderService.getSubscriptionInformation(workOrderId);
    }

    @GetMapping("/getWorkOrderRequestorInfoBySourceType")
    public BaseResponse getWoekOrderRequestorInfoBySourceType(@RequestParam("sourceType") String sourceType,
                                                              @RequestParam("workOrderId") String workOrderId) {
        return workOrderService.getWorkOrderRequestorInfoBySourceType(sourceType, workOrderId);
    }

    @GetMapping("/getAllWorkOrderList")
    public BaseResponse getAllWorkOrderList(@RequestHeader("Comp-Key") Long compKey,
                                            @RequestParam("groupBy") String groupBy,
                                            @RequestParam(value = "groupByName", required = false) String groupByName,
                                            @RequestParam(value = "status", required = false) String status,
                                            @RequestParam(value = "type", required = false) String type,
                                            @RequestParam(value = "requesterType", required = false) String requesterType,
                                            @RequestParam(value = "requesterIds", required = false) String requesterIds,
                                            @RequestParam(value = "agentIds", required = false) String agentIds,
                                            @RequestParam(value = "billable", required = false) String billable,
                                            @RequestParam("pageNumber") Integer pageNumber,
                                            @RequestParam("pageSize") Integer pageSize) {
        return workOrderService.getAllWorkOrderList(compKey, groupBy, groupByName, status, type, requesterType, requesterIds, agentIds, billable, pageSize, pageNumber);
    }

    @GetMapping("/getAllWorkOrderInformation")
    public BaseResponse getAllWorkOrderInformation(@RequestParam("workOrderId") String workOrderId) {
        return workOrderService.getAllWorkOrderInformation(workOrderId);
    }
    @GetMapping("/getWorkOrderManagementFilterDropDown")
    public BaseResponse getWorkOrderManagementFilterDropDown(@RequestHeader("Comp-Key") Long compKey) {
        return workOrderService.getWorkOrderManagementFilterDropDown();
    }
    @GetMapping("/getTicketInformation")
    public BaseResponse getTicketInformation(@RequestHeader("Comp-Key") Long compKey,
                                                             @RequestParam("headId") Long headId) {
        return workOrderService.getTicketInformation(headId);
    }

}

