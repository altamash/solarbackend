package com.solar.api.saas.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.Constants;
import com.solar.api.helper.Message;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.service.StorageService;

import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.tenant.mapper.contract.LinkedContractDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.mongo.MongoCustomerSubscriptionDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.mongo.MongoCustomerSubscriptionMasterDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.mongo.PaginationDTO;
import com.solar.api.tenant.mapper.workOrder.UserSubscriptionTemplateWoDTO;
import com.solar.api.tenant.model.BaseResponse;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.repository.DocuLibraryRepository;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.contract.OrganizationDetailService;
import com.solar.api.tenant.service.controlPanel.ControlPanelService;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController("SubscriptionWrapperController")
@RequestMapping(value = "/subscriptionWrapper")
public class SubscriptionWrapperController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${app.mongoBaseUrl}")
    private String mongoBaseUrl;
    @Value("${app.storage.marketplacePublicContainer}")
    private String storageContainer;
    @Value("${app.storage.marketplacePublicContainer}")
    private String marketPlacePublicUrl;
    @Autowired
    private ControlPanelService controlPanelService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserService userService;
    @Autowired
    private StorageService storageService;
    @Autowired
    PhysicalLocationService physicalLocationService;
    @Autowired
    private Gson gson;
    @Autowired
    SubscriptionService subscriptionService;
    @Autowired
    OrganizationDetailService organizationDetailService;
    @Autowired
    private DocuLibraryRepository docuLibraryRepository;

    @Autowired
    private DataExchange dataExchange;

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showAllGardens", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity showAllGardens() {
        String DYNAMIC_MAPPINGS_URL = "https://simongo.azurewebsites.net/productsapi/product/showAllGardens";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        ResponseEntity<String> staticValues =
                WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL), null, headers, String.class);
        return staticValues;
    }

    @PreAuthorize("checkAccess()")
    @PostMapping(value = "/createSubscriptionByGardenIdInTenant/{requestType}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse createSubscriptionByGardenIdInTenant(@PathVariable String requestType,
                                                             @RequestParam(value = "subsObjectList", required = true) String subsObjectList) {
        String DYNAMIC_MAPPINGS_URL = "https://simongo.azurewebsites.net/productsapi/product/createSubscriptionCollectionByGardenIdInTenant/%s";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("subsObjectList", subsObjectList);
        ResponseEntity<BaseResponse> response = WebUtils.submitFormDataRequest(HttpMethod.POST, String.format(DYNAMIC_MAPPINGS_URL, requestType), map, headers, BaseResponse.class);
        return response.getBody();
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showAllSubscription", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<MongoCustomerSubscriptionDTO> showAllSubscription() {
        String DYNAMIC_MAPPINGS_URL = "https://simongo.azurewebsites.net/productsapi/product/showAllSubscription";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        ResponseEntity<MongoCustomerSubscriptionMasterDTO> staticValues =
                WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL), null, headers, MongoCustomerSubscriptionMasterDTO.class);
        List<MongoCustomerSubscriptionDTO> subscriptions = staticValues.getBody().getMongoCustomerSubscriptionDTO();
        return controlPanelService.formatSubscriptions(subscriptions);
    }

    @PreAuthorize("checkAccess()")
    @PostMapping(value = "/showSubscriptionDetail", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<String> showSubscriptionDetail(@RequestParam("subscriptions") String subscriptions) throws JsonProcessingException {
        return controlPanelService.formatSubscriptionMappings(subscriptions);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showSubscriptionsByVariantId/{variantId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public PaginationDTO showSubscriptionsByVariantId(@PathVariable("variantId") String variantId,
                                                      @RequestParam("pageNumber") int pageNumber,
                                                      @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/product/tenant/getAllSubscriptionByVariantId/%s";
        DYNAMIC_MAPPINGS_URL = String.format(DYNAMIC_MAPPINGS_URL, variantId);
        DYNAMIC_MAPPINGS_URL += "?pageNumber=" + pageNumber;
        PaginationDTO paginationDTO = PaginationDTO.builder().build();
        if (pageSize != null) {
            DYNAMIC_MAPPINGS_URL += "&pageSize=" + pageSize;
        }
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        ResponseEntity<MongoCustomerSubscriptionMasterDTO> staticValues =
                WebUtils.submitRequest(HttpMethod.GET, DYNAMIC_MAPPINGS_URL, null, headers, MongoCustomerSubscriptionMasterDTO.class);
        if (staticValues.getBody().getPaginationDTO().getSubscriptions() != null && staticValues.getBody().getPaginationDTO().getSubscriptions().size() > 0) {
            paginationDTO = staticValues.getBody().getPaginationDTO();
            List<MongoCustomerSubscriptionDTO> subscriptions = paginationDTO.getSubscriptions();
            List<Long> userIds = subscriptions.stream().map(MongoCustomerSubscriptionDTO::getAccountId).collect(Collectors.toList());
            List<UserSubscriptionTemplateWoDTO> users = userService.findUsersAndLocationByAccountId(userIds);
            if (users != null) {
                subscriptions.forEach(sub -> {
                    Date expiryDate = null;
                    Optional<UserSubscriptionTemplateWoDTO> userOptional = users.stream().filter(userObj -> userObj.getAccountId().longValue() == sub.getAccountId().longValue()).findFirst();
                    if (userOptional.isPresent()) {
                        UserSubscriptionTemplateWoDTO user = userOptional.get();
                        sub.setCustomerName(user.getFirstName() + " " + user.getLastName());
                        sub.setEntityType(user.getCustomerType());
                    }
                    if (sub.getExpiryDate() != null) {
                        expiryDate = sub.getExpiryDate();
                    }
                    sub.setStatus(subscriptionService.getSubscriptionStatus(sub.getActiveDate(), expiryDate));
                });
            }
            paginationDTO.setSubscriptions(subscriptions);
        }
        return paginationDTO;
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showGardenByProductIdAndGardenId", produces = {MediaType.APPLICATION_JSON_VALUE})
    public String showGardenByProductIdAndGardenId(@RequestParam("gardenId") String gardenId,
                                                   @RequestParam("productId") String productId) {
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/product/showGardenByProductIdAndGardenId?gardenId=" + gardenId + "&productId=" + productId;
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        ResponseEntity<String> staticValues =
                WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL), null, headers, String.class);

        String garden = staticValues.getBody().toString();
        JSONObject gardenObj = new JSONObject(garden);

        if (gardenObj.get("site_physical_locId") != null && !gardenObj.get("site_physical_locId").equals("null")) {
            PhysicalLocation siteLocation = physicalLocationService.findById(gardenObj.getLong("site_physical_locId"));
            gardenObj.put("site_physical_locName", siteLocation.getLocationName());
        }
        if (gardenObj.get("maging_physical_loc_id") != null && !gardenObj.get("maging_physical_loc_id").equals("null")) {
            PhysicalLocation magingLocation = physicalLocationService.findById(gardenObj.getLong("maging_physical_loc_id"));
            gardenObj.put("maging_physical_loc_name", magingLocation.getLocationName());
        }
        return gardenObj.toString();
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/showProductById/{productId}")
    public String showProductById(@PathVariable("productId") String productId) throws JsonProcessingException {
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/product/showProductById/%s";
        String directoryReference = "SAAS Environment/Marketplace/Products/" + productId + "/";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList("saas"));

        ResponseEntity<String> staticValue =
                WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL, productId), null, headers, String.class);
        List<String> urls = null;
        if (staticValue != null) {
            try {
                urls = storageService.getBlobUrl(directoryReference, marketPlacePublicUrl);
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (StorageException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        JSONObject productObj = new JSONObject(staticValue.getBody());
        productObj.remove("measures");
        productObj.put("productImagesUrl", urls);
        return productObj.toString();
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showAllProductsGarden", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> showAllProductsGarden(@RequestParam(value = "unitType", required = false) Long unitTypeId, @RequestParam("OrgId") Long OrgId) throws JsonProcessingException {
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();
        List<LinkedContractDTO> output = null;

        if (unitTypeId == null) {
            String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/product/showAllProductGardens";
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            ResponseEntity<String> staticValues =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL), null, headers, String.class);
            if (staticValues != null) {
                response.put("code", HttpStatus.OK);
                response.put("message", Message.ORG_DETAIL_GET_LINKED_CONTRACTS.getMessage());
            } else {
                response.put("code", HttpStatus.NOT_FOUND);
                response.put("message", "Linked Contracts not found");
            }
            output = organizationDetailService.getModifiedContracts(staticValues.getBody(), OrgId);
        } else {
            output = organizationDetailService.getModifiedContracts(null, OrgId, unitTypeId);
        }
        response.put("data", output);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/showGardensByProductId/{productId}")
    public ResponseEntity showGardensByProductId(@PathVariable("productId") String productId) throws JsonProcessingException {
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/product/showGardensByProductId/%s";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList("saas"));
        List<String> finalGardens = new ArrayList<>();
        ResponseEntity<String> staticValue =
                WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL, productId), null, headers, String.class);
        if (staticValue != null) {
            List<String> gardens = Arrays.asList(mapper.readValue(staticValue.getBody(), String[].class));
            for (String garden : gardens) {
                JSONObject gardenObj = new JSONObject(garden);
                List<DocuLibrary> docuLibraryList = docuLibraryRepository.findAllByCodeRefIdAndCodeRefType(gardenObj.getString("_id"), "VAR_THMB");
                gardenObj.put("gardenImagesUrl", docuLibraryList.stream().findFirst().get().getUri());
                finalGardens.add(gardenObj.toString());
            }
        }
        return ResponseEntity.ok(finalGardens.toString());
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/test/getAllMeasuresBy/{subIds}")
    public ResponseEntity getAllMeasuresBy(@RequestParam("subIds") String subIds) throws JsonProcessingException {
        return ResponseEntity.ok(dataExchange.getCustomerAndProductMeasuresBySubIds(subIds));
    }

    @PostMapping("/v2/createSubscriptionCollectionByVariantId/{variantId}/{requestType}")
    public ResponseEntity<?> createSubscriptionCollectionByVariantId(@PathVariable String variantId,
                                                                     @PathVariable String requestType,
                                                                     @RequestParam("subscriptionObject") String subscriptionObject,
                                                                     @RequestHeader("Comp-Key") Long compKey,
                                                                     @RequestParam(value = "isProjection", required = false, defaultValue = "false") Boolean isProjection
                                                                     ) {
        BaseResponse<Object> baseResponse = subscriptionService.createSubscriptionCollectionByVariantId(variantId, requestType, subscriptionObject, isProjection);
        return ResponseEntity.ok(baseResponse);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showAllProducts", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity showAllProducts() {
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl+"/product/showAllProducts";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(Constants.SCHEMA.SAAS_SCHEMA));
        ResponseEntity<String> staticValues = dataExchange.showAllProducts();
        return staticValues;
    }

    /*
    1. created_by : sana
    2. created_at :24/07/2023
    3. description : this api will return all the list of subscriptions with isProjection = true means projection type subscription
     */
    @GetMapping(value = "/showSubProjectionByVariantId/{variantId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public PaginationDTO showSubProjectionByVariantId(@PathVariable("variantId") String variantId,
                                                      @RequestParam("pageNumber") int pageNumber,
                                                      @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                      @RequestParam(value="isProjection", required = true, defaultValue = "true") Boolean isProjection
                                                      ) {
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/product/tenant/getAllSubscriptionByVariantId/%s";
        DYNAMIC_MAPPINGS_URL = String.format(DYNAMIC_MAPPINGS_URL, variantId);
        DYNAMIC_MAPPINGS_URL += "?pageNumber=" + pageNumber;
        PaginationDTO paginationDTO = PaginationDTO.builder().build();
        if (pageSize != null) {
            DYNAMIC_MAPPINGS_URL += "&pageSize=" + pageSize;
        }
        DYNAMIC_MAPPINGS_URL += "&isProjection=" + isProjection;
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        ResponseEntity<MongoCustomerSubscriptionMasterDTO> staticValues =
                WebUtils.submitRequest(HttpMethod.GET, DYNAMIC_MAPPINGS_URL, null, headers, MongoCustomerSubscriptionMasterDTO.class);
        if (staticValues.getBody().getPaginationDTO().getSubscriptions() != null && staticValues.getBody().getPaginationDTO().getSubscriptions().size() > 0) {
            paginationDTO = staticValues.getBody().getPaginationDTO();
            List<MongoCustomerSubscriptionDTO> subscriptions = paginationDTO.getSubscriptions();
                subscriptions.forEach(sub -> {
                    Date expiryDate = null;
                    if (sub.getExpiryDate() != null) {
                        expiryDate = sub.getExpiryDate();
                    }
                   String status =  subscriptionService.getSubscriptionStatus(sub.getActiveDate(), expiryDate);
                    sub.setStatus(status == null? sub.getStatus() : null);
                });
            paginationDTO.setSubscriptions(subscriptions);
        }
        return paginationDTO;
    }

}
