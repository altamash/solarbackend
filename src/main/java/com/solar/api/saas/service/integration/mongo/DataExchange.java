package com.solar.api.saas.service.integration.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.helper.Message;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.integration.mongo.response.subscription.*;
import com.solar.api.saas.service.integration.mongo.response.subscription.transStage.TransStageTempDTO;
import com.solar.api.tenant.mapper.tiles.projectmanagement.ProjectManagementFilterDTO;
import com.solar.api.tenant.mapper.tiles.projectmanagement.ProjectManagementPaginationTile;
import com.solar.api.tenant.mapper.tiles.weatherTile.GardenDetail;
import com.solar.api.tenant.mapper.workOrder.MongoCustomerDetailWoDTO;
import com.solar.api.tenant.mapper.workOrder.MongoCustomerDetailWoMasterDTO;
import com.solar.api.tenant.model.BaseResponse;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.stage.monitoring.VariantCustomerSubsDTO;
import com.solar.api.tenant.model.weather.WeatherApiHistory;
import com.solar.api.tenant.repository.TenantConfigRepository;
import com.solar.api.tenant.service.weather.WeatherApiHistoryService;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

@Getter
@Setter
@ApplicationScope
@Component
public class DataExchange {

    @Value("${app.mongoBaseUrl}")
    private String mongoBaseUrl;
    @Value("${app.storage.marketplacePublicContainer}")
    private String storageContainer;
    @Value("${app.storage.container}")
    private String storageContainerPrivate;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private String VARIANTS_GET_URL;
    private String VARIANTS_ALL_PARAMS_GET_URL;
    private String VARIANTS_CAT_PARAMS_GET_URL;
    private String VARIANTS_STATUS_PARAMS_GET_URL;
    private String SUBSCRIPTION_MAPPING_GET_URL;
    private String SUBSCRIPTIONS_GET_URL;
    private String SUBSCRIPTION_MAPPING_BY_VARIANT_GET_URL;
    private String SUBSCRIPTION_SAVE_OR_UPDATE_URL;
    private String SUBSCRIPTION_SAVE_OR_UPDATE_URL_MONGO;
    private String VARIANT_SAVE_OR_UPDATE_URL;
    private String VARIANT_WITH_SUBSCRIPTIONS;
    private String FIND_ACTIVE_SUBSCRIPTIONS;
    private String FIND_ALL_MERGED_MEASURES_BY_SUBSCRIPTION;
    private String FIND_ALL_SUBSCRIPTIONS_DETAILS;
    private String FIND_ALL_FILTERED_MEASURES_AND_SUBSCRIPTION;
    private String CUSTOMER_INVERTER_VARIANT_SUBS;
    private String FIND_ACTIVE_SUBSCRIPTION_COUNT;
    private String FIND_ACTIVE_VARIANT_COUNT;
    private String DISABLE_VARIANT;
    private String SET_DISABLE_DATE_VARIANT;
    private String SET_DISABLE_DATE_PRODUCT;
    private String REMOVE_DISABLE_DATE_VARIANT;
    private String REMOVE_DISABLE_DATE_PRODUCT;
    private String DISABLE_PRODUCT_BY_PRODUCTID;
    private String GET_ALL_MEASURES_BY_SUB_IDS;
    private String SHOW_PROJECT_LISTINGS;
    private String SHOW_CHILD_PROJECT_LISTINGS;
    private String UPDATE_PROJECT_STATUSES;
    private String SHOW_PROJECT_LISTINGS_WITH_FILTERS;
    private String SHOW_ALL_PRODUCTS;
    private String ACTIVATE_PROJECTION;
    private String WASH_HTML;
    private String HTML_TO_PDF;
    private String SHOW_WORK_ORDER_TEMPLATE;
    private String CREATE_WORK_ORDER_BOARD;
    private String UPDATE_SECTION_CONTENT;
    private String GET_PROJECT_MANAGEMENT_FILTER_DROPDOWN;

    public DataExchange() {
    }

    @PostConstruct
    private void init() {
        VARIANTS_GET_URL = mongoBaseUrl + "/product/tenant/getVariantsByCategoryAndStatus";
        VARIANTS_ALL_PARAMS_GET_URL = mongoBaseUrl + "/product/tenant/getVariantsByCategoryAndStatus?category={category}&status={status}";
        VARIANTS_CAT_PARAMS_GET_URL = mongoBaseUrl + "/product/tenant/getVariantsByCategoryAndStatus?category={category}";
        VARIANTS_STATUS_PARAMS_GET_URL = mongoBaseUrl + "/product/tenant/getVariantsByCategoryAndStatus?status={status}";
        SUBSCRIPTION_MAPPING_GET_URL = mongoBaseUrl + "/product/tenant/getSubscriptionMappingById/%s";
        SUBSCRIPTIONS_GET_URL = mongoBaseUrl + "/product/tenant/getSubscriptionsByCriteria";
        SUBSCRIPTION_MAPPING_BY_VARIANT_GET_URL = mongoBaseUrl + "/product/tenant/getVariantMappingById/%s";
        SUBSCRIPTION_SAVE_OR_UPDATE_URL = mongoBaseUrl + "/product/createCollectionByGardenIdInTenant/%s";
        SUBSCRIPTION_SAVE_OR_UPDATE_URL_MONGO = mongoBaseUrl + "/product/tenant/createSubscriptionCollectionByVariantId/%s/%s";
        VARIANT_SAVE_OR_UPDATE_URL = mongoBaseUrl + "/product/createCollectionByProductIdInTenant/%s";
        VARIANT_WITH_SUBSCRIPTIONS = mongoBaseUrl + "/product/billing/variantsWithSubscriptions";
        FIND_ACTIVE_SUBSCRIPTIONS = mongoBaseUrl + "/product/billing/findAllActiveSubscriptions/%s/?projectionFields={projectionFields}&subscriptionIds={subscriptionIds}";
        FIND_ALL_MERGED_MEASURES_BY_SUBSCRIPTION = mongoBaseUrl + "/product/findSubscriptionAndVariantBySubId/%s";
        FIND_ALL_SUBSCRIPTIONS_DETAILS = mongoBaseUrl + "/product/showAllSubscriptionDetails";
        FIND_ALL_FILTERED_MEASURES_AND_SUBSCRIPTION = mongoBaseUrl + "/product/tenant/getPWSubscriptionsAndMeasures";
        FIND_ACTIVE_SUBSCRIPTION_COUNT = mongoBaseUrl + "/product/v1/getActiveCount?variantCollection={variantCollection}";
        FIND_ACTIVE_VARIANT_COUNT = mongoBaseUrl + "/product/v1/getActiveCount?productCollection={productCollection}";
        DISABLE_VARIANT = mongoBaseUrl + "/product/v1/disableVariant?productId={productId}&variantId={variantId}";
        SET_DISABLE_DATE_VARIANT = mongoBaseUrl + "/product/v1/setDisableDate?productId={productId}&variantId={variantId}";
        SET_DISABLE_DATE_PRODUCT = mongoBaseUrl + "/product/v1/setDisableDate?productId={productId}";
        REMOVE_DISABLE_DATE_VARIANT = mongoBaseUrl + "/product/v1/removeDisableDate?productId={productId}&variantId={variantId}";
        REMOVE_DISABLE_DATE_PRODUCT = mongoBaseUrl + "/product/v1/removeDisableDate?productId={productId}";
        CUSTOMER_INVERTER_VARIANT_SUBS = "/product/inverter/customerInverterVariants";
        DISABLE_PRODUCT_BY_PRODUCTID = mongoBaseUrl + "/product/disableProductByProductId";
        GET_ALL_MEASURES_BY_SUB_IDS = mongoBaseUrl + "/product/billing/getCustomerAndProductMeasuresBySubIds?subscriptionIds={subscriptionIds}";
        SHOW_PROJECT_LISTINGS = mongoBaseUrl + "/project/showProjectListings?pageNumber={pageNumber}&pageSize={pageSize}&groupBy={groupBy}&loggedInUserAcctId={loggedInUserAcctId}&status={status}&template={template}&type={type}&owner={owner}&createdAt={createdAt}&searchWords={searchWords}";
        SHOW_CHILD_PROJECT_LISTINGS = mongoBaseUrl + "/project/showProjectListings?pageNumber={pageNumber}&pageSize={pageSize}&groupBy={groupBy}&name={name}&loggedInUserAcctId={loggedInUserAcctId}&status={status}&template={template}&type={type}&owner={owner}&createdAt={createdAt}&searchWords={searchWords}";
        UPDATE_PROJECT_STATUSES = mongoBaseUrl + "/project/updateProjectStatus/v1";
        SHOW_PROJECT_LISTINGS_WITH_FILTERS = mongoBaseUrl + "/project/getAllProjectListingsWithFilters/v1?pageNumber={pageNumber}&pageSize={pageSize}&status={status}&template={template}&type={type}&owner={owner}&createdAt={createdAt}";
        SHOW_ALL_PRODUCTS = mongoBaseUrl + "/product/showAllProducts";
        ACTIVATE_PROJECTION = mongoBaseUrl + "/product/v1/activateProjection?variantId={variantId}&projectionId={projectionId}&active={active}";
        WASH_HTML = "https://www.htmlcorrector.com/ajax-calls/paste/";
        HTML_TO_PDF = "https://api.pdfendpoint.com/v1/convert/playground";
        SHOW_WORK_ORDER_TEMPLATE = mongoBaseUrl + "/workOrder/showWorkOrderTemplate/%s";
        CREATE_WORK_ORDER_BOARD = mongoBaseUrl + "/workOrder/createWorkOrderBoard/%s";
        UPDATE_SECTION_CONTENT = mongoBaseUrl + "/project/updateSectionContent/%s/%s";
        GET_PROJECT_MANAGEMENT_FILTER_DROPDOWN = mongoBaseUrl + "/project/getFilterDropDown/v1";
    }

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Gson gson;
    @Autowired
    private MasterTenantRepository masterTenantRepository;

    @Autowired
    private StorageService storageService;
    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    @Autowired
    private WeatherApiHistoryService weatherApiHistoryService;
    /*@PostConstruct
    public void init() {
        String subId = "62b2cff4609ad53891c35355";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        ResponseEntity<SubscriptionMapping[]> staticValues =
                WebUtils.submitRequest(HttpMethod.GET, String.format(STATIC_MAPPINGS_URL, subId), null, headers, SubscriptionMapping[].class);
        staticMappings = Arrays.asList(staticValues.getBody());
        ResponseEntity<SubscriptionMapping[]> dynamicValues =
                WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL, subId), null, headers, SubscriptionMapping[].class);
        calculationMappings = Arrays.asList(dynamicValues.getBody());
    }*/


    public List<Variant> getVariants(String category, String status, String tenantName) {
        List<Variant> variants = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            ResponseEntity<String> response = null;
            if (category != null && status != null) {
                response =
                        WebUtils.submitRequest(HttpMethod.GET, VARIANTS_ALL_PARAMS_GET_URL, null, headers, String.class, category, status);
            } else if (category != null) {
                response =
                        WebUtils.submitRequest(HttpMethod.GET, VARIANTS_CAT_PARAMS_GET_URL, null, headers, String.class, category);
            } else if (status != null) {
                response =
                        WebUtils.submitRequest(HttpMethod.GET, VARIANTS_STATUS_PARAMS_GET_URL, null, headers, String.class, status);
            } else {
                response =
                        WebUtils.submitRequest(HttpMethod.GET, VARIANTS_GET_URL, null, headers, String.class);
            }
            variants = Arrays.asList(new ObjectMapper().readValue(response.getBody(), Variant[].class));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return variants;
    }

    public SubscriptionMapping getSubscriptionMapping(String subscriptionId, String tenantName) {
        SubscriptionMapping subscriptionMapping = new SubscriptionMapping();
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            ResponseEntity<String> response =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(SUBSCRIPTION_MAPPING_GET_URL, subscriptionId),
                            null, headers, String.class);
//            subscription = objectMapper.readValue(response.getBody(), Subscription.class);
            subscriptionMapping = new ObjectMapper().readValue(measuresDefaultValuesCastToStringType(response.getBody()), SubscriptionMapping.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return subscriptionMapping;
    }

    // TODO: implement
    public Subscription getSubscriptionById(String subscriptionId, String tenantName) {
        Subscription subscription = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            ResponseEntity<String> response =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(SUBSCRIPTION_MAPPING_GET_URL, subscriptionId),
                            null, headers, String.class);
//            subscription = objectMapper.readValue(response.getBody(), Subscription.class);
            subscription = new ObjectMapper().readValue(response.getBody(), Subscription.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return subscription;
    }

    // TODO: get Subscription list by header and/or measure values
    public List<Subscription> getSubscriptions(String tenantName, Object... criteria) {
        Subscription[] subscriptions = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            ResponseEntity<String> response =
                    WebUtils.submitRequest(HttpMethod.GET, SUBSCRIPTIONS_GET_URL, null, headers, String.class, criteria);
//            subscriptions = objectMapper.readValue(response.getBody(), Subscription[].class);
            subscriptions = new ObjectMapper().readValue(response.getBody(), Subscription[].class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Arrays.asList(subscriptions);
    }

    // TODO: implement
    public List<String> findIdsBySubscriptionStatus(String status) {
        return Collections.emptyList();
    }

    public VariantSubscriptionMapping getSubscriptionMappingsByVariant(String variantId, String tenantName) {
        VariantSubscriptionMapping variantSubscriptionMapping = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            ResponseEntity<String> response =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(SUBSCRIPTION_MAPPING_BY_VARIANT_GET_URL, variantId),
                            null, headers, String.class);
//            subscription = objectMapper.readValue(response.getBody(), Subscription.class);
            variantSubscriptionMapping = new ObjectMapper().readValue(response.getBody(), VariantSubscriptionMapping.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return variantSubscriptionMapping;
    }

    // TODO: needs implementation
    public VariantSubscriptionMapping getSubscriptionMappingsByVariantIdsIn(List<String> variantIds, String tenantName) {
        VariantSubscriptionMapping variantSubscriptionMapping = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            ResponseEntity<String> response =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(SUBSCRIPTION_MAPPING_BY_VARIANT_GET_URL, variantIds),
                            null, headers, String.class);
//            subscription = objectMapper.readValue(response.getBody(), Subscription.class);
            variantSubscriptionMapping = new ObjectMapper().readValue(response.getBody(), VariantSubscriptionMapping.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return variantSubscriptionMapping;
    }

    // TODO: needs implementation
    public VariantSubscriptionMapping getSubscriptionMappingsByVariantCode(String variantCode, String tenantName) {
        VariantSubscriptionMapping variantSubscriptionMapping = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            ResponseEntity<String> response =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(SUBSCRIPTION_MAPPING_BY_VARIANT_GET_URL, variantCode),
                            null, headers, String.class);
//            subscription = objectMapper.readValue(response.getBody(), Subscription.class);
            variantSubscriptionMapping = new ObjectMapper().readValue(response.getBody(), VariantSubscriptionMapping.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return variantSubscriptionMapping;
    }

    public List<ProductDTO> getVariantsWithSubscriptions(String tenantName) {
        List<ProductDTO> productDTOList = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            ResponseEntity<String> response =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(VARIANT_WITH_SUBSCRIPTIONS),
                            null, headers, String.class);
            productDTOList = Arrays.asList(new ObjectMapper().readValue(response.getBody(), ProductDTO[].class));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return productDTOList;
    }

    public List<SubscriptionDetailDTO> getAllSubscriptionsDetails(String tenantName) {
        List<SubscriptionDetailDTO> subscriptionDTOList = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            ResponseEntity<String> response =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(FIND_ALL_SUBSCRIPTIONS_DETAILS),
                            null, headers, String.class);
            subscriptionDTOList = Arrays.asList(new ObjectMapper().readValue(response.getBody(), SubscriptionDetailDTO[].class));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return subscriptionDTOList;
    }

    public List<String> getActiveSubscriptions(String projection, String subscriptionIds, String variantId) {
        List<String> activeIds = null;
        List<Subscription> subscriptions = null;
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        try {
            ResponseEntity<String> response =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(FIND_ACTIVE_SUBSCRIPTIONS, variantId), null, headers, String.class, projection, subscriptionIds);
            subscriptions = Arrays.asList(new ObjectMapper().readValue(response.getBody(), Subscription[].class));
            activeIds = subscriptions.stream().map(x -> x.getId().getOid()).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return activeIds;
    }


    public BaseResponse updateSubscription(Subscription subscription, String tenantName) {
        BaseResponse baseResponse = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            MultiValueMap<String, Object> formDataMap = new LinkedMultiValueMap<>();
            formDataMap.add("subsObject", new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).writeValueAsString(subscription));
            ResponseEntity<BaseResponse> response =
                    WebUtils.submitRequest(HttpMethod.POST, String.format(SUBSCRIPTION_SAVE_OR_UPDATE_URL, "update"), formDataMap, headers, BaseResponse.class);
            baseResponse = response.getBody();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return baseResponse;
    }

    public BaseResponse updateSubscription(SubscriptionOutput subscription, String tenantName) {
        BaseResponse baseResponse = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            MultiValueMap<String, Object> formDataMap = new LinkedMultiValueMap<>();
            Gson gson = new GsonBuilder().serializeNulls().create();
            formDataMap.add("subsObject", gson.toJson(subscription));
            ResponseEntity<BaseResponse> response =
                    WebUtils.submitRequest(HttpMethod.POST, String.format(SUBSCRIPTION_SAVE_OR_UPDATE_URL, "update"), formDataMap, headers, BaseResponse.class);
            baseResponse = response.getBody();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return baseResponse;
    }


    public BaseResponse updateMongoSubscription(String variantId, Subscription subscription, String tenantName) {
        BaseResponse baseResponse = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            MultiValueMap<String, Object> formDataMap = new LinkedMultiValueMap<>();
            formDataMap.add("subscriptionObject", new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).writeValueAsString(subscription));
            ResponseEntity<BaseResponse> response =
                    WebUtils.submitRequest(HttpMethod.POST, String.format(SUBSCRIPTION_SAVE_OR_UPDATE_URL_MONGO, variantId, "update"), formDataMap, headers, BaseResponse.class);
            baseResponse = response.getBody();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return baseResponse;
    }

    public BaseResponse updateVariant(Variant variant, String tenantName) {
        BaseResponse baseResponse = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            MultiValueMap<String, Object> formDataMap = new LinkedMultiValueMap<>();
            formDataMap.add("productObject", new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).writeValueAsString(variant));
            ResponseEntity<BaseResponse> response =
                    WebUtils.submitRequest(HttpMethod.POST, String.format(VARIANT_SAVE_OR_UPDATE_URL, "update"), formDataMap, headers, BaseResponse.class);
            baseResponse = response.getBody();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return baseResponse;
    }

    public SubscriptionMapping getAllMergedMeasuresBySubscription(String subscriptionId, String tenantName) {
        SubscriptionMapping subscriptionMapping = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            ResponseEntity<String> response =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(FIND_ALL_MERGED_MEASURES_BY_SUBSCRIPTION, subscriptionId),
                            null, headers, String.class);
            subscriptionMapping = new ObjectMapper().readValue(response.getBody(), SubscriptionMapping.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return subscriptionMapping;
    }

    public MongoSubscriptionPMMasterDTO getAllFilteredMeasuresAndSubscriptions(String tenantName, String rateCodes) {
        MongoSubscriptionPMMasterDTO subscriptions = null;

        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Collections.singletonList(tenantName));
            ResponseEntity<String> response = WebUtils.submitRequest(HttpMethod.GET, String.format(FIND_ALL_FILTERED_MEASURES_AND_SUBSCRIPTION +
                    "?codeIds=" + rateCodes), null, headers, String.class);

            LOGGER.info(response.toString());
            subscriptions = new ObjectMapper().readValue(response.getBody(), MongoSubscriptionPMMasterDTO.class);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return subscriptions;
    }

    public static void main(String[] a) throws JsonProcessingException {
        DataExchange dataExchange = new DataExchange();
        dataExchange.getCustomerAndProductMeasuresBySubIds("63b80646c61f630b446a024d");
        List<Variant> variants = dataExchange.getVariants(null, "active", "ec1001");
        VariantSubscriptionMapping variantSubscriptionMapping = dataExchange.getSubscriptionMappingsByVariant("6303671efc802c5bfe78940d", "ec1001");

        SubscriptionMapping subscriptionMapping = dataExchange.getSubscriptionMapping("6303a323c28ce71d4c496815", "ec1001");

        Subscription subscription = subscriptionMapping.getSubscription();
        MeasureType srembalMeasure =
                subscription.getMeasures().getByCustomer().stream().filter(m -> m.getCode().equals("S_REMBAL")).findFirst().orElse(null);
        srembalMeasure.setDefaultValue("500");
        MeasureType sdscpMeasure =
                subscription.getMeasures().getByCustomer().stream().filter(m -> m.getCode().equals("S_DSCP")).findFirst().orElse(null);
        sdscpMeasure.setDefaultValue(".1");
        MeasureType stpreMeasure =
                subscription.getMeasures().getByCustomer().stream().filter(m -> m.getCode().equals("S_TPRE")).findFirst().orElse(null);
        stpreMeasure.setDefaultValue("1001");
        MeasureType sssdtMeasure =
                subscription.getMeasures().getByCustomer().stream().filter(m -> m.getCode().equals("S_SSDT")).findFirst().orElse(null);
        sssdtMeasure.setDefaultValue("{{NOW()}}");
        BaseResponse subscriptionResponse = dataExchange.updateSubscription(subscription, "ec1001");
        System.out.println(subscriptionResponse.toString());

        Variant variant = subscriptionMapping.getVariant();
        MeasureType ssrteMeasure =
                variant.getMeasures().getByProduct().stream().filter(m -> m.getCode().equals("S_SRTE")).findFirst().orElse(null);
        ssrteMeasure.setDefaultValue("[[VYR2020]]");
        ssrteMeasure.setSeq(6);
        MeasureType sopyrMeasure =
                variant.getMeasures().getByProduct().stream().filter(m -> m.getCode().equals("S_OPYR")).findFirst().orElse(null);
        sopyrMeasure.setDefaultValue("1");
        sopyrMeasure.setSeq(4);
        sssdtMeasure =
                variant.getMeasures().getByProduct().stream().filter(m -> m.getCode().equals("S_SSDT")).findFirst().orElse(null);
        sssdtMeasure.setDefaultValue("{{NOW()}}");
        BaseResponse variantResponse = dataExchange.updateVariant(variant, "ec1001");
        System.out.println(variantResponse.toString());
    }

    public Integer getActiveCount(String productCollection, String variantCollection) {
        Integer count = 0;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            ResponseEntity<String> response = null;
            if (productCollection != null) {
                response =
                        WebUtils.submitRequest(HttpMethod.GET, FIND_ACTIVE_VARIANT_COUNT, null, headers, String.class, productCollection);
            } else if (variantCollection != null) {
                response =
                        WebUtils.submitRequest(HttpMethod.GET, FIND_ACTIVE_SUBSCRIPTION_COUNT, null, headers, String.class, variantCollection);
            }
            count = Integer.valueOf(response.getBody().toString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return count;
    }

    public String disableVariant(String productId, String variantId) {
        String message = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            ResponseEntity<String> response = null;
            if (productId != null && variantId != null) {
                response =
                        WebUtils.submitRequest(HttpMethod.POST, DISABLE_VARIANT, null, headers, String.class, productId, variantId);
            }
            message = response.getBody().toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return message;
    }

    public BaseResponse disableProduct(String productId, String batchJobExecutionDate) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("productId", productId);
        map.add("batchJobExecutionDate", new Date().toString());
        ResponseEntity<BaseResponse> response = null;
        try {
            response = WebUtils.submitFormDataRequest(HttpMethod.POST, DISABLE_PRODUCT_BY_PRODUCTID, map, headers, BaseResponse.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return response.getBody();
    }

    public String setDisableDate(String productId, String variantId) {
        String message = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            ResponseEntity<String> response = null;
            if (productId != null && variantId != null) {
                response = WebUtils.submitRequest(HttpMethod.POST, SET_DISABLE_DATE_VARIANT, null, headers, String.class, productId, variantId);
            } else if (productId != null && variantId == null) {
                response = WebUtils.submitRequest(HttpMethod.POST, SET_DISABLE_DATE_PRODUCT, null, headers, String.class, productId);
            }
            message = response.getBody().toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return message;
    }

    public String removeDisableDate(String productId, String variantId) {
        String message = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            ResponseEntity<String> response = null;
            if (productId != null && variantId != null) {
                response = WebUtils.submitRequest(HttpMethod.POST, REMOVE_DISABLE_DATE_VARIANT, null, headers, String.class, productId, variantId);
            } else if (productId != null && variantId == null) {
                response = WebUtils.submitRequest(HttpMethod.POST, REMOVE_DISABLE_DATE_PRODUCT, null, headers, String.class, productId);
            }
            message = response.getBody().toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return message;
    }

    public List<VariantDTO> customerInverterVariantAndSubscriptions(VariantCustomerSubsDTO variantCustomerSubsDTO, String tenantName) {
        List<VariantDTO> variantDTOS = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(tenantName));
            headers.put("Content-Type", Arrays.asList("application/json"));
            headers.put("Accept", Arrays.asList("application/json"));
            if (Objects.nonNull(variantCustomerSubsDTO)) {
                ResponseEntity<String> response =
                        WebUtils.submitRequest(HttpMethod.POST, mongoBaseUrl + CUSTOMER_INVERTER_VARIANT_SUBS, variantCustomerSubsDTO, headers, String.class);
                variantDTOS = Arrays.asList(new ObjectMapper().readValue(response.getBody(), VariantDTO[].class));
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
        return variantDTOS;
    }

    public Map getAllCustomerListSubscriptionGroupByAccountId() {
        List<MongoCustomerDetailWoDTO> mongoDataList = new ArrayList<>();
        Map response = new HashMap();
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(FIND_ALL_SUBSCRIPTIONS_DETAILS), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues.getBody().getMongoCustomerDetailWoDTO() != null) {
                mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();
                Utility.generateResponseMap(response, HttpStatus.OK.toString(), Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage(), mongoDataList.stream().collect(
                        Collectors.groupingBy(MongoCustomerDetailWoDTO::getAccountId)));
            } else {
                Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Subscriptions not found", null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Subscriptions not found", null);
        }
        return response;
    }

    public Map getAllCustomerListSubscriptionGroupByCreatedAt() {
        List<MongoCustomerDetailWoDTO> mongoDataList = new ArrayList<>();
        Map response = new HashMap();
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(FIND_ALL_SUBSCRIPTIONS_DETAILS), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues.getBody().getMongoCustomerDetailWoDTO() != null) {
                mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();
                List<String> tempList = mongoDataList.stream().map(MongoCustomerDetailWoDTO::getCreatedAt).collect(Collectors.toList());
                mongoDataList.forEach(x ->
                        x.setCreatedAt(getDateString(x.getCreatedAt())));
                response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage(), mongoDataList.stream().collect(
                        Collectors.groupingBy(x -> (YearMonth.from(LocalDate.parse(x.getCreatedAt()))
                        ))));
            } else {
                Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Subscriptions not found", null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Subscriptions not found", null);
        }
        return response;
    }

    public Map getAllCustomerListSubscriptionGroupByVariant() {
        List<MongoCustomerDetailWoDTO> mongoDataList = new ArrayList<>();
        Map response = new HashMap();
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(FIND_ALL_SUBSCRIPTIONS_DETAILS), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues.getBody().getMongoCustomerDetailWoDTO() != null) {
                mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();

                Utility.generateResponseMap(response, HttpStatus.OK.toString(), Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage(), mongoDataList.stream().filter(data -> data.getSubAlias() != null).collect(
                        Collectors.groupingBy(MongoCustomerDetailWoDTO::getSubAlias)));
            } else {
                Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Subscriptions not found", null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Subscriptions not found", null);
        }
        return response;
    }

    private String getDateString(String date) {
        try {
            List<String> temp = Arrays.asList(date.split(" - "));
            if (Arrays.asList(date.split(" - ")).size() > 1) {
                String year = date.split(" - ")[0];
                String month = date.split(" - ")[1];
                String day = date.split(" - ")[2].split(" ")[0];
                date = year + "-" + month + "-" + day;
            }
            if (Arrays.asList(date.split(" ")).size() > 1) {
                date = date.split(" ")[0];
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return date;

    }

    public List<TransStageTempDTO> getCustomerAndProductMeasuresBySubIds(String subIds) {
        List<TransStageTempDTO> result = new ArrayList<>();
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            ResponseEntity<String> response = null;

            List<String> responseArray = null;
            if (subIds != null) {
                response = WebUtils.submitRequest(HttpMethod.GET, GET_ALL_MEASURES_BY_SUB_IDS, null, headers, String.class, subIds);
                responseArray = Arrays.asList(new ObjectMapper().readValue(response.getBody(), String[].class));
                for (String stringResponse : responseArray) {
                    result.add(new ObjectMapper().readValue(stringResponse, TransStageTempDTO.class));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    public BaseResponse createSubscriptionCollectionByVariantId(String requestType, String variantId, String subscriptionObject) {
        BaseResponse baseResponse = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            MultiValueMap<String, Object> formDataMap = new LinkedMultiValueMap<>();
            formDataMap.add("subscriptionObject", subscriptionObject);
            ResponseEntity<BaseResponse> response =
                    WebUtils.submitRequest(HttpMethod.POST, String.format(SUBSCRIPTION_SAVE_OR_UPDATE_URL_MONGO, variantId, requestType), formDataMap, headers, BaseResponse.class);
            baseResponse = response.getBody();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return baseResponse;
    }

    private String measuresDefaultValuesCastToStringType(String jsonStr) {
        JSONObject jsonObj = new JSONObject(jsonStr);
        JSONObject jsonMeasures = ((JSONObject) ((JSONObject) jsonObj.get("subscription")).get("measures"));
        JSONArray jsonByCustomer = jsonMeasures.getJSONArray("by_customer");
        Iterator<Object> itr = jsonByCustomer.iterator();
        while (itr.hasNext()) {
            Object obj = itr.next();
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.has("default_value")) {
                Object objDefaultValue = jsonObject.get("default_value");
                if (!objDefaultValue.toString().equalsIgnoreCase("null") &&
                        !(objDefaultValue instanceof String) && jsonObject.get("default_value") != null)
                    jsonObject.put("default_value", objDefaultValue.toString());
            } else {
                jsonObject.put("default_value", JSONObject.NULL);
            }
        }
        return jsonObj.toString();
    }


    public SubscriptionOutput measuresDefaultValuesCastToGivenFormat(Subscription subscriptionObj) {
        SubscriptionOutput subscriptionOutput = null;
        try {
            String json = gson.toJson(subscriptionObj);
            JSONObject jsonObj = new JSONObject(json);
            JSONObject jsonMeasures = ((JSONObject) jsonObj.get("measures"));
            JSONArray jsonByCustomer = jsonMeasures.getJSONArray("byCustomer");
            Iterator<Object> itr = jsonByCustomer.iterator();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            while (itr.hasNext()) {
                Object obj = itr.next();
                JSONObject jsonObject = (JSONObject) obj;
                if (!jsonObject.has("defaultValue")) {
                    jsonObject.put("defaultValue", JSONObject.NULL);
                }
                Object objDefaultValue = jsonObject.get("defaultValue");
                String format = jsonObject.get("format").toString();
                if (!objDefaultValue.equals(null) && !format.equalsIgnoreCase("TEXT")) {
                    //decimal
                    if (format.equalsIgnoreCase("NUM") && objDefaultValue.toString().contains(".")) {
                        jsonObject.put("defaultValue", Double.valueOf(objDefaultValue.toString()));
                    } else if (!objDefaultValue.equals(null) && format.equalsIgnoreCase("NUM")) {
                        jsonObject.put("defaultValue", Long.valueOf(objDefaultValue.toString()));
                    } else if (format.equalsIgnoreCase("DATE") && !objDefaultValue.toString().contains("$date")) {

                        SimpleDateFormat dateFormatObj = new SimpleDateFormat("yyyy-MM-dd");
                        jsonObject.put("defaultValue", dateFormatObj.parse(dateFormatObj.format(dateFormatObj.parse(objDefaultValue.toString()))));
                    } else if (!objDefaultValue.equals(null) && format.equalsIgnoreCase("DATE")) {
                        String dateStr = objDefaultValue.toString().replace("{\"$date\":\"", "");
                        dateStr = dateStr.replace("\"}", "");
                        jsonObject.put("defaultValue", dateFormat.parse(dateFormat.format(dateFormat.parse(dateStr))));
                    }
                }
            }
            MeasureOutput measureOutput = objectMapper.readValue(jsonMeasures.toString(), MeasureOutput.class);
            subscriptionOutput = objectMapper.readValue(jsonObj.toString(), SubscriptionOutput.class);
            subscriptionOutput.setMeasures(measureOutput);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return subscriptionOutput;
    }

    public ProjectManagementPaginationTile showProjectListings(Integer pageSize, Integer pageNumber, String groupBy, String name, String status, String template, String type, String owner, String createdAt, String searchWords, Long loggedInAcctUser) {
        BaseResponse baseResponse = null;
        ResponseEntity<String> response = null;
        ProjectManagementPaginationTile paginationDTO = new ProjectManagementPaginationTile<>();
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            if (name != null) {
                response = WebUtils.submitRequest(HttpMethod.GET, SHOW_CHILD_PROJECT_LISTINGS, null, headers, String.class, pageNumber, pageSize, groupBy, name, loggedInAcctUser, status, template, type, owner, createdAt, searchWords);
            } else {
                response = WebUtils.submitRequest(HttpMethod.GET, SHOW_PROJECT_LISTINGS, null, headers, String.class, pageNumber, pageSize, groupBy, loggedInAcctUser, status, template, type, owner, createdAt, searchWords);
            }
            paginationDTO = new ObjectMapper().readValue(response.getBody(), ProjectManagementPaginationTile.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return paginationDTO;
    }

    public Map updateProjectStatuses() {
        ResponseEntity<String> response = null;
        Map result = new HashMap();
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            response = WebUtils.submitRequest(HttpMethod.POST, UPDATE_PROJECT_STATUSES, null, headers, String.class);
            result = new ObjectMapper().readValue(response.getBody(), Map.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    @Deprecated
    public ProjectManagementPaginationTile showProjectListingsWithFilters(Integer size, Integer pageNumber, String status, String template, String type, String owner, String createdAt) {
        BaseResponse baseResponse = null;
        ResponseEntity<String> response = null;
        ProjectManagementPaginationTile paginationDTO = new ProjectManagementPaginationTile<>();
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            response = WebUtils.submitRequest(HttpMethod.GET, SHOW_PROJECT_LISTINGS_WITH_FILTERS, null, headers, String.class, pageNumber, size, status, template, type, owner, createdAt);
            paginationDTO = new ObjectMapper().readValue(response.getBody(), ProjectManagementPaginationTile.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return paginationDTO;
    }

    public ResponseEntity<String> showAllProducts() {
        ResponseEntity<String> response = null;
        String responseStr = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(Constants.SCHEMA.SAAS_SCHEMA));
            response = WebUtils.submitRequest(HttpMethod.GET, SHOW_ALL_PRODUCTS, null, headers, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            ArrayNode arrayNode = jsonNode.isArray() ? (ArrayNode) jsonNode : null;
            ArrayNode arrayNodeResponse = new ObjectMapper().createArrayNode();
            Iterator itr = arrayNode.iterator();
            while (itr.hasNext()) {
                ObjectNode jsonNode1 = (ObjectNode) itr.next();
                String productId = jsonNode1.get("_id").toString();
                productId = productId.replace("{\"$oid\":\"", "");
                productId = productId.replace("\"}", "");
                String directoryReference = "SAAS Environment/Marketplace/Products/" + productId + "/THMBNAIL/";
                List<String> urls = storageService.getBlobUrl(directoryReference, storageContainer);
                String thumbnailurl = null;
                if (urls != null && !urls.isEmpty()) {
                    Optional<String> url = urls.stream().findFirst();
                    if (url.isPresent()) {
                        thumbnailurl = url.get();
                    }
                }
                jsonNode1.put("url", thumbnailurl);
                arrayNodeResponse.add(jsonNode1);
            }
            JsonNode jsonNodeResponse = (JsonNode) arrayNodeResponse;
            responseStr = objectMapper.writeValueAsString(jsonNodeResponse);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return ResponseEntity.ok(responseStr);
    }

    public String activateProjection(String variantId, String projectionId, Boolean active) {
        String message = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            ResponseEntity<String> response = null;
            if (projectionId != null && variantId != null) {
                response =
                        WebUtils.submitRequest(HttpMethod.POST, ACTIVATE_PROJECTION, null, headers, String.class, variantId, projectionId, active);
            }
            message = response.getBody().toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return message;
    }

    public String washHtml(String html) {
        String baseResponse = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded; charset=UTF-8"));
            headers.put("Accept", Arrays.asList("application/json"));
            MultiValueMap<String, String> formDataMap = new LinkedMultiValueMap<>();
            formDataMap.add("Input", html);
            ResponseEntity<String> response =
                    WebUtils.submitRequest(HttpMethod.POST, WASH_HTML, formDataMap, headers, String.class);
            baseResponse = response.getBody();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return baseResponse;
    }

    public String htmlToPDFConversion(String html, String fileName, Long compKey) {
        String blobUrl = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded; charset=UTF-8"));
            headers.put("Accept", Arrays.asList("application/json"));
            headers.put("Authorization", Arrays.asList("Bearer f5a792b9ebff8b0871178a72472aa461167c448a75"));
            MultiValueMap<String, Object> formDataMap = new LinkedMultiValueMap<>();
            formDataMap.add("orientation", "vertical");
            formDataMap.add("page_size", "A4");
            formDataMap.add("http_headers", "{\n\t\"cache-control\": \"max-age=0\"\n}");
            formDataMap.add("viewport", "970x1400");
            formDataMap.add("use_print_media", true);
            formDataMap.add("wait_for_network", true);
            formDataMap.add("margin_top", "1cm");
            formDataMap.add("margin_bottom", "1cm");
            formDataMap.add("margin_right", "1cm");
            formDataMap.add("margin_left", "1cm");
            formDataMap.add("delivery_mode", "inline");
            formDataMap.add("filename", fileName);
            formDataMap.add("html", html);
            ResponseEntity<byte[]> response =
                    WebUtils.submitRequest(HttpMethod.POST, HTML_TO_PDF, formDataMap, headers, byte[].class);
            byte[] fileBytes = response.getBody();

            blobUrl = uploadToBlobStorage(fileBytes, storageContainerPrivate, "tenant/" + compKey + AppConstants.PROJECTION_REVENUE_PATH,
                    fileName, compKey);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return blobUrl;
    }

    private String uploadToBlobStorage(byte[] fileBytes, String container, String directory, String fileName, Long compKey) {
        try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
            String blobUrl = storageService.uploadInputStream(inputStream, (long) fileBytes.length,
                    container, directory, fileName,
                    compKey, false);

            return blobUrl;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to trigger third party weather api call.
     *
     * @param gardenDetail
     * @param weatherApiKey
     * @return
     */
    public String getWeatherData(GardenDetail gardenDetail, Optional<TenantConfig> weatherApiKey, Long days) {
        RestTemplate restTemplate = new RestTemplate();
        String response = null;
        String apiUrl = AppConstants.WEATHER_API_URL_FOR_SEVEN_DAYS_DATA + gardenDetail.getGeoLatitude() + "," + gardenDetail.getGeoLongitude()
                + "&days=" + days + "&key=" + weatherApiKey.get().getText() + "";
        WeatherApiHistory weatherApiHistory = WeatherApiHistory.builder().weatherApi(apiUrl).gardenId(gardenDetail.getRefId()).build();
        try {

            response = restTemplate.getForObject(apiUrl, String.class);
            weatherApiHistory.setMessage("Data Found");
            weatherApiHistory.setStatus("Success");

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            weatherApiHistory.setMessage(e.getMessage());
            weatherApiHistory.setStatus("Error");
        }
        weatherApiHistoryService.addWeatherApiHistory(weatherApiHistory);
        return response;

    }


    public String getHourlyData(GardenDetail gardenDetail, Optional<TenantConfig> weatherApiKey, Long days) {
        RestTemplate restTemplate = new RestTemplate();
        String response = null;
        try {
            String apiUrl = AppConstants.WEATHER_API_URL_FOR_SEVEN_DAYS_DATA + gardenDetail.getGeoLatitude() + "," + gardenDetail.getGeoLongitude()
                    + "&days=" + days + "&key=" + weatherApiKey.get().getText() + "";
            response = restTemplate.getForObject(apiUrl, String.class);
        } catch (Exception e) {
            LOGGER.error("Error while triggering third party api in data exchange", e);
        }
        return response;
    }

    public ResponseEntity showAcquisitionTemplate(String projectId, String privLevel) {

        ResponseEntity<String> staticValues = null;
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + AppConstants.SHOW_ACQUISITION_TEMPLATE_MONGO_API;
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        headers.put("Priv-Level", Arrays.asList(String.valueOf(privLevel)));
        try {
            staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL, projectId), null, headers, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staticValues;
    }

    public ResponseEntity showSectionContent(String projectId, String sectionId, String privLevel) {

        ResponseEntity<String> staticValues = null;
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + AppConstants.SHOW_SECTION_CONTENT_MONGO_API;
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        headers.put("Priv-Level", Arrays.asList(String.valueOf(privLevel)));
        headers.put("Section-id", Arrays.asList(String.valueOf(sectionId)));
        try {
            staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL, projectId), null, headers, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staticValues;
    }

    public BaseResponse saveOrUpdateAcquisitionProject(String template) {
        ResponseEntity<BaseResponse> response = null;
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + AppConstants.SAVE_ACQUISITION_PROJECT_IN_MONGO_API;
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("template", template);
        try {
            response = WebUtils.submitRequest(HttpMethod.POST, String.format(DYNAMIC_MAPPINGS_URL), map, headers, BaseResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error while calling mongo api", e);
        }
        return response.getBody();
    }

    public BaseResponse addOrUpdateAcquisitionSection(String sectionJson, String projectId, String requestType) {
        ResponseEntity<BaseResponse> response = null;
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + AppConstants.UPDATE_ACQUISITION_PROJECT_IN_MONGO_API;
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("sectionJson", sectionJson);
        try {
            response = WebUtils.submitRequest(HttpMethod.POST, String.format(DYNAMIC_MAPPINGS_URL, projectId, requestType), map, headers, BaseResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error while calling mongo api", e);
        }
        return response.getBody();
    }

    public ResponseEntity showHierarchySectionDetail(String projectId, String sectionId, String loggedInUserprivLevel, Long loggedInUserAcctId) {
        int privLevel = Integer.parseInt(loggedInUserprivLevel);
        ResponseEntity<String> staticValues = null;
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + AppConstants.SHOW_ACTIVITY_DETAIL_MONGO_API;
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        headers.put("Priv-Level", Arrays.asList(String.valueOf(privLevel)));
        try {
            staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL, projectId, sectionId), null, headers, String.class, loggedInUserAcctId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return staticValues;
    }

    public BaseResponse disableProjectionInMongo(String variantId, String subscriptionId, Boolean status, String tenantId) {
        ResponseEntity<BaseResponse> response = null;
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + AppConstants.DISABLE_PROJECTION_IN_MONGO;
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("variantId", variantId);
        map.add("projectionId", subscriptionId);
        map.add("active", String.valueOf(status));
        try {
            response = WebUtils.submitRequest(HttpMethod.POST, String.format(DYNAMIC_MAPPINGS_URL), map, headers, BaseResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error while calling mongo api", e);
        }
        return response.getBody();
    }

    public BaseResponse showWorkOrderTemplate(String privLevel, Long compKey) {
        ResponseEntity<String> staticValues = null;
        try {
            Optional<TenantConfig> tenantConfig = tenantConfigRepository.findByParameter(AppConstants.WorkOrderManagement.WORK_ORDER_TEMPLATE);
            if (!tenantConfig.isPresent()) {
                return BaseResponse.builder().code(HttpStatus.PRECONDITION_FAILED.value()).message(AppConstants.WorkOrderManagement.NO_TEMPLATE_FOUND_MESSAGE).build();
            }
            MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
            DBContextHolder.setTenantName(masterTenant.getDbName());
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            headers.put("Priv-Level", Arrays.asList(String.valueOf(privLevel)));
            staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(SHOW_WORK_ORDER_TEMPLATE, tenantConfig.get().getText()), null, headers, String.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).message(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(staticValues.getBody()).build();
    }

    public String createWorkOrderBoard(String privLevel, Long compKey) {
        ResponseEntity<String> staticValues = null;
        try {
            Optional<TenantConfig> tenantConfig = tenantConfigRepository.findByParameter(AppConstants.WorkOrderManagement.WORK_ORDER_TEMPLATE);
            MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
            DBContextHolder.setTenantName(masterTenant.getDbName());
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            headers.put("Priv-Level", Arrays.asList(String.valueOf(privLevel)));
            staticValues = WebUtils.submitRequest(HttpMethod.POST, String.format(CREATE_WORK_ORDER_BOARD, tenantConfig.get().getText()), null, headers, String.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return staticValues.getBody().toString();
    }

    public BaseResponse updateSectionContent(String projectId, String requestType, String workOrder, String privLevel, Long compKey) {
        ResponseEntity<BaseResponse> result = null;
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        try {
            MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
            DBContextHolder.setTenantName(masterTenant.getDbName());
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            headers.put("Priv-Level", Arrays.asList(String.valueOf(privLevel)));
            map.add("sectionJson", workOrder);
            result = WebUtils.submitRequest(HttpMethod.POST, String.format(UPDATE_SECTION_CONTENT, projectId, requestType), map, headers, BaseResponse.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result.getBody();
    }

    public ProjectManagementFilterDTO showProjectListingFilterDropDown() {
        BaseResponse baseResponse = null;
        ResponseEntity<String> response = null;
        ProjectManagementFilterDTO filter = new ProjectManagementFilterDTO();
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            response = WebUtils.submitRequest(HttpMethod.GET, GET_PROJECT_MANAGEMENT_FILTER_DROPDOWN, null, headers, String.class);
            filter = new ObjectMapper().readValue(response.getBody(), ProjectManagementFilterDTO.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return filter;
    }
}
