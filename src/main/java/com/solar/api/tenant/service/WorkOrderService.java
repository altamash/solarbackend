package com.solar.api.tenant.service;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.mapper.customerSupport.ConversationHeadDTO;
import com.solar.api.tenant.mapper.workOrder.MongoCustomerDetailWoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface WorkOrderService {


    Map getCustomerList(Long orgId);

    Map getOrgList();

    Map getWorkOrderList(String projectId, String sectionId, Long orgId);

    Map getWorkOrderDetail(String projectId, String sectionId, String workOrderId);

    Map getAllCustomerList();

    BaseResponse addOrUpdateWorkOrder(String workOrder, String requestType, String mongoBaseUrl, String privLevel, String projectId, String resourcesJson) throws URISyntaxException, IOException, StorageException;

    ;

    Map getCusTypeChart(String projectId, String sectionId, Long orgId);

    Map getCustomerWithAllSubscriptionsByEntityAndOrgId(Long entityId, Long orgId);

    Map validateTicketCustomer(MongoCustomerDetailWoDTO mongoCustomerDetailWoDTO);

    Map<String, Object> getSubscriptionsByEntityId(Long entityId);

    BaseResponse getAvailableEmployeesForWorkOrder(Long businessUnitId, String workOrderId, String searchWord);

    BaseResponse getAssignedEmployeeWordOrderLists(String workOrderId, String searchWord, Integer size, int pageNumber);

    BaseResponse addOrUpdateWorkOrderV2(String workOrder, String requestType, String entityRoleIds, Long businessUnitId, String privLevel, Long compKey, List<MultipartFile> files);

    BaseResponse getCustomerAndGardenDetailListBySourceType(String sourceType);

    BaseResponse getBusinessUnitInformation(String workOrderId);

    ResponseEntity<?> resolveOrCloseWorkOrderList(List<ConversationHeadDTO> conversationHeadDTOS);

    BaseResponse getSubscriptionInformation(String workOrderId);

    BaseResponse getWorkOrderRequestorInfoBySourceType(String sourceType, String workOrderId);

    BaseResponse getAllWorkOrderList(Long compKey, String groupBy, String groupByName, String status, String type, String requesterType, String requesterIds, String agentIds, String billable, Integer pageSize, Integer pageNumber);

    BaseResponse getAllWorkOrderInformation(String workOrderId);

    BaseResponse getWorkOrderManagementFilterDropDown();

    BaseResponse getTicketInformation(Long headId);
}
