package com.solar.api.tenant.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.exception.ForbiddenException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.Constants;
import com.solar.api.helper.Message;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionDetailTemplate;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.tenant.mapper.contract.OrganizationMapper;
import com.solar.api.tenant.mapper.customerSupport.ConversationHeadDTO;
import com.solar.api.tenant.mapper.customerSupport.ConversationHeadMapper;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionDTO;
import com.solar.api.tenant.mapper.tiles.workorder.*;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.employee.EmployeeManagementPaginationTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.employee.EmployeeManagementTile;
import com.solar.api.tenant.mapper.tiles.workorder.filter.WorkOrderManagementFilterDTO;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.mapper.user.userGroup.EntityGroupDTO;
import com.solar.api.tenant.mapper.workOrder.*;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.contract.OrganizationDetail;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.customerSupport.ConversationHead;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.userGroup.EntityGroup;
import com.solar.api.tenant.model.userGroup.UserGroup;
import com.solar.api.tenant.repository.ConversationHeadRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.UserGroup.UserGroupRepository;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.repository.contract.OrganizationDetailRepository;
import com.solar.api.tenant.repository.contract.OrganizationRepository;
import com.solar.api.tenant.repository.stage.monitoring.ExtDataStageDefinitionRepository;
import com.solar.api.tenant.repository.service.WorkOrderDetailRepository;
import com.solar.api.tenant.service.contract.OrganizationDetailService;
import com.solar.api.tenant.service.contract.OrganizationService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import com.solar.api.tenant.service.customerSupport.ConversationHeadService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import com.solar.api.tenant.service.userGroup.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordOrderServiceImpl implements WorkOrderService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${app.profile}")
    private String appProfile;

    @Value("${app.storage.container}")
    private String storageContainer;
    @Value("${app.mongoBaseUrl}")
    private String MONGO_BASE_URL;
    @Autowired
    private OrganizationDetailRepository organizationDetailRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private EntityGroupServiceImplExt entityGroupServiceImplExt;
    @Autowired
    private UserService userService;
    @Autowired
    private ConversationHeadService conversationHeadService;
    @Autowired
    private OrganizationDetailService organizationDetailService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;
    @Autowired
    private ExtDataStageDefinitionRepository extDataStageDefinitionRepository;
    @Autowired
    private EntityRoleService entityRoleService;
    @Autowired
    private ConversationHeadRepository conversationHeadRepository;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private EntityGroupService entityGroupService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private DocuLibraryService docuLibraryService;
    @Autowired
    private Utility utility;
    private final WorkOrderDetailRepository workOrderDetailRepository;

    public WordOrderServiceImpl(WorkOrderDetailRepository workOrderDetailRepository) {
        this.workOrderDetailRepository = workOrderDetailRepository;
    }

    @Autowired
    UserGroupRepository userGroupRepository;

    @Override

    public Map getCustomerList(Long orgId) {
        List<String> gardenIdList = organizationDetailRepository.getGardenIds(orgId);
        List<MongoCustomerDetailWoDTO> mongoDataResults = new ArrayList<>();
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();

        if (gardenIdList != null) {
            String ids = String.join(",", gardenIdList);
            String dynamicMappingUrl = MONGO_BASE_URL + "/workOrder/getSubscriptionList?variantIds=" + ids;

            Map<String, List<String>> headers = new HashMap<>();

            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));


            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues != null) {
                response.put("code", HttpStatus.OK);
                response.put("message", Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage());
                List<MongoCustomerDetailWoDTO> mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();

                List<Long> accountIds = mongoDataList.stream().map(x -> x.getAccountId() != null ? Long.parseLong(x.getAccountId()) : 0).collect(Collectors.toList());
                //List<Long> locIds = mongoDataList.stream().map(x -> x.getSubLocation() != null ? Long.parseLong(x.getSubLocation()) : 0).collect(Collectors.toList());

                List<UserSubscriptionTemplateWoDTO> userDetailList = userRepository.findUsersAndLocationByAccountId(accountIds);
                userDetailList.stream().map(userDetailSql -> {
                    mongoDataList.stream().map(mongoData ->
                            {
                                if (mongoData.getAccountId() != null && Long.parseLong(mongoData.getAccountId()) == userDetailSql.getAccountId()) {
                                    mongoData.setFirstName(userDetailSql.getFirstName());
                                    mongoData.setLastName(userDetailSql.getFirstName());
                                    mongoData.setCustomerType(userDetailSql.getCustomerType());
                                    mongoData.setEntityId(userDetailSql.getEntityId());
                                    mongoData.setEntityName(userDetailSql.getEntityName());
                                    mongoData.setRegion(userDetailSql.getRegion());
                                    mongoData.setProfileUrl(userDetailSql.getProfileUrl());
                                }
                                mongoDataResults.add(mongoData);
                                return mongoData;
                            }
                    ).collect(Collectors.toList());
                    return userDetailSql;
                }).collect(Collectors.toList());

            }

        } else {
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", "Subscriptions not found");
        }
        response.put("data", mongoDataResults);
        return response;


    }

    @Override
    public Map getOrgList() {
        Long masterOrgId = organizationRepository.getMasterOrgId();
        List<OrgnazationTemplateMasterWoDTO> organizationList = new ArrayList<>();
        OrgnazationTemplateWoDTO masterOrg = organizationRepository.findMasterOrg(masterOrgId);
        List<OrgnazationTemplateWoDTO> subOrgList = organizationRepository.findAllSubOrg(masterOrgId);

        organizationList.add(new OrgnazationTemplateMasterWoDTO(masterOrg.getOrgId(), masterOrg.getUnitName(), masterOrg.getUnitManager(), masterOrg.getUnitType(),
                masterOrg.getAddress(), masterOrg.getState(), masterOrg.getCountry(), masterOrg.getGeoLat(), masterOrg.getGeoLong()));


        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();

        if (subOrgList != null) {
            for (OrgnazationTemplateWoDTO template : subOrgList) {
                organizationList.add(new OrgnazationTemplateMasterWoDTO(template.getOrgId(), template.getUnitName(), template.getUnitManager(), template.getUnitType(),
                        template.getAddress(), template.getState(), template.getCountry(), template.getGeoLat(), template.getGeoLong()));
            }
            response.put("code", HttpStatus.OK.value());
            response.put("message", "Organization List Successfully Found");


        } else {
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", "No Organizations Found");
        }
        response.put("data", organizationList);
        return response;


    }

    @Override
    public Map getAllCustomerList() {
        List<String> gardenIdList = organizationDetailRepository.getAllGarden();
        List<MongoCustomerDetailWoDTO> mongoDataResults = new ArrayList<>();
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();

        if (gardenIdList != null) {
            String ids = String.join(",", gardenIdList);
            String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllGardensSubscription";

            Map<String, List<String>> headers = new HashMap<>();

            headers.put("Tenant-id", Collections.singletonList(DBContextHolder.getTenantName()));


            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues.getBody().getMongoCustomerDetailWoDTO() != null) {
                response.put("code", HttpStatus.OK);
                response.put("message", Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage());

                List<MongoCustomerDetailWoDTO> mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();

                List<Long> accountIds = mongoDataList.stream().map(x -> x.getAccountId() != null ? Long.parseLong(x.getAccountId()) : 0).collect(Collectors.toList());

                List<UserSubscriptionTemplateWoDTO> userDetailList = userRepository.findUsersByAccountId(accountIds);

                for (UserSubscriptionTemplateWoDTO userDetailSql : userDetailList) {
                    for (MongoCustomerDetailWoDTO mongoData : mongoDataList) {
                        if (mongoData.getAccountId() != null && Long.parseLong(mongoData.getAccountId()) == userDetailSql.getAccountId()) {
                            mongoData.setFirstName(userDetailSql.getFirstName());
                            mongoData.setLastName(userDetailSql.getFirstName());
                            mongoData.setCustomerType(userDetailSql.getCustomerType());
                            mongoData.setEntityId(userDetailSql.getEntityId());
                            mongoData.setEntityName(userDetailSql.getEntityName());
                            mongoData.setProfileUrl(userDetailSql.getProfileUrl());
                            mongoDataResults.add(mongoData);
                        }
                    }
                }
            }

        } else {
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", "Subscriptions not found");
        }

        response.put("data", mongoDataResults.stream().collect(
                Collectors.groupingBy(MongoCustomerDetailWoDTO::getAccountId)));
        return response;
    }

    public Map getWorkOrderList(String projectId, String sectionId, Long orgId) {
        List<MongoWorkOrderDTO> workOrderList = new ArrayList<>();
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();
        Map<String, List<String>> headers = new HashMap<>();
        try {
            String dynamicMappingUrl = MONGO_BASE_URL + "/workOrder/getWorkOrderList?projectId=" + projectId + "&sectionId=" + sectionId + "&orgId=" + orgId;
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));

            ResponseEntity<MongoWorkOrderMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, MongoWorkOrderMasterDTO.class);
            if (staticValues != null) {

                List<MongoWorkOrderDTO> mongoDataList = staticValues.getBody().getMongoWorkOrderDTO();
                mongoDataList.stream().map(x ->
                {
                    Long entityId = Long.parseLong(x.getCustomer());
                    UserDetailTemplateWoDTO customerDetails = userRepository.findUserDetails(entityId);
                    if (customerDetails != null) {
                        if (customerDetails.getUserName() != null) {
                            x.setCustomerName(customerDetails.getUserName());
                        }
                        if (customerDetails.getUserType() != null) {
                            x.setCustomerType(customerDetails.getUserType());
                        }
                        if (customerDetails.getUserPhone() != null) {
                            x.setCustomerPhone(customerDetails.getUserPhone());
                        }
                        if (customerDetails.getUserEmail() != null) {
                            x.setCustomerEmail(customerDetails.getUserEmail());
                        }
                        if (customerDetails.getImageUri() != null) {
                            x.setCustomerImageUri(customerDetails.getImageUri());
                        }
                    }
                    workOrderList.add(x);
                    return x;
                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            response.put("data", null);
            response.put("message", e.getMessage());
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value() + "");
            return response;
        }

        response.put("code", HttpStatus.OK);
        response.put("message", Message.WO_GET_List.getMessage());
        response.put("data", workOrderList);
        return response;
    }

    @Override
    public Map getWorkOrderDetail(String projectId, String sectionId, String workOrderId) {
        JSONObject workOrder = new JSONObject();
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();
        Map<String, List<String>> headers = new HashMap<>();
        try {
            String dynamicMappingUrl = MONGO_BASE_URL + "/workOrder/getWorkOrderDetail?projectId=" + projectId + "&sectionId=" + sectionId + "&workOrderId=" + workOrderId;

            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));

            ResponseEntity<String> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, String.class);
            if (staticValues != null) {

                String mongoData = staticValues.getBody().toString();
                workOrder = new JSONObject(mongoData);
                Long entityId = Long.parseLong(workOrder.get("entityId").toString());
                if (!workOrder.get("customer_supp_refId").toString().equalsIgnoreCase(null) || workOrder.get("customer_supp_refId") != null) {
                    Long refStaffId = Long.parseLong(workOrder.get("customer_supp_refId").toString());
                    UserDetailTemplateWoDTO refStaffDetails = userRepository.findUserDetails(refStaffId);
                    workOrder.put("customer_supp_refName", refStaffDetails.getUserName() != null ? refStaffDetails.getUserName() : "");
                }
                UserDetailTemplateWoDTO customerDetails = userRepository.findUserDetails(entityId);
                if (customerDetails != null) {
                    if (customerDetails.getUserName() != null) {
                        workOrder.put("customer_name", customerDetails.getUserName());
                    }
                    if (customerDetails.getUserType() != null) {
                        workOrder.put("customer_type", customerDetails.getUserType());
                    }
                    if (customerDetails.getUserPhone() != null) {
                        workOrder.put("customer_email", customerDetails.getUserPhone());
                    }
                    if (customerDetails.getUserEmail() != null) {
                        workOrder.put("customer_phone", customerDetails.getUserEmail());
                    }
                }


            }
        } catch (Exception e) {
            response.put("data", null);
            response.put("message", e.getMessage());
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value() + "");
            return response;
        }

        response.put("code", HttpStatus.OK);
        response.put("message", Message.WO_GET_Details.getMessage());
        response.put("data", workOrder.toString());
        return response;
    }

    @Override
    public BaseResponse addOrUpdateWorkOrder(String workOrder, String reqType, String mongoBaseUrl, String privLevel, String projectId, String resourcesJson) throws JsonProcessingException {
        String dynamicMappingsUrl = mongoBaseUrl + "/project/updateSectionContent/%s/%s";
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        headers.put("Priv-Level", Arrays.asList(privLevel));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("sectionJson", workOrder);
        ResponseEntity<com.solar.api.saas.service.integration.BaseResponse> response = null;

        try {
            response = WebUtils.submitRequest(HttpMethod.POST, String.format(dynamicMappingsUrl, projectId, reqType), map, headers, com.solar.api.saas.service.integration.BaseResponse.class);
            if (resourcesJson != null) {
                if (reqType.equalsIgnoreCase("save") && response != null && response.getStatusCode() == HttpStatus.OK) {
                    CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, EntityGroupDTO.class);
                    List<EntityGroupDTO> entityGroupDTOS = mapper.readValue(resourcesJson, javaType);
                    String workOrderId = response.getBody().getMessage().split("=")[1].trim();
                    Root root = mapper.readValue(workOrder, Root.class);
                    Root.Content content = (Root.Content) root.getContent().content.stream().findFirst().get();
                    Root.WorkOrder workOrderObj = (Root.WorkOrder) content.workOrder.stream().findFirst().get();
                    entityGroupServiceImplExt.addClosedGroupAndResources("WorkOrder", workOrderId, projectId, workOrderObj.name, entityGroupDTOS);
                    if (workOrderObj.ticket_number != null) {
                        conversationHeadService.associateWorkOrderToConversationHead(workOrderObj.ticket_number, workOrderId);
                    } else {
                        try {

                            conversationHeadService.add(ConversationHead.builder()
                                    .sourceType(AppConstants.WORK_ORDER)
                                    .status("Open")
                                    .sourceId(workOrderId)
                                    .build(), null, "DOCU");
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

        }
        return response.getBody();
    }


    @Override
    public Map getCusTypeChart(String projectId, String sectionId, Long orgId) {
        HashMap<String, TreeMap<String, Integer>> resultMap = new HashMap<>();
        List<MongoChartTemplateWoDTO> cusTypeChart = new ArrayList<>();
        Map response = new HashMap();

        Map<String, List<String>> headers = new HashMap<>();
        try {
            String dynamicMappingUrl = MONGO_BASE_URL + "/workOrder/getCusTypeChart?projectId=" + projectId + "&sectionId=" + sectionId + "&orgId=" + orgId;
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));

            ResponseEntity<MongoWorkOrderMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, MongoWorkOrderMasterDTO.class);
            if (staticValues != null) {

                HashMap<String, TreeMap<String, Integer>> bodyMap = staticValues.getBody().getCustTypeMap();
                for (String entityKey : bodyMap.keySet()) {
                    TreeMap<String, Integer> bodyTemp = bodyMap.get(entityKey);
                    Long entityId = Long.parseLong(entityKey);
                    if (entityId > 0) {
                        try {
                            UserDetailTemplateWoDTO customerDetails = userRepository.findUserDetails(entityId);
                            if (customerDetails != null) {
                                String entityType = customerDetails.getUserType() != null ? customerDetails.getUserType() : "";

                                if (resultMap.containsKey(entityType)) {
                                    TreeMap<String, Integer> temp = resultMap.get(entityType);
                                    for (String x : bodyTemp.keySet()) {
                                        if (temp.containsKey(x)) {
                                            bodyTemp.put(x, temp.get(x) + bodyTemp.get(x));
                                        }
                                        resultMap.put(entityType, bodyTemp);
                                    }
                                } else {
                                    resultMap.put(entityType, bodyTemp);
                                }
                            }
                        } catch (Exception e) {
                            response.put("data", null);
                            response.put("message", e.getMessage());
                            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value() + "");
                            return response;
                        }
                    }
                }
            }
        } catch (Exception e) {
            response.put("data", null);
            response.put("message", e.getMessage());
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value() + "");
            return response;
        }
        for (String cusTypeKey : resultMap.keySet()) {
            Set<String> keyCusType = resultMap.get(cusTypeKey).keySet();
            Collection<Integer> CusTypeValues = resultMap.get(cusTypeKey).values();
            ArrayList<String> listOfCusTypeKeys = new ArrayList<String>(keyCusType);
            ArrayList<Integer> listOfCusTypeValues = new ArrayList<>(CusTypeValues);
            if (!cusTypeKey.equals("")) {
                cusTypeChart.add(MongoChartTemplateWoDTO.builder().label(cusTypeKey).labels(listOfCusTypeKeys).values(listOfCusTypeValues).build());
            }

        }
        response.put("code", HttpStatus.OK);
        if (cusTypeChart.size() > 0)
            response.put("message", "Customer type chart retrieved successfully ");
        else
            response.put("message", "Customer type chart not found ");
        response.put("data", cusTypeChart);
        return response;
    }

    @Override
    public Map getCustomerWithAllSubscriptionsByEntityAndOrgId(Long entityId, Long orgId) {
        List<String> gardenIdList = organizationDetailRepository.getGardenIds(orgId);
        List<MongoCustomerDetailWoDTO> mongoDataResults = new ArrayList<>();
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();
        User user = userService.findUserByEntityId(entityId);
        if (gardenIdList != null) {
            String ids = String.join(",", gardenIdList);
            String dynamicMappingUrl = MONGO_BASE_URL + "/workOrder/getSubscriptionListByVariantIdsAndAccountId?variantIds=" + ids + "&acctId=" + user.getAcctId();
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));

            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues != null) {
                response.put("code", HttpStatus.OK);
                response.put("message", Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage());
                List<MongoCustomerDetailWoDTO> mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();
                Optional<UserSubscriptionTemplateWoDTO> optionalUserDetail = userRepository.findUsersAndLocationByAccountId(Arrays.asList(user.getAcctId())).stream().findFirst();
                if (optionalUserDetail.isPresent() && mongoDataList.size() > 0) {
                    UserSubscriptionTemplateWoDTO userDetailSql = optionalUserDetail.get();
                    mongoDataList.stream().map(mongoData ->
                            {
                                mongoData.setFirstName(userDetailSql.getFirstName());
                                mongoData.setLastName(userDetailSql.getFirstName());
                                mongoData.setCustomerType(userDetailSql.getCustomerType());
                                mongoData.setEntityId(userDetailSql.getEntityId());
                                mongoData.setEntityName(userDetailSql.getEntityName());
                                mongoData.setRegion(userDetailSql.getRegion());
                                mongoData.setProfileUrl(userDetailSql.getProfileUrl());
                                mongoDataResults.add(mongoData);
                                return mongoData;
                            }
                    ).collect(Collectors.toList());
                } else {
                    response.put("code", HttpStatus.NOT_FOUND);
                    response.put("message", "Subscriptions not found");
                }
            } else {
                response.put("code", HttpStatus.NOT_FOUND);
                response.put("message", "Subscriptions not found");
            }
            response.put("data", mongoDataResults);
            return response;
        }
        return response;
    }

    /**
     * @param mongoCustomerDetailWoDTO
     * @return
     * @author Sana
     * @Updated by kashif
     */
    @Override
    public Map<String, String> validateTicketCustomer(MongoCustomerDetailWoDTO mongoCustomerDetailWoDTO) {
        Map<String, String> map = new HashMap<>();
        String variantId = mongoCustomerDetailWoDTO.getVariantId();
        //String subId =  mongoCustomerDetailWoDTO.getSubId();
        List<OrganizationDetail> organizationDetailList = null;

        boolean validationStatus = false;
        Organization organization = null;
        for (OrganizationDetail organizationDetail : organizationDetailList) {

            organization = organizationService.findById(organizationDetail.getOrgId());
            if (organization.getSubType().equals(AppConstants.DISTRIBUTION_CENTER))
                validationStatus = true;

        }

        if (!organizationDetailList.isEmpty() && validationStatus) {
            map.put("message", "Can associate workOrder with this customer");
            map.put("code", HttpStatus.OK.toString());
            map.put("data", OrganizationMapper.toOrganizationDTO(organization).toString());
        } else {
            map.put("message", "Can n't associate workOrder with this customer");
            map.put("code", HttpStatus.NOT_FOUND.toString());
            map.put("data", null);
        }
        return map;
    }

    @Override
    public Map<String, Object> getSubscriptionsByEntityId(Long entityId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SubscriptionDetailTemplate> subscriptionDetailList = subscriptionRepository.findSubsByEntityId(entityId);
            if (subscriptionDetailList != null && !subscriptionDetailList.isEmpty()) {
                List<CustomerSubscriptionDTO> customerSubscriptionDTOList = SubscriptionMapping.toCustomerSubscriptionDTOs(subscriptionDetailList);
                return Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Subscriptions for entityId returned successfully", customerSubscriptionDTOList);
            }
        } catch (Exception ex) {
            return Utility.generateResponseMap(response, HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage(), null);
        }
        return Utility.generateResponseMap(response, HttpStatus.NO_CONTENT.toString(), "No subscriptions exist for this entityId = " + entityId, null);
    }

    @Override
    public BaseResponse getCustomerAndGardenDetailListBySourceType(String sourceType) {
        List<WorkOrderCustomerDetailTile> workOrderDetailTiles = null;
        List<WorkOrderGardenDetailTile> workOrderGardenDetailTiles = null;
        try {
            if (sourceType.equalsIgnoreCase(AppConstants.WorkOrderManagement.CUSTOMER_REQUEST) || sourceType.equalsIgnoreCase(AppConstants.WorkOrderManagement.CUSTOMER_SUPPORT)) {
                workOrderDetailTiles = WorkOrderManagementMapper.toWorkOrderCustomerDetailTiles(conversationHeadRepository.getCustomerListBySourceType());
            } else if (sourceType.equalsIgnoreCase(AppConstants.WorkOrderManagement.SERVICE_REQUEST)) {
                workOrderGardenDetailTiles = WorkOrderManagementMapper.toWorkOrderGardenDetailTiles(conversationHeadRepository.getGardenListBySourceType());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return (workOrderDetailTiles == null && workOrderGardenDetailTiles == null) ?
                BaseResponse.builder().code(HttpStatus.PRECONDITION_FAILED.value()).message(AppConstants.INVALID_PARAMETER).build() :
                BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data((workOrderDetailTiles != null) ? workOrderDetailTiles : workOrderGardenDetailTiles).build();
    }

    @Override
    public BaseResponse getBusinessUnitInformation(String workOrderId) {
        BusinessUnitInfoTile businessUnitInfoTile = null;
        try {
            businessUnitInfoTile = conversationHeadRepository.findBusinessUnitInformationBySubsId(workOrderId);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " (Invalid Work order id) " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(businessUnitInfoTile).build();


    }

    @Override
    public BaseResponse getAvailableEmployeesForWorkOrder(Long businessUnitId, String workOrderId, String searchWord) {
        List<DefaultUserGroupResponseDTO> result = new ArrayList<>();
        try {
            result = userGroupRepository.getAllAvailableEmployeesForWorkOrder(businessUnitId, workOrderId, searchWord);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    @Override
    public BaseResponse getAssignedEmployeeWordOrderLists(String workOrderId, String searchWord, Integer size, int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, size);
        EmployeeManagementPaginationTile result = new EmployeeManagementPaginationTile();
        try {
            Page<EmployeeManagementTile> employeeManagementTiles = organizationRepository.getAllSubOrgEmployeeWordOrderLists(workOrderId, searchWord, pageable);
            result.setTotalPages(employeeManagementTiles.getTotalPages());
            result.setTotalElements(employeeManagementTiles.getTotalElements());
            List<EmployeeManagementTile> data = employeeManagementTiles.getContent();
            result.setData(data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    @Override
    public ResponseEntity<?> resolveOrCloseWorkOrderList(List<ConversationHeadDTO> conversationHeadDTOS) {
        List<ConversationHeadDTO> conversationHeadDTOList = new ArrayList<>();
        List<ConversationHead> conversationHeadDToUpdate = new ArrayList<>();
        try {
            List<Long> conversationHeadIds = conversationHeadDTOS.stream().map(ConversationHeadDTO::getId).collect(Collectors.toList());
            List<ConversationHead> conversationHeadList = conversationHeadRepository.findAllById(conversationHeadIds);
            for (ConversationHeadDTO conversationHeadDTO : conversationHeadDTOS) {
                Optional<ConversationHead> conversationHeadOptional = conversationHeadList.stream().filter(i -> i.getId().longValue() == conversationHeadDTO.getId().longValue()).findFirst();
                if (conversationHeadOptional.isPresent()) {
                    ConversationHead conversationHead = conversationHeadOptional.get();
                    conversationHead.setStatus(conversationHeadDTO.getStatus());
                    conversationHead.setRemarks(conversationHeadDTO.getRemarks());
                    conversationHeadDToUpdate.add(conversationHead);
                    conversationHeadDTOList.add(ConversationHeadMapper.toConversationHeadDTOCustom(conversationHead));
                }
            }
            if (conversationHeadDToUpdate.size() > 0) {
                conversationHeadRepository.saveAll(conversationHeadDToUpdate);
            } else if (conversationHeadDTOList.size() == 0 || conversationHeadDToUpdate.size() == 0) {
                return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data not updated");
            }
        } catch (ForbiddenException | NotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data not updated");
        }
        return utility.buildSuccessResponse(HttpStatus.OK, "Data updated successfully", conversationHeadDTOList);
    }

    @Override
    public BaseResponse addOrUpdateWorkOrderV2(String workOrder, String requestType, String entityRoleIds, Long businessUnitId, String privLevel, Long compKey, List<MultipartFile> files) {
        BaseResponse response = new BaseResponse();
        try {
            String projectId = getOrCreateProjectId(businessUnitId, privLevel, compKey);
            String workOrderId = processWorkOrder(workOrder, requestType, projectId, privLevel, compKey);

            if (workOrderId != null) {
                User loggedInUser = userService.getLoggedInUser();
                JSONObject workOrderObj = new JSONObject(workOrder);
                JSONObject extractedWorkOrderPart = extractWorkOrderPart(workOrderObj);
                addOrUpdateConversationHead(loggedInUser, workOrderId, projectId, extractedWorkOrderPart, workOrderObj);
                if (entityRoleIds != null) {
                    manageWorkOrderResources(loggedInUser, workOrderId, entityRoleIds);
                }
                if (files != null) {
                    addAttachmentsInWorkOrder(workOrderId, compKey, files);
                }
                return buildSuccessResponse(workOrderId);
            } else {
                return buildErrorResponse(AppConstants.WorkOrderManagement.WORK_ORDER_NOT_FOUND_ERROR);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return buildErrorResponse(AppConstants.WorkOrderManagement.WORK_ORDER_PROCESSING_ERROR + e.getMessage());
        }
    }

    private String getOrCreateProjectId(Long businessUnitId, String privLevel, Long compKey) {
        OrganizationDetail organizationDetail = organizationDetailService.findByBusinessUnitId(businessUnitId);
        if (organizationDetail == null) {
            String projectId = dataExchange.createWorkOrderBoard(privLevel, compKey);
            organizationDetailService.saveOrUpdate(businessUnitId, projectId);
            return projectId;
        }
        return organizationDetail.getMongoRefId();
    }

    private String processWorkOrder(String workOrder, String requestType, String projectId, String privLevel, Long compKey) {
        com.solar.api.tenant.model.BaseResponse response = dataExchange.updateSectionContent(projectId, requestType, workOrder, privLevel, compKey);
        if (requestType.equalsIgnoreCase(AppConstants.OrganizationManagement.SAVE) && response != null && response.getCode() == HttpStatus.OK.value()) {
            return response.getMessage().split("=")[1].trim();
        } else if (requestType.equalsIgnoreCase(AppConstants.OrganizationManagement.UPDATE)) {
            return extractWorkOrderId(new JSONObject(workOrder));
        }
        return null;
    }

    private BaseResponse buildSuccessResponse(String message) {
        return BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    private BaseResponse buildErrorResponse(String message) {
        return BaseResponse.builder()
                .code(HttpStatus.CONFLICT.value())
                .message(message)
                .build();
    }

    private static JSONObject extractWorkOrderPart(JSONObject jsonObject) {
        // Directly access the 'content' object
        if (jsonObject.has(AppConstants.WorkOrderManagement.CONTENT)) {
            JSONObject contentObj = jsonObject.getJSONObject(AppConstants.WorkOrderManagement.CONTENT);
            if (contentObj.has(AppConstants.WorkOrderManagement.CONTENT)) {
                JSONArray contentArray = contentObj.getJSONArray(AppConstants.WorkOrderManagement.CONTENT);
                if (contentArray.length() > 0) {
                    JSONObject firstContentObj = contentArray.getJSONObject(0);
                    if (firstContentObj.has(AppConstants.WorkOrderManagement.WORK_ORDER)) {
                        JSONArray workOrderArray = firstContentObj.getJSONArray(AppConstants.WorkOrderManagement.WORK_ORDER);
                        if (workOrderArray.length() > 0) {
                            return workOrderArray.getJSONObject(0);
                        }
                    }
                }
            }
        }
        return null;
    }


    private static String extractWorkOrderId(JSONObject jsonObject) {
        if (jsonObject.has(AppConstants.WorkOrderManagement.CONTENT)) {
            JSONObject contentObj = jsonObject.getJSONObject(AppConstants.WorkOrderManagement.CONTENT);
            if (contentObj.has(AppConstants.WorkOrderManagement.CONTENT)) {
                JSONArray contentArray = contentObj.getJSONArray(AppConstants.WorkOrderManagement.CONTENT);
                if (!contentArray.isEmpty()) {
                    JSONObject firstContentObj = contentArray.getJSONObject(0);
                    if (firstContentObj.has(AppConstants.WorkOrderManagement.WORK_ORDER)) {
                        JSONArray workOrderArray = firstContentObj.getJSONArray(AppConstants.WorkOrderManagement.WORK_ORDER);
                        if (!workOrderArray.isEmpty()) {
                            JSONObject workOrder = workOrderArray.getJSONObject(0);
                            if (workOrder.has("_id") && workOrder.getJSONObject("_id").has("$oid")) {
                                return workOrder.getJSONObject("_id").getString("$oid");
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    private Long getJsonKeyValue(String json, String key) {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.optLong(key);
    }

    private void addOrUpdateConversationHead(User loggedInUser, String workOrderId, String projectId, JSONObject workOrderObj, JSONObject parentObj) {
        ConversationHead conversationHead = conversationHeadRepository.findBySubscriptionIdAndProductId(workOrderId, projectId);

        String channel = Utility.getStringValue(workOrderObj, AppConstants.WorkOrderManagement.CHANNEL);
        String variantId = extractVariantId(parentObj);
        String message = Utility.getStringValue(workOrderObj, AppConstants.WorkOrderManagement.NAME);
        String summary = Utility.getStringValue(workOrderObj, AppConstants.WorkOrderManagement.WORK_ORDER_SUMMARY);
        String priority = Utility.getStringValue(workOrderObj, AppConstants.WorkOrderManagement.PRIORITY);
        String status = Utility.getStringValue(workOrderObj, AppConstants.WorkOrderManagement.STATUS);
        String category = Utility.getStringValue(workOrderObj, AppConstants.WorkOrderManagement.TYPE);
        String coverage = Utility.getStringValue(workOrderObj, AppConstants.WorkOrderManagement.CONTRACT_TYPE);

        Long raisedBy = loggedInUser.getAcctId();
        Long parentId = Utility.getLongValue(workOrderObj, AppConstants.WorkOrderManagement.TICKET_NUMBER);
        Long sourceId = determineSourceId(category, parentObj, variantId);
        Double estimatedHours = Utility.getDoubleValue(workOrderObj, AppConstants.WorkOrderManagement.ESTIMATED_HOURS);
        Long acctId = userLevelPrivilegeService.UserLevelPrivilegeByEntityId(sourceId).getUser().getAcctId();
        LocalDateTime plannedDate = Utility.getDateTimeValue(workOrderObj, AppConstants.WorkOrderManagement.PLANNED_DATE_TIME);

        if (conversationHead != null) {
            conversationHead = updateExistingConversationHead(conversationHead, message, summary, priority, status, category, estimatedHours, plannedDate);
        } else {
            conversationHead = buildNewConversationHead(channel, message, summary, priority, raisedBy, sourceId, status, category, workOrderId, variantId, projectId, coverage, acctId, parentId, estimatedHours, plannedDate);
        }
        conversationHeadRepository.save(conversationHead);
    }

    private Long extractEntityId(JSONObject jsonObject) {
        JSONArray contentArray = jsonObject.getJSONObject(AppConstants.WorkOrderManagement.CONTENT)
                .getJSONArray(AppConstants.WorkOrderManagement.CONTENT);
        if (contentArray.length() > 0) {
            JSONObject firstContentObj = contentArray.getJSONObject(0);
            return Utility.getLongValue(firstContentObj, "entityId");
        }
        return null;
    }

    private String extractVariantId(JSONObject jsonObject) {
        JSONArray contentArray = jsonObject.getJSONObject(AppConstants.WorkOrderManagement.CONTENT)
                .getJSONArray(AppConstants.WorkOrderManagement.CONTENT);
        if (contentArray.length() > 0) {
            JSONObject firstContentObj = contentArray.getJSONObject(0);
            return Utility.getStringValue(firstContentObj, "variantId");
        }
        return null;
    }

    private Long determineSourceId(String category, JSONObject workOrderObj, String variantId) {
        if (isCustomerRequestOrSupport(category)) {
            return extractEntityId(workOrderObj);
        } else {
            ExtDataStageDefinition extDataStageDefinition = extDataStageDefinitionRepository.findTopByRefId(variantId);
            Long entityRoleId = getJsonKeyValue(extDataStageDefinition.getExtJson(), Constants.RATE_CODES.projectOwnerEntityRoleId);
            return entityRoleService.findById(entityRoleId).getEntity().getId();
        }
    }

    private boolean isCustomerRequestOrSupport(String category) {
        return AppConstants.WorkOrderManagement.CUSTOMER_REQUEST.equalsIgnoreCase(category)
                || AppConstants.WorkOrderManagement.CUSTOMER_SUPPORT.equalsIgnoreCase(category);
    }

    private ConversationHead updateExistingConversationHead(ConversationHead conversationHead, String message, String summary, String priority, String status, String category, Double estimatedHours, LocalDateTime plannedDate) {
        conversationHead.setMessage(message);
        conversationHead.setSummary(summary);
        conversationHead.setPriority(priority);
        conversationHead.setStatus(status);
        conversationHead.setCategory(category);
        conversationHead.setEstimatedHours(estimatedHours);
        conversationHead.setPlannedDate(plannedDate);
        return conversationHead;
    }

    private ConversationHead buildNewConversationHead(String channel, String message, String summary, String priority, Long raisedBy, Long sourceId, String status, String category, String workOrderId, String variantId, String projectId, String coverage, Long acctId, Long parentId, Double estimatedHours, LocalDateTime plannedDate) {
        return ConversationHead.builder()
                .channel(channel)
                .customerId(acctId)
                .message(message) //WorkOrder title
                .summary(summary) //WorkOrder description
                .parentRequestId(parentId)
                .priority(priority)
                .raisedBy(raisedBy)
                .sourceId(String.valueOf(sourceId))
                .status(status)
                .category(category)
                .subscriptionId(workOrderId)
                .sourceType(AppConstants.WorkOrderManagement.WORK_ORDER)
                .variantId(variantId)
                .productId(projectId)
                .coverage(coverage)
                .estimatedHours(estimatedHours)
                .plannedDate(plannedDate)
                .build();
    }

    public void manageWorkOrderResources(User loggedInUser, String workOrderId, String entityRoleIds) {
        List<Long> entityRoleIdList = Arrays.stream(entityRoleIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        UserGroup userGroup = getUserGroup(loggedInUser, workOrderId);
        List<EntityGroup> existingEntityGroups = entityGroupService.findAllByUserGroup(userGroup);

        Set<Long> existingEntityRoleIds = existingEntityGroups.stream()
                .map(entityGroup -> entityGroup.getEntityRole().getId())
                .collect(Collectors.toSet());
        //Deleting the entityGroups that were removed from the front end
        List<EntityGroup> toUpdate = existingEntityGroups.stream()
                .filter(entityGroup -> !entityRoleIdList.contains(entityGroup.getEntityRole().getId()))
                .peek(entityGroup -> {
                    entityGroup.setIsDeleted(true);
                    entityGroup.setStatus(false);
                })
                .collect(Collectors.toList());

        entityGroupService.saveAll(toUpdate);

        Set<Long> newEntityRoleIds = entityRoleIdList.stream()
                .filter(id -> !existingEntityRoleIds.contains(id))
                .collect(Collectors.toSet());
        resourceService.addResources(userGroup, new ArrayList<>(newEntityRoleIds));
    }

    private UserGroup getUserGroup(User loggedInUser, String workOrderId) {
        UserGroup userGroup = userGroupService.findByRefIdAndRefTypeAndStatusAndIsDeleted(workOrderId, AppConstants.WorkOrderManagement.WORK_ORDER, true, false);
        if (userGroup == null) {
            UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(loggedInUser.getAcctId());
            userGroup = userGroupService.save(UserGroup.builder()
                    .userGroupName(AppConstants.WorkOrderManagement.WORK_ORDER.concat("_".concat(workOrderId)))
                    .userGroupType(Constants.USER_GROUP.STATUS_CLOSED)
                    .createdBy(String.valueOf(userLevelPrivilege.getEntity().getId()))
                    .updatedBy(String.valueOf(userLevelPrivilege.getEntity().getId()))
                    .refId(workOrderId)
                    .refType(AppConstants.WorkOrderManagement.WORK_ORDER)
                    .status(true)
                    .isActive(true)
                    .isDeleted(false)
                    .build());
        }
        return userGroup;
    }

    private void addAttachmentsInWorkOrder(String workOrderId, Long compKey, List<MultipartFile> files) throws URISyntaxException, IOException, StorageException {
        List<DocuLibrary> docuLibraryList = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
            String documentName = multipartFile.getOriginalFilename().replaceAll("\\s", "_");

            String uri = storageService.storeInContainer(multipartFile, appProfile, "tenant/"
                            + compKey + AppConstants.PROJECT_DOCUMENT_PATH,
                    documentName, compKey
                    , false);
            Double fileSizeInKB = Double.valueOf(multipartFile.getSize() / 1024);
            Double fileSizeInMB = fileSizeInKB / 1024;
            docuLibraryList.add(DocuLibrary.builder()
                    .docuName(multipartFile.getName())
                    .codeRefType(AppConstants.WorkOrderManagement.WORK_ORDER)
                    .codeRefId(workOrderId)
                    .uri(uri)
                    .size(fileSizeInMB + "MB")
                    .docuType(multipartFile.getContentType())
                    .visibilityKey(true)
                    .referenceTime(timeStamp)
                    .build());
        }
        docuLibraryService.saveAll(docuLibraryList);
    }

    @Override
    public BaseResponse getSubscriptionInformation(String workOrderId) {
        SubscriptionInformation subscriptionInformation = null;
        SubscriptionInformationTile subscriptionInformationTile = null;
        try {
            subscriptionInformation = conversationHeadRepository.findSubscriptionsInformation(workOrderId);
            subscriptionInformationTile = SubscriptionMapping.convertToSubscriptionInformationTile(subscriptionInformation);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " (Invalid Work order id) " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(subscriptionInformationTile).build();
    }


    @Override
    public BaseResponse getWorkOrderRequestorInfoBySourceType(String sourceType, String workOrderId) {
        List<WorkOrderCustomerDetailTile> workOrderDetailTiles = null;
        try {
            if (sourceType.equalsIgnoreCase(AppConstants.WorkOrderManagement.CUSTOMER_REQUEST) || sourceType.equalsIgnoreCase(AppConstants.WorkOrderManagement.CUSTOMER_SUPPORT)) {
                workOrderDetailTiles = conversationHeadRepository.getRequesterInfoListBySourceType(workOrderId);
            } else if (sourceType.equalsIgnoreCase(AppConstants.WorkOrderManagement.SERVICE_REQUEST)) {
                workOrderDetailTiles = conversationHeadRepository.getSourceManagerListBySourceType(workOrderId);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        if (workOrderDetailTiles == null || workOrderDetailTiles.isEmpty()) {
            return BaseResponse.builder().code(HttpStatus.PRECONDITION_FAILED.value()).message(AppConstants.INVALID_PARAMETER).build();
        } else {
            return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(workOrderDetailTiles).build();
        }
    }

    @Override
    public BaseResponse getAllWorkOrderList(Long compKey, String groupBy, String groupByName, String status, String type, String requesterType, String requesterIds, String agentIds, String billable, Integer pageSize, Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        WorkOrderManagementPaginationTile result = new WorkOrderManagementPaginationTile();
        try {
            WorkOrderManagementGroupBy groupByType = WorkOrderManagementGroupBy.get(groupBy);
            Page<WorkOrderManagementTemplate> workOrderManagementTemplates = getWorkOrderGroupByResult(groupBy, groupByName, status, type, requesterType, requesterIds, agentIds, billable, groupByType, pageable);
            result.setTotalPages(workOrderManagementTemplates.getTotalPages());
            result.setTotalElements(workOrderManagementTemplates.getTotalElements());
            List<WorkOrderManagementTile> data = (!groupBy.equalsIgnoreCase(groupByType.NONE.getType()) && groupByName == null) ?
                    WorkOrderManagementMapper.toWorkOrderManagementTilesGroupBy(workOrderManagementTemplates.getContent()) :
                    WorkOrderManagementMapper.toWorkOrderManagementTiles(workOrderManagementTemplates.getContent());
            result.setData(data);
            result.setGroupBy(groupBy);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    private Page<WorkOrderManagementTemplate> getWorkOrderGroupByResult(String groupBy, String groupByName, String status, String type, String requesterType, String requesterIds, String agentIds, String billable, WorkOrderManagementGroupBy groupByType, Pageable pageable) {
        Page<WorkOrderManagementTemplate> workOrderTemplates = null;
        Boolean requesterIdIsPresent = requesterIds != null ? true : false;
        Boolean agentIdIsPresent = agentIds != null ? true : false;
        List<Long> requesterIdList = requesterIdIsPresent ? Arrays.asList(requesterIds.split(",")).stream().map(Long::parseLong).collect(Collectors.toList()) : Collections.emptyList();
        List<Long> agentIdList = agentIdIsPresent ? Arrays.asList(agentIds.split(",")).stream().map(Long::parseLong).collect(Collectors.toList()) : Collections.emptyList();
        if (!groupBy.equalsIgnoreCase(WorkOrderManagementGroupBy.NONE.getType()) && groupByName != null) {
            workOrderTemplates = conversationHeadRepository.findAllWorkOrderManagementTemplate(groupBy, groupByName, status, type, requesterType, requesterIdList, requesterIdIsPresent, agentIdList, agentIdIsPresent, billable, true, pageable);
        } else {
            switch (groupByType) {
                case NONE:
                    workOrderTemplates = conversationHeadRepository.findAllWorkOrderManagementTemplate(null, null, status, type, requesterType, requesterIdList, requesterIdIsPresent, agentIdList, agentIdIsPresent, billable, false, pageable);
                    break;
                case STATUS:
                case BOARD:
                case TYPE:
                case REQUESTER:
                case REQUESTER_TYPE:
                case BILLABLE:
                case SUPPORT_AGENT:
                    workOrderTemplates = conversationHeadRepository.findAllWorkOrderManagementTemplateGroupByList(groupByType.getType(), false, pageable);
                    break;
            }
        }
        return workOrderTemplates;
    }

    @Override
    public BaseResponse getAllWorkOrderInformation(String workOrderId) {
        WorkOrderInformationTile data = null;
        try {
            data = WorkOrderManagementMapper.toWorkOrderInformationTile(conversationHeadRepository.getAllWorkOrderInformation(workOrderId));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(data).build();
    }

    @Override
    public BaseResponse getWorkOrderManagementFilterDropDown() {
        WorkOrderManagementFilterDTO data = null;
        try {
            data = WorkOrderManagementMapper.toWorkOrderManagementFilterDTO(conversationHeadRepository.findWorkOrderManagementFilterDropDown());

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(data).build();
    }

    @Override
    public BaseResponse getTicketInformation(Long headId) {
        ConversationHeadDTO data = null;
        try {
            data = WorkOrderManagementMapper.toConversationHeadDTO(conversationHeadRepository.getTicketInformation(headId));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(data).build();
    }
}