package com.solar.api.tenant.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.helper.service.SubscriptionRateCodes;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.module.com.solar.batch.service.StageMonitorService;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.*;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.mapper.subscription.SubscriptionInfoTemplate;
import com.solar.api.tenant.mapper.subscription.SubscriptionTemplate;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionMaintenanceDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionMapper;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionRateCodeDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.SubscriptionCountDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscriptionMapping.CustomerSubscriptionMappingMapper;
import com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.SubscriptionRateTypeMatrixMapper;
import com.solar.api.tenant.mapper.subscription.subscriptionType.SubscriptionTypeDTO;
import com.solar.api.tenant.mapper.subscription.subscriptionType.SubscriptionTypeMapper;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.BaseResponse;
import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.paymentDetailView.SearchParams;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.stage.monitoring.*;
import com.solar.api.tenant.model.subscription.*;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixDetail;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRatesDerived;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.ERole;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.repository.contract.ContractMappingRepository;
import com.solar.api.tenant.repository.contract.UserLevelPrivilegeRepository;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import com.solar.api.tenant.service.process.subscription.activation.SubscriptionActivation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionMapper.toUserDTOs;

@Service
//@Transactional("tenantTransactionManager")
public class SubscriptionServiceImpl implements SubscriptionService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;
    @Autowired
    private SubscriptionRateMatrixDetailRepository subscriptionRateMatrixDetailRepository;
    @Autowired
    private SubscriptionRateMatrixHeadRepository subscriptionRateMatrixHeadRepository;
    @Autowired
    private CustomerSubscriptionMappingRepository customerSubscriptionMappingRepository;
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SubscriptionRatesDerivedRepository subscriptionRatesDerivedRepository;
    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    CustomerSubscriptionsListRepository subscriptionsListRepositoryCustom;
    @Autowired
    private SubscriptionActivation subscriptionActivation;
    @Autowired
    private JobManagerTenantService jobManagerService;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private ExtDataStageDefinitionService extDataStageDefinitionService;
    @Autowired
    private StageMonitorService stageMonitorService;
    @Autowired
    private ObjectMapper objectMapper;

    //    SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
    SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.DEFAULT_DATE_FORMAT);


    // SubscriptionType ////////////////////////////////////////////////////////

    /**
     * @param subscriptionType
     * @return
     */
    @Override
    public SubscriptionType addOrUpdateSubscriptionType(SubscriptionType subscriptionType) {
        if (subscriptionType.getId() != null) {
            SubscriptionType subscriptionTypeData = subscriptionTypeRepository.findById(subscriptionType.getId())
                    .orElseThrow(() -> new NotFoundException(SubscriptionType.class, subscriptionType.getId()));
            subscriptionTypeData = SubscriptionTypeMapper.toUpdatedSubscriptionType(subscriptionTypeData,
                    subscriptionType);
            return subscriptionTypeRepository.save(subscriptionTypeData);
        }
        return subscriptionTypeRepository.save(subscriptionType);
    }

    /**
     * @param subscriptionTypes
     * @return
     */
    @Override
    public List<SubscriptionType> addSubscriptionTypes(List<SubscriptionType> subscriptionTypes) {
        return subscriptionTypeRepository.saveAll(subscriptionTypes);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public SubscriptionType findSubscriptionTypeById(Long id) {
        return subscriptionTypeRepository.findById(id).orElseThrow(() -> new NotFoundException(SubscriptionType.class, id));
    }

    @Override
    public SubscriptionType findSubscriptionTypeByCode(String code) {
        return subscriptionTypeRepository.findByCode(code);
    }

    @Override
    public List<SubscriptionType> findSubscriptionTypeByCodeIn(List<String> codes) {
        return subscriptionTypeRepository.findByCodeIn(codes);
    }

    /**
     * @return
     */
    @Override
    public SubscriptionType findSubscriptionTypeBySubscriptionName(String subscriptionName) {
        return subscriptionTypeRepository.findBySubscriptionName(subscriptionName);
    }

    @Override
    public List<SubscriptionTypeDTO> findAllSubscriptionTypesWithPrimaryGroup() {
        List<SubscriptionTypeDTO> finalList = new ArrayList<>();
        List<SubscriptionTypeDTO> list = subscriptionTypeRepository.findAllSubscriptionTypesWithPrimaryGroup();
        Map<String, List<SubscriptionTypeDTO>> map = list.stream().collect(Collectors.groupingBy(st -> st.getPrimaryGroup()));
        for (Map.Entry<String, List<SubscriptionTypeDTO>> obj : map.entrySet()) {
            finalList.add(new SubscriptionTypeDTO(obj.getKey(), obj.getValue().stream().map(SubscriptionTypeDTO::getCode).collect(Collectors.toList())));
        }
        return finalList;
    }

    @Override
    public List<SubscriptionType> findAllSubscriptionTypes(String status) {
        if (DBContextHolder.isLegacy()) {
            return subscriptionTypeRepository.findAll();
        }
        return Variant.toSubscriptionTypes(dataExchange.getVariants(null, status, DBContextHolder.getTenantName()));
    }

    /**
     * @param id
     */
    @Override
    public void deleteSubscriptionType(Long id) {
        subscriptionTypeRepository.deleteById(id);
    }

    /**
     *
     */
    @Override
    public void deleteAllSubscriptionTypes() {
        subscriptionTypeRepository.deleteAll();
    }

    // SubscriptionRateMatrixHead /////////////////////////////////////////////////

    /**
     * @param subscriptionRateMatrixHead
     * @return
     */
    @Override
    public SubscriptionRateMatrixHead addOrUpdateSubscriptionRateMatrixHead(SubscriptionRateMatrixHead subscriptionRateMatrixHead) {
        if (subscriptionRateMatrixHead.getId() != null) {
            SubscriptionRateMatrixHead rateMatrixHeadData =
                    subscriptionRateMatrixHeadRepository.findById(subscriptionRateMatrixHead.getId()).orElseThrow(() ->
                            new NotFoundException(SubscriptionRateMatrixHead.class, subscriptionRateMatrixHead.getId()));
            rateMatrixHeadData = SubscriptionRateTypeMatrixMapper.toUpdatedSubscriptionRateMatrix(rateMatrixHeadData,
                    subscriptionRateMatrixHead);
            return subscriptionRateMatrixHeadRepository.save(rateMatrixHeadData);
        }
        return subscriptionRateMatrixHeadRepository.save(subscriptionRateMatrixHead);
    }

    /**
     * @param subscriptionRateMatrixHeads
     * @return
     */
    @Override
    public List<SubscriptionRateMatrixHead> addSubscriptionRateMatrixHeads(List<SubscriptionRateMatrixHead> subscriptionRateMatrixHeads) {
        return subscriptionRateMatrixHeadRepository.saveAll(subscriptionRateMatrixHeads);
    }

    @Override
//    @Cacheable(value = "matrixHead")
    public SubscriptionRateMatrixHead findSubscriptionRateMatrixHeadById(Long subscriptionRateMatrixHeadId) {
        SubscriptionRateMatrixHead rateMatrixHead =
                subscriptionRateMatrixHeadRepository.findById(subscriptionRateMatrixHeadId)
                        .orElseThrow(() -> new NotFoundException(SubscriptionRateMatrixHead.class,
                                subscriptionRateMatrixHeadId));
        List<SubscriptionRateMatrixDetail> subscriptionRateMatrixDetails =
                subscriptionRateMatrixDetailRepository.findBySubscriptionRateMatrixId(rateMatrixHead.getId());
        rateMatrixHead.setSubscriptionRateMatrixDetails(subscriptionRateMatrixDetails);
        return rateMatrixHead;
    }

    /**
     * @param subscriptionTemplate
     * @return
     */
    @Override
//    @Cacheable(value = "matrixHead")
    public SubscriptionRateMatrixHead findSubscriptionRateMatrixHeadBySubscriptionTemplate(String subscriptionTemplate) {
        SubscriptionRateMatrixHead rateMatrixHead =
                subscriptionRateMatrixHeadRepository.findSubscriptionRateMatrixHeadBySubscriptionTemplate(subscriptionTemplate);
        List<SubscriptionRateMatrixDetail> subscriptionRateMatrixDetails =
                subscriptionRateMatrixDetailRepository.findBySubscriptionRateMatrixId(rateMatrixHead.getId());
        rateMatrixHead.setSubscriptionRateMatrixDetails(subscriptionRateMatrixDetails);

        List<MeasureDefinitionTenantDTO> definitionTenantDTOs = measureDefinitionOverrideService.findByIds(subscriptionRateMatrixDetails.stream().map(detail -> detail.getMeasureDefinitionId()).collect(Collectors.toList()));
        subscriptionRateMatrixDetails.forEach(detail -> {
            definitionTenantDTOs.stream().filter(dfn -> dfn.getId() != null
                            && detail.getMeasureDefinitionId() != null
                            && dfn.getId().longValue() == detail.getMeasureDefinitionId().longValue())
                    .findFirst()
                    .ifPresent(dfn -> {
                        detail.setMeasureDefinition(dfn);
                        detail.setMeasureDefinitionId(dfn.getId());
                    });
        });
        return rateMatrixHead;
    }

    @Override
//    @Cacheable(value = "matrixHeadList", key = "'matrixHeadList'")
    public List<SubscriptionRateMatrixHead> findSubscriptionRateMatrixHeadBySubscriptionCodeAndActive(String subscriptionCode, Boolean active) {
        if (subscriptionCode.equals("-1")) {
            List<String> subCodes = Lists.newArrayList("CSGF", "CSGR");
            return subscriptionRateMatrixHeadRepository.findBySubscriptionCodeInAndActive(subCodes, true);
        }
        return subscriptionRateMatrixHeadRepository.findBySubscriptionCodeAndActive(subscriptionCode, active);
    }

    @Override
//    @Cacheable(value = "matrixHeadList", key = "'matrixHeadList'")
    public List<SubscriptionRateMatrixHead> findSubscriptionRateMatrixHeadsByIdsIn(List<Long> ids) {
        return subscriptionRateMatrixHeadRepository.findByIdsIn(ids);
    }

    @Override
    public SubscriptionRateMatrixHead findByIdFetchDetails(Long subscriptionRateMatrixHeadId) {
        return subscriptionRateMatrixHeadRepository.findByIdFetchDetails(subscriptionRateMatrixHeadId);
    }

    @Override
    public List<SubscriptionRateMatrixHead> findAllSubscriptionRateMatrixHeadsFetchDetails() {
        return subscriptionRateMatrixHeadRepository.findAllFetchDetails();
    }

    @Override
//    @Cacheable(value = "matrixHeadList", key = "'matrixHeadList'")
    public List<SubscriptionRateMatrixHead> findAllSubscriptionRateMatrixHeads() {
        List<SubscriptionRateMatrixHead> subscriptionRateMatrixHeads = subscriptionRateMatrixHeadRepository.findAll();
        return subscriptionRateMatrixHeads;
    }

    /**
     * @param id
     */
    @Override
    public void deleteSubscriptionRateMatrixHead(Long id) {
        subscriptionRateMatrixHeadRepository.deleteById(id);
    }

    /**
     * Delete RateMatrixHead
     */
    @Override
    public void deleteAllSubscriptionRateMatrixHeads() {
        subscriptionRateMatrixHeadRepository.deleteAll();
    }

    // SubscriptionRateMatrixDetail /////////////////////////////////////////////////

    /**
     * @param subscriptionRateMatrixDetail
     * @return
     */
    @Override
    public SubscriptionRateMatrixDetail addOrUpdateSubscriptionRateMatrix(SubscriptionRateMatrixDetail subscriptionRateMatrixDetail) {
        if (subscriptionRateMatrixDetail.getId() != null) {
            SubscriptionRateMatrixDetail subscriptionTypeData = subscriptionRateMatrixDetailRepository.findById(
                    subscriptionRateMatrixDetail.getId()).orElseThrow(() -> new NotFoundException(
                    SubscriptionRateMatrixDetail.class, subscriptionRateMatrixDetail.getId()));
            subscriptionTypeData =
                    SubscriptionRateTypeMatrixMapper.toUpdatedSubscriptionRateMatrix(subscriptionTypeData,
                            subscriptionRateMatrixDetail);
            MeasureDefinitionTenantDTO billingDefinition =
                    measureDefinitionOverrideService.findMeasureDefinitionByCode(subscriptionRateMatrixDetail.getRateCode());
            if (billingDefinition != null) {
                subscriptionRateMatrixDetail.setMeasureDefinition(billingDefinition);
                subscriptionRateMatrixDetail.setMeasureDefinitionId(billingDefinition.getId());
            }
            return subscriptionRateMatrixDetailRepository.save(subscriptionTypeData);
        }
        MeasureDefinitionTenantDTO billingDefinition =
                measureDefinitionOverrideService.findMeasureDefinitionByCode(subscriptionRateMatrixDetail.getRateCode());
        if (billingDefinition != null) {
            subscriptionRateMatrixDetail.setMeasureDefinition(billingDefinition);
            subscriptionRateMatrixDetail.setMeasureDefinitionId(billingDefinition.getId());
        }
        return subscriptionRateMatrixDetailRepository.save(subscriptionRateMatrixDetail);
    }

    /**
     * @param subscriptionRateMatrixDetails
     * @return
     */
    @Override
    public List<SubscriptionRateMatrixDetail> add(List<SubscriptionRateMatrixDetail> subscriptionRateMatrixDetails) {
        return subscriptionRateMatrixDetailRepository.saveAll(subscriptionRateMatrixDetails);
    }

    @Override
//    @Cacheable(value = "matrixDetail")
    public SubscriptionRateMatrixDetail findSubscriptionRateMatrixDetailById(Long subscriptionRateMatrixDetailId) {
        SubscriptionRateMatrixDetail detail = subscriptionRateMatrixDetailRepository.findById(
                subscriptionRateMatrixDetailId).orElseThrow(() -> new NotFoundException(
                SubscriptionRateMatrixDetail.class, subscriptionRateMatrixDetailId));
        return detail;
    }

    @Override
//    @Cacheable(value = "matrixDetailList", key = "'matrixDetailList'")
    public List<SubscriptionRateMatrixDetail> findBySubscriptionRateMatrixId(Long id) {
        return subscriptionRateMatrixDetailRepository.findBySubscriptionRateMatrixId(id);
    }

    @Override
//    @Cacheable(value = "matrixDetail")
    public SubscriptionRateMatrixDetail findBySubscriptionRateMatrixIdAndRateCode(Long id, String code) {
        return subscriptionRateMatrixDetailRepository.findBySubscriptionRateMatrixIdAndRateCode(id, code);
    }

    @Override
    public List<String> findRateCodesBySubscriptionRateMatrixIdAndVaryByCustomer(Long subscriptionRateMatrixId,
                                                                                 Boolean varyByCustomer) {
        return subscriptionRateMatrixDetailRepository.findRateCodesBySubscriptionRateMatrixIdAndVaryByCustomer(subscriptionRateMatrixId, varyByCustomer);
    }

    @Override
    public List<UserDTO> findCustomerInverterSubscriptions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User LoggedInUser = userService.getLoggedInUser();
        List<Long> subscriptionTypes = new ArrayList<>();
        List<UserDTO> userDTOs = new ArrayList<>();
        subscriptionTypes.add(Constants.INVERTER_TYPES.SOLAX_PRODUCTION_RESIDENTIAL);
        subscriptionTypes.add(Constants.INVERTER_TYPES.SOLAX_PRODUCTION_COMMERCIAL);
        subscriptionTypes.add(Constants.INVERTER_TYPES.SOLIS_POWER_COMMERCIAL);
        subscriptionTypes.add(Constants.INVERTER_TYPES.SOLIS_POWER_RESIDENTIAL);
        subscriptionTypes.add(Constants.INVERTER_TYPES.GOODWE_POWER_RESIDENTIAL);
        subscriptionTypes.add(Constants.INVERTER_TYPES.GOODWE_POWER_COMMERCIAL);

        //this block to show logged in customer subscription
        if (EUserType.CUSTOMER.getName().equals(LoggedInUser.getUserType().getName().name())) {
            List<CustomerSubscription> cs = customerSubscriptionRepository.findCustomerInverterSubscription(subscriptionTypes, LoggedInUser.getAcctId());
            userDTOs.add(toUserDTOs(LoggedInUser, cs));
        } else {
            //this will be executed for admin
            List<User> users = userRepository.findCustomerInverterSubs(subscriptionTypes);
            for (User user : users) {
                List<CustomerSubscription> cs = customerSubscriptionRepository.findCustomerInverterSubscription(subscriptionTypes, user.getAcctId());
                userDTOs.add(toUserDTOs(user, cs));
            }
        }
        return userDTOs;
    }

    @Override
//    @Cacheable(value = "matrixDetail")
    public SubscriptionRateMatrixDetail findBySubscriptionCodeAndSubscriptionRateMatrixIdAndRateCode(String subscriptionCode, Long subscriptionRateMatrixId, String rateCode) {
        return subscriptionRateMatrixDetailRepository.findBySubscriptionCodeAndSubscriptionRateMatrixIdAndRateCode(subscriptionCode, subscriptionRateMatrixId, rateCode);
    }

    /**
     * @return
     */
    @Override
//    @Cacheable(value = "matrixDetailList", key = "'matrixDetailList'")
    public List<SubscriptionRateMatrixDetail> findAllSubscriptionRateMatrixDetails() {
        return subscriptionRateMatrixDetailRepository.findAll();
    }

    /**
     * @param rateCode
     * @return
     */
    @Override
//    @Cacheable(value = "matrixDetailList", key = "'matrixDetailList'")
    public List<SubscriptionRateMatrixDetail> findAllSubscriptionRateMatrixDetailsByRateCode(String rateCode) {
        List<SubscriptionRateMatrixDetail> subscriptionRateMatrixDetails =
                subscriptionRateMatrixDetailRepository.findByRateCode(rateCode);
        subscriptionRateMatrixDetails.forEach(subscriptionRateMatrixDetail -> {
            if (subscriptionRateMatrixDetail.getSubscriptionCode() != null) {

                MeasureDefinitionTenantDTO billingDefinition =
                        measureDefinitionOverrideService.findMeasureDefinitionByCode(rateCode);
                if (billingDefinition != null) {
                    subscriptionRateMatrixDetail.setMeasureDefinition(billingDefinition);
                    subscriptionRateMatrixDetail.setMeasureDefinitionId(billingDefinition.getId());
                }
            }
        });
        return subscriptionRateMatrixDetails;
    }

    @Override
    public List<SubscriptionRateMatrixDetail> getRequiredForUpload(Long subscriptionRateMatrixId) {
        return subscriptionRateMatrixDetailRepository.getRequiredForUpload(subscriptionRateMatrixId);
    }

    @Override
    public List<String> getRequiredCodesForSubscriptionMapping(Long subscriptionRateMatrixId) {
        return subscriptionRateMatrixDetailRepository.getRequiredCodesForSubscriptionMapping(subscriptionRateMatrixId);
    }

    /**
     * @param subscriptionRateMatrixDetailId
     */
    @Override
    public void deleteSubscriptionRateMatrixDetail(Long subscriptionRateMatrixDetailId) {
        SubscriptionRateMatrixDetail subscriptionRateMatrixDetail = subscriptionRateMatrixDetailRepository.findById(
                subscriptionRateMatrixDetailId).orElseThrow(() -> new NotFoundException(
                SubscriptionRateMatrixDetail.class, subscriptionRateMatrixDetailId));
        subscriptionRateMatrixDetail.setMeasureDefinition(null);
        subscriptionRateMatrixDetailRepository.save(subscriptionRateMatrixDetail);
        subscriptionRateMatrixDetailRepository.delete(subscriptionRateMatrixDetail);
    }

    /**
     *
     */
    @Override
    public void deleteAllSubscriptionRateMatrices() {
        subscriptionRateMatrixDetailRepository.deleteAll();
    }

    @Override
    public List<SubscriptionRateMatrixDetail> findDefaultValueForPaymentDownload() {
        return subscriptionRateMatrixDetailRepository.findDefaultValueForPaymentDownload();
    }

    @Override
    public List<SubscriptionRateMatrixDetail> findByDefaultValue(String defaultValue) {
        return subscriptionRateMatrixDetailRepository.findByDefaultValue(defaultValue);
    }

    // CustomerSubscriptionMapping /////////////////////////////////////////////////
    @Override
    public CustomerSubscriptionMapping addOrUpdateCustomerSubscriptionMapping(CustomerSubscriptionMapping customerSubscriptionMapping) {
        customerSubscriptionMapping = customerSubscriptionMappingRepository.save(customerSubscriptionMapping);
        customerSubscriptionMapping.setMeasureDefinition(measureDefinitionOverrideService.findById(customerSubscriptionMapping.getMeasureDefinitionId()));
        return customerSubscriptionMapping;
    }

    @Override
    public List<CustomerSubscriptionMapping> addAllCustomerSubscriptionMappings(List<CustomerSubscriptionMapping> customerSubscriptionMappings) {
        return customerSubscriptionMappingRepository.saveAll(customerSubscriptionMappings);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public CustomerSubscriptionMapping findCustomerSubscriptionMappingById(Long id) {
        CustomerSubscriptionMapping customerSubscriptionMapping =
                customerSubscriptionMappingRepository.findById(id).orElseThrow(() -> new NotFoundException(CustomerSubscriptionMapping.class, id));
        if (customerSubscriptionMapping == null) {
            throw new NotFoundException(CustomerSubscriptionMapping.class, id);
        }
        customerSubscriptionMapping.setMeasureDefinition(measureDefinitionOverrideService.findById(customerSubscriptionMapping.getMeasureDefinitionId()));
        return customerSubscriptionMapping;
    }

    @Override
    public Optional<CustomerSubscriptionMapping> findCustomerSubscriptionMappingOptionalById(Long id) {
        return customerSubscriptionMappingRepository.findById(id);
    }

    @Override
    public List<CustomerSubscriptionMapping> findCustomerSubscriptionMappingByRateCode(String rateCode) {
        return customerSubscriptionMappingRepository.findCustomerSubscriptionMappingByRateCode(rateCode);
    }

    @Override
    public List<CustomerSubscriptionMapping> findCustomerSubscriptionMappingByfindByRateCodeValueMatrixHead(String rateCode, String value, SubscriptionRateMatrixHead subscriptionRateMatrixHead) {
        return customerSubscriptionMappingRepository.findByRateCodeValueMatrixHead(rateCode, value, subscriptionRateMatrixHead);
    }

    @Override
    public CustomerSubscriptionMapping findCustomerSubscriptionMappingByRateCodeAndSubscription(String rateCode,
                                                                                                CustomerSubscription subscription) {
        return customerSubscriptionMappingRepository.findByRateCodeAndSubscription(rateCode, subscription);
    }

    @Override
    public List<CustomerSubscriptionMapping> findBySubscription(CustomerSubscription subscription) {
        return customerSubscriptionMappingRepository.findBySubscription(subscription);
    }

    /**
     * @return
     */
    @Override
    public List<CustomerSubscriptionMapping> findAllCustomerSubscriptionMappings() {
        return customerSubscriptionMappingRepository.findAll();
    }

    /**
     * @param subscription
     * @param userAccount
     * @return
     */
    @Override
    public List<CustomerSubscriptionMapping> getBySubscriptionAndSubscriptionRateMatrixId(CustomerSubscription subscription, User userAccount) {
        return null;
    }

    @Override
    public List<CustomerSubscriptionMapping> getMappingsForCalculationOrderedBySequence(CustomerSubscription subscription, Long subscriptionRateMatrixHeadId) {
        return customerSubscriptionMappingRepository.getMappingsForCalculationOrderedBySequence(subscription,
                subscriptionRateMatrixHeadId);
    }

    @Override
    public List<CustomerSubscriptionMapping> getMappingsWithStaticValues(CustomerSubscription subscription,
                                                                         Long subscriptionRateMatrixHeadId) {
        return customerSubscriptionMappingRepository.getMappingsWithStaticValues(subscription,
                subscriptionRateMatrixHeadId);
    }

    @Override
    public CustomerSubscriptionMapping getRolloverDate(CustomerSubscription subscription) {
        return customerSubscriptionMappingRepository.getRolloverDate(subscription);
    }

    @Override
    public String findCumulativeKWDCofActiveSubs(Long subscriptionRateMatrixHeadId) {
        return customerSubscriptionMappingRepository.findCumulativeKWDCofActiveSubs(subscriptionRateMatrixHeadId);
    }

    @Override
    public String findCumulativeKWDCofInactiveSubs(Long subscriptionRateMatrixHeadId) {
        return customerSubscriptionMappingRepository.findCumulativeKWDCofInactiveSubs(subscriptionRateMatrixHeadId);
    }

    @Override
    public String findCumulativeKWDCofInvalidSubs(Long subscriptionRateMatrixHeadId) {
        return customerSubscriptionMappingRepository.findCumulativeKWDCofInvalidSubs(subscriptionRateMatrixHeadId);
    }

    @Override
    public Double gardenCapacityConsumed(Long subscriptionRateMatrixHeadId) {
        return customerSubscriptionMappingRepository.gardenCapacityConsumed(subscriptionRateMatrixHeadId);
    }

    @Override
    public void deleteCustomerSubscriptionMapping(Long customerSubscriptionMappingId) {
        CustomerSubscriptionMapping customerSubscriptionMapping =
                customerSubscriptionMappingRepository.findById(customerSubscriptionMappingId).orElseThrow(() ->
                        new NotFoundException(CustomerSubscriptionMapping.class, customerSubscriptionMappingId));
        customerSubscriptionMappingRepository.delete(customerSubscriptionMapping);
    }

    /**
     *
     */
    @Override
    public void deleteAllCustomerSubscriptionMappings() {
        customerSubscriptionMappingRepository.deleteAll();
    }

    @Override
    public List<CustomerSubscriptionMapping> findByValue(String value) {
        return customerSubscriptionMappingRepository.findByValue(value);
    }

    // CustomerSubscription /////////////////////////////////////////////////
    @Override
    public CustomerSubscription addOrUpdateCustomerSubscription(CustomerSubscription customerSubscription, boolean isSubsActive, Boolean isLegacy) {
        User user = userService.findByIdFetchAll(customerSubscription.getUserAccountId());
        if (user == null) {
            throw new NotFoundException(User.class, customerSubscription.getUserAccountId());
        }
        if (customerSubscription.getId() != null) {
            CustomerSubscription customerSubscriptionData =
                    findCustomerSubscriptionById(customerSubscription.getId());
            if (customerSubscriptionData == null) {
                throw new NotFoundException(CustomerSubscription.class, customerSubscription.getId());
            }
            customerSubscriptionData =
                    CustomerSubscriptionMapper.toUpdatedCustomerSubscription(customerSubscriptionData,
                            customerSubscription);
            customerSubscriptionData.setUserAccountId(user.getAcctId());

            List<CustomerSubscriptionMapping> mappings = customerSubscription.getCustomerSubscriptionMappings();
            SubscriptionRateMatrixHead matrixHead =
                    findSubscriptionRateMatrixHeadById(customerSubscription.getSubscriptionRateMatrixId());
//            CustomerSubscription finalCustomerSubscriptionData = customerSubscriptionData;
            List<CustomerSubscriptionMapping> mappingsToUpdate = new ArrayList<>();
            Map<String, CustomerSubscriptionMapping> mappingMap = new HashMap<>();
            customerSubscription.getCustomerSubscriptionMappings().forEach(mapping -> {
                mappingMap.put(mapping.getRateCode(), mapping);
            });
            CustomerSubscription finalCustomerSubscription = customerSubscription;
            mappings.forEach(mapping -> {
                CustomerSubscriptionMapping customerSubscriptionMapping = null;
                //bulk upload update case will be true for if
                if (mapping.getId() == null) {
                    customerSubscriptionMapping = findCustomerSubscriptionMappingByRateCodeAndSubscription(mapping.getRateCode(), finalCustomerSubscription);
                } else {
                    Optional<CustomerSubscriptionMapping> mappingOptional = findCustomerSubscriptionMappingOptionalById(mapping.getId());
                    if (mappingOptional.isPresent())
                        customerSubscriptionMapping = mappingOptional.get();
                }
                CustomerSubscriptionMapping mappingData;
                if (customerSubscriptionMapping != null) {
                    mappingData = customerSubscriptionMapping;
                    mappingData = CustomerSubscriptionMappingMapper.toUpdatedCustomerSubscriptionMapping(mappingData,
                            mapping);
                } else {
                    mappingData = mapping;
                    mappingData.setSubscription(finalCustomerSubscription);
                }
                if (mappingData != null) {
                    mappingData.setSubscriptionId(finalCustomerSubscription.getId());
                    mappingData.setSubscriptionRateMatrixId(finalCustomerSubscription.getSubscriptionRateMatrixId());
                    mappingData.setSubscriptionRateMatrixHead(matrixHead);
                    MeasureDefinitionTenantDTO billingDefinition =
                            measureDefinitionOverrideService.findMeasureDefinitionByCode(mapping.getRateCode());
                    if (billingDefinition != null) {
                        mappingData.setMeasureDefinition(billingDefinition);
                        mappingData.setMeasureDefinitionId(billingDefinition.getId());
                    }
                    mappingsToUpdate.add(mappingData);
                }
            });
            customerSubscription.getCustomerSubscriptionMappings().forEach(mapping -> {
                mapping.setId(null);
            });
            customerSubscriptionData.setCustomerSubscriptionMappings(mappingsToUpdate);
//            customerSubscription.setId(null);
//            customerSubscription.setSubscriptionRateMatrixHead(matrixHead);
            return customerSubscriptionRepository.save(customerSubscriptionData);
        }
        customerSubscription.setUserAccount(user);
        SubscriptionRateMatrixHead matrixHead =
                findByIdFetchDetails(customerSubscription.getSubscriptionRateMatrixId());
        customerSubscription.setSubscriptionRateMatrixHead(matrixHead);
        customerSubscription.setSubscriptionStatus(ESubscriptionStatus.INACTIVE.getStatus());
        customerSubscription = customerSubscriptionRepository.save(customerSubscription);
        List<CustomerSubscriptionMapping> mappings = customerSubscription.getCustomerSubscriptionMappings();

//        SubscriptionRateMatrixHead matrixHead = subscriptionService.findByIdFetchDetails(customerSubscription
//        .getSubscriptionRateMatrixId());
        List<CustomerSubscriptionMapping> mappingsTAdd = new ArrayList<>();
        CustomerSubscription finalCustomerSubscription = customerSubscription;
        mappings.forEach(mapping -> {
            mapping.setSubscriptionId(finalCustomerSubscription.getId());
            mapping.setSubscription(finalCustomerSubscription);
            mapping.setSubscriptionRateMatrixId(finalCustomerSubscription.getSubscriptionRateMatrixId());
            mapping.setSubscriptionRateMatrixHead(matrixHead);
            if (mapping.getValue() == null) {
                mapping.setValue(mapping.getDefaultValue());
            }
            MeasureDefinitionTenantDTO billingDefinition =
                    measureDefinitionOverrideService.findMeasureDefinitionByCode(mapping.getRateCode());
            if (billingDefinition != null) {
                mapping.setMeasureDefinition(billingDefinition);
                mapping.setMeasureDefinitionId(billingDefinition.getId());
            }
            mapping.setId(null);
            mappingsTAdd.add(mapping);
        });
        customerSubscriptionMappingRepository.saveAll(mappings);
        CustomerSubscription cs = findByIdFetchCustomerSubscriptionMappings(customerSubscription.getId());
        cs.setSubscriptionRateMatrixHead(matrixHead);

        //to activate subscription
        if (isSubsActive) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            ObjectNode requestMessage = new ObjectMapper().createObjectNode();
            requestMessage.put("userAccountId", user.getAcctId());
            requestMessage.put("subscriptionId", customerSubscription.getId());
            requestMessage.put("type", "activate");
            JobManagerTenant jobManager =
                    jobManagerService.add(EJobName.ACTIVATION.toString() + "_" + user.getAcctId() +
                            "_" + customerSubscription.getId(), requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
            try {
                enqueActivation(user.getAcctId(), customerSubscription.getId(), null, jobManager);
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return cs;
    }

    private void enqueActivation(Long userAccountId, Long subscriptionId, String startDate, JobManagerTenant jobManager)
            throws ParseException {
        CustomerSubscription subscription = findCustomerSubscriptionById(subscriptionId);
        if (subscription.getSubscriptionStatus().equals(ESubscriptionStatus.INACTIVE.getStatus())) {
            SubscriptionType subscriptionType =
                    findSubscriptionTypeByCode(subscription.getSubscriptionType());
            if (subscriptionType.getGenerateCycle() != -1) {
                subscriptionActivation.activate(userAccountId, String.valueOf(subscriptionId), startDate, jobManager.getId(), true);
            } else if (subscriptionType.getGenerateCycle() == -1) {
                subscriptionActivation.activateOnly(userAccountId, String.valueOf(subscriptionId), startDate, jobManager.getId(), true);
            }
        }
        jobManagerService.setCompleted(jobManager, LOGGER);
    }

    @Override
    public List<CustomerSubscription> addCustomerSubscriptions(List<CustomerSubscription> customerSubscriptions, Boolean isLegacy) {
        List<CustomerSubscription> subscriptions = new ArrayList<>();
        customerSubscriptions.forEach(subscription -> subscriptions.add(addOrUpdateCustomerSubscription(subscription, false, isLegacy)));
        return subscriptions;
    }

    /**
     * @param id
     * @return
     */
    @Override
//    @Transactional(propagation = Propagation.SUPPORTS)
    public CustomerSubscription findCustomerSubscriptionById(Long id) {
        return customerSubscriptionRepository.findById(id).orElseThrow(() -> new NotFoundException(CustomerSubscription.class, id));
    }

    @Override
    public CustomerSubscription findCustomerSubscriptionByIdNoThrow(Long id) {
        return customerSubscriptionRepository.findById(id).orElse(null);
    }

    @Override
    public List<CustomerSubscription> findCustomerSubscriptionByUserAccount(Long userId) {
        User user = userRepository.findByIdFetchCustomerSubscription(userId);
        if (user == null) {
            throw new NotFoundException(User.class, userId);
        }
        List<CustomerSubscription> customerSubscriptions = user.getCustomerSubscriptions();
        customerSubscriptions.forEach(subscription -> {
            subscription.setCustomerSubscriptionMappings(customerSubscriptionMappingRepository
                    .findCustomerSubscriptionMappingIncludingVaryByCustomerZero(subscription.getId(),
                            subscription.getSubscriptionRateMatrixId()));
            subscription.setSubscriptionRateMatrixHead(findByIdFetchDetails(subscription.getSubscriptionRateMatrixId()));

            SubscriptionTerminationTemplate cst =
                    customerSubscriptionRepository.getAutoTerminationDate(Constants.SUBSCRIPTION_TERMINATION_RATE_CODES.ROLL, Constants.SUBSCRIPTION_TERMINATION_STATUS.CUSTOMER_SUBSCRIPTION_MAPPING_STATUS_ROLL, subscription.getId());
            //sending rollValue for ui to hide terminationDate link
            if (cst != null) {
                subscription.setAutoTerminationDate(cst.getTerminationDate());
                subscription.setRollValue(cst.getValue());
            }
            CustomerSubscriptionMapping pnMapping =
                    customerSubscriptionMappingRepository.findByRateCodeAndSubscription("PN", subscription);
            if (pnMapping != null && pnMapping.getValue() != null && pnMapping.getValue().equals("-1")) {
                subscription.setMarkedForDeletion(true);
            }
        });
        return customerSubscriptions;
    }

    @Override
    public List<CustomerSubscription> findAllBySubscriptionRateMatrixId(Long subscriptionRateMatrixId) {
        return subscriptionRepository.findAllBySubscriptionRateMatrixId(subscriptionRateMatrixId);
    }

    @Override
    public List<SubscriptionTemplate> listCustomerSubscriptionByUserAccount(Long userId) {
        User user = userRepository.findByIdFetchRoles(userId);
        if (user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()).contains(ERole.ROLE_ADMIN.toString())) {
            return subscriptionRepository.listCustomerSubscription();
        }
        return subscriptionRepository.listCustomerSubscriptionByUserAccount(user);
    }

    @Override
    public List<Long> findForTrueUp(Long subscriptionRateMatrixId) {
        return subscriptionRepository.findForTrueUp(subscriptionRateMatrixId);
    }

    @Override
    public List<CustomerSubscription> findForTrueUpCustomerSubscriptionObject(Long subscriptionRateMatrixId) {
        return subscriptionRepository.findForTrueUpCustomerSubscriptionObject(subscriptionRateMatrixId);
    }


    @Override
    public List<CustomerSubscription> findActiveBySubscriptionRateMatrixId(Long subscriptionRateMatrixId) {
        return subscriptionRepository.findActiveBySubscriptionRateMatrixId(subscriptionRateMatrixId);
    }

    @Override
    public List<CustomerSubscription> findActiveSubscriptionsBySubscriptionRateMatrixId(Long subscriptionRateMatrixId) {
        List<CustomerSubscription> customerSubscriptions =
                subscriptionRepository.findActiveBySubscriptionRateMatrixId(subscriptionRateMatrixId);
        List<CustomerSubscription> nullifiedCustomerSubscriptions = new ArrayList<>();
        customerSubscriptions.forEach(cs -> {
            cs.setUserAccount(null);
            cs.setCustomerSubscriptionMappings(null);
            cs.setBillingHeads(null);
            nullifiedCustomerSubscriptions.add(cs);
        });
        return nullifiedCustomerSubscriptions;
    }

    @Override
    public List<CustomerSubscription> findActiveBySubscriptionRateMatrixIds(List<Long> subscriptionRateMatrixIds) {
        return subscriptionRepository.findActiveBySubscriptionRateMatrixIds(subscriptionRateMatrixIds);
    }

    @Override
    public List<Long> findActiveIdsBySubscriptionRateMatrixIds(List<Long> subscriptionRateMatrixIds) {
        return subscriptionRepository.findActiveIdsBySubscriptionRateMatrixIds(subscriptionRateMatrixIds);
    }

    @Override
    public List<CustomerSubscription> findBySubscriptionStatusAndSubscriptionType(String subscriptionStatus,
                                                                                  String subscriptionType) {
        return subscriptionRepository.findBySubscriptionStatusAndSubscriptionType(subscriptionStatus, subscriptionType);
    }

    @Override
    public CustomerSubscription findByIdFetchCustomerSubscriptionMappings(Long id) {
        return subscriptionRepository.findByIdFetchCustomerSubscriptionMappings(id);
    }

    @Override
    public List<SubscriptionInfoTemplate> findActiveSubscriptionInfos(User userAccount) {
        List<SubscriptionInfoTemplate> subscriptionInfoTemplateList = subscriptionRepository.findActiveSubscriptionInfos(userAccount);
        for (SubscriptionInfoTemplate subscriptionInfoTemplate : subscriptionInfoTemplateList) {
            SubscriptionRateMatrixDetail subscriptionRateMatrixDetail = findBySubscriptionRateMatrixIdAndRateCode(
                    findCustomerSubscriptionById(subscriptionInfoTemplate.getSubscriptionId()).getSubscriptionRateMatrixId()
                    , "PPS");
            if (subscriptionRateMatrixDetail != null) {
                subscriptionInfoTemplate.setPendingPaymentDays(Integer.parseInt(subscriptionRateMatrixDetail.getDefaultValue()));
                subscriptionInfoTemplate.setPendingPaymentDate(Utility.addDays(subscriptionInfoTemplate.getDueDate(), Integer.parseInt(subscriptionRateMatrixDetail.getDefaultValue())));
            }
        }
        return subscriptionInfoTemplateList;
    }

    @Override
    public List<CustomerSubscription> findInactiveSubscriptionsTillToday(Date toDate) {
        return subscriptionRepository.findInactiveSubscriptionsTillToday(toDate);
    }

    @Override
    public List<CustomerSubscription> findInactiveSubscriptionsTillTodayForGarden(Date toDate, Long subscriptionRateMatrixId) {
        return subscriptionRepository.findInactiveSubscriptionsTillTodayForGarden(toDate, subscriptionRateMatrixId);
    }

    /**
     * @return
     */
    @Override
    public List<CustomerSubscription> findAllCustomerSubscriptions() {
        return customerSubscriptionRepository.findAll();
    }

    @Override
    public List<CustomerSubscription> findBySubscriptionStatus(String status) {
        return customerSubscriptionRepository.findBySubscriptionStatus(status);
    }

    @Override
    public List<Long> findIdsBySubscriptionStatus(String status) {
        return customerSubscriptionRepository.findIdsBySubscriptionStatus(status);
    }

    @Override
    public List<Long> findBySubscriptionTypesIn(List<String> subscriptionType) {
        return customerSubscriptionRepository.findBySubscriptionTypesIn(subscriptionType);
    }

    /**
     * @param customerSubscriptionId
     */
    @Override
    public void deleteCustomerSubscription(Long customerSubscriptionId) {
        CustomerSubscription customerSubscription = customerSubscriptionRepository.findById(customerSubscriptionId)
                .orElseThrow(() -> new NotFoundException(CustomerSubscription.class, customerSubscriptionId));
        customerSubscriptionRepository.delete(customerSubscription);
    }

    /**
     *
     */
    @Override
    public void deleteAllCustomerSubscriptions() {
        customerSubscriptionRepository.deleteAll();
    }

    @Override
    public String markForDeletion(Long customerSubscriptionId) {
        CustomerSubscription subscription = findCustomerSubscriptionById(customerSubscriptionId);
        if (!subscription.getSubscriptionStatus().equals(ESubscriptionStatus.INACTIVE.getStatus())) {
            return "Only INACTIVE subscriptions can be marked for deletion";
        }
        CustomerSubscriptionMapping mappingPN = customerSubscriptionMappingRepository.findByRateCodeAndSubscription("PN", subscription);
        if (mappingPN != null) {
            mappingPN.setValue("-1");
            customerSubscriptionMappingRepository.save(mappingPN);
        }
        return "Subscription " + customerSubscriptionId + " marked for deletion";
    }

    /**
     * Subscription must be "INACTIVE" and "PN" is -1 (Update any 1 to -1)
     * <p>Set userAccount to null in CustomerSubscription
     * <p>Set subscriptionStatus in to "INVALID" in CustomerSubscription
     * <p><b>!! IMPORTANT !!</b> disallow activation of "INVALID" subscriptions
     *
     * @param customerSubscriptionId
     * @return
     */
    @Override
    public String deleteSubscription(Long customerSubscriptionId) {
        CustomerSubscription subscription = findCustomerSubscriptionById(customerSubscriptionId);
        if (!subscription.getSubscriptionStatus().equals(ESubscriptionStatus.INACTIVE.getStatus())) {
            return "Only INACTIVE subscriptions can be deleted (invalidated)";
        }
        CustomerSubscriptionMapping mappingPN = customerSubscriptionMappingRepository.findByRateCodeAndSubscription("PN", subscription);
        if (mappingPN != null && !mappingPN.getValue().equals("-1")) {
            return "Premise number must be -1 for deletion (invalidation)";
        }
        subscription.setUserAccount(null);
        subscription.setSubscriptionStatus(ESubscriptionStatus.INVALID.getStatus());
        customerSubscriptionRepository.save(subscription);
        return "Deleted subscription with id " + customerSubscriptionId;
    }

    @Override
    public List<CustomerSubscription> searchMarkedForDeletion(SearchParams searchParams) {
        Long firstParam = searchParams.getAttValue();
        String secondParams = searchParams.getAttDependentValue();

        if (firstParam == 1) {
            return customerSubscriptionRepository.getMarkedForDeletionByAccounts(userService.findByAcctIdIn(
                    Arrays.stream(secondParams.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList())));
        } else if (firstParam == 2) {
            return customerSubscriptionRepository.getMarkedForDeletionBySubscriptionType(
                    Arrays.stream(secondParams.split(",")).map(id -> id.trim()).collect(Collectors.toList()));
        } else if (firstParam == 3) {
            return customerSubscriptionRepository.getMarkedForDeletionBySubscriptionIds(
                    Arrays.stream(secondParams.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList()));
        } else if (firstParam == 4) {
            return customerSubscriptionRepository.getMarkedForDeletionByGardenSRCs(
                    Arrays.stream(secondParams.split(",")).map(id -> id.trim()).collect(Collectors.toList()));
        }
        return null;
    }

    // SubscriptionRatesDerived /////////////////////////////////////////////

    /**
     * @param subscriptionRatesDeriveds
     * @return
     */
    @Override
    public List<SubscriptionRatesDerived> addSubscriptionRatesDerived(List<SubscriptionRatesDerived> subscriptionRatesDeriveds) {
        return subscriptionRatesDerivedRepository.saveAll(subscriptionRatesDeriveds);
    }

    /**
     * @return
     */
    @Override
    public List<SubscriptionRatesDerived> getAllSubscriptionRatesDerived() {
        return subscriptionRatesDerivedRepository.findAll();
    }

    @Override
    public SubscriptionRatesDerived findByConditionExprAndSubscriptionCodeAndCalcGroup(String conditionExpr,
                                                                                       String subscriptionCode,
                                                                                       String calcGroup) {
        return subscriptionRatesDerivedRepository.findByConditionExprAndSubscriptionCodeAndCalcGroup(conditionExpr,
                subscriptionCode, calcGroup);
    }

    @Override
    public List<SubscriptionRatesDerived> findBySubscriptionCodeAndCalcGroup(String subscriptionCode,
                                                                             String calcGroup) {
        return subscriptionRatesDerivedRepository.findBySubscriptionCodeAndCalcGroup(subscriptionCode, calcGroup);
    }

    @Override
    public List<CustomerSubscriptionsListView> comprehensiveSearch(SearchParams searchParams) {
        Long firstParam = searchParams.getAttValue();
        String secondParams = searchParams.getAttDependentValue();
        if (firstParam == 1) {
            return subscriptionsListRepositoryCustom.getByAccount(Arrays.stream(secondParams.split(",")).map(id
                    -> Long.parseLong(id.trim())).collect(Collectors.toList()));
        } else if (firstParam == 2) {
            return subscriptionsListRepositoryCustom.getBySubscriptionType(Arrays.stream(secondParams.split(",")).map(id
                    -> String.valueOf(id.trim())).collect(Collectors.toList()));
        } else if (firstParam == 3) {
            return subscriptionsListRepositoryCustom.getBySubscriptionId(Arrays.stream(secondParams.split(",")).map(id
                    -> Long.parseLong(id.trim())).collect(Collectors.toList()));
        } else if (firstParam == 4) {
            return subscriptionsListRepositoryCustom.getByGardenSRC(Arrays.stream(secondParams.split(",")).map(id
                    -> String.valueOf(id.trim())).collect(Collectors.toList()));
        } else if (firstParam == 5) {
            return subscriptionsListRepositoryCustom.getByPremiseNumber(Arrays.stream(secondParams.split(",")).map(id
                    -> String.valueOf(id.trim())).collect(Collectors.toList()));
        }
        return null;
    }

    @Override
    public List<CustomerSubscriptionsListView> getAll() {
        return subscriptionsListRepositoryCustom.getAll();
    }

    @Override
    public List<SubscriptionTerminationTemplate> terminationBatchQuery() {
        return customerSubscriptionRepository.terminationBatchQuery();
    }

    @Override
    public CustomerSubscriptionMapping getRateCode(CustomerSubscription customerSubscription, List<String> rateCodes) {
        CustomerSubscriptionMapping customerSubscriptionMapping =
                customerSubscriptionMappingRepository.getRateCode(customerSubscription, rateCodes);

        if (customerSubscriptionMapping == null) {
            throw new NotFoundException(CustomerSubscriptionMapping.class, customerSubscription.getId());
        }
        return customerSubscriptionMapping;
    }

/*    @Override
    public List<CustomerSubscription> getAllAdhocSubscriptionTermination(String subsStatus, String rateCode) {
        List<CustomerSubscription> cs = customerSubscriptionRepository.adhocSubscriptionTermination(subsStatus,
                rateCode);
        return cs;
    }*/

    @Override
    public List<SubscriptionTerminationTemplate> getAllAutoTerminationNotification(String rateCode, String value) {
        return customerSubscriptionRepository.getAllAutoTerminationNotification(rateCode, value);
    }

    /*@Override
    public List<CustomerSubscription> getAllAutoSubscriptionTerminationOnEndDate(String rateCode, String value) {
        return customerSubscriptionRepository.getAllAutoSubscriptionTerminationOnEndDate(rateCode, value);
    }*/

    @Override
    public List<Long> getAdhocTerminationFutureInvoicingDate(String subsStatus, List<String> billStatuses,
                                                             Date terminationDate) {
        return customerSubscriptionRepository.getAdhocTerminationFutureInvoicingDate(subsStatus, billStatuses,
                terminationDate);
    }

    @Override
    public String updateTerminationDate(Map<String, String> terminationParam) {

        CustomerSubscription cs = null;

        String subscriptionId = terminationParam.get("subscriptionId");
        String terminationDate = terminationParam.get("terminationDate");
        String terminationReason = terminationParam.get("terminationReason");
        String terminationType = terminationParam.get("terminationType");
        Long subId = subscriptionId.equals("") ? 0l : Long.parseLong(subscriptionId);

        try {

            if (terminationType.equals("") && subId != 0) {

                if (!terminationDate.equals("") && !terminationDate.equals(null)) {
                    cs = customerSubscriptionRepository.checkAdhocSubscriptionTerminationDate(Constants.CUSTOMER_SUBSCRIPTION_STATUS.ACTIVE,
                            Constants.SUBSCRIPTION_TERMINATION_RATE_CODES.ROLL_DT, subId, terminationDate);

                    if (cs != null) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
                        Date terminationDt = simpleDateFormat.parse(terminationDate);
                        customerSubscriptionRepository.updateTerminationDateAndReason(terminationDt,
                                terminationReason, cs.getId());
                    } else {
                        return "Termination date must be greater than Current Date and less than Roll Over Date!";
                    }

                } else {
                    return "Please select termination date!";
                }

            } else if (terminationType.equals(Constants.SUBSCRIPTION_TERMINATION_STATUS.TERMINATION_CANCEL) && subId != 0) {

                cs = customerSubscriptionRepository.findById(subId).get();
                //cs.setTerminationDate(null);
                customerSubscriptionRepository.updateTerminationDateAndReason(null, terminationReason, cs.getId());
                return "Termination has unscheduled!";
            }


        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return "Termination has scheduled!";
    }

    @Override
    public CustomerSubscriptionRateCodeDTO getSubscriptionMappingLatLonRateCodes(Long id) {
        CustomerSubscription customerSubscription = subscriptionRepository.findByIdFetchCustomerSubscriptionMappings(id);
        if (customerSubscription == null) {
            return null;
        } else {
            CustomerSubscriptionRateCodeDTO csDTO = CustomerSubscriptionRateCodeDTO.builder().build();
            csDTO.setId(customerSubscription.getId());
            customerSubscription.getCustomerSubscriptionMappings().forEach(csm -> {
                if (csm.getRateCode().equalsIgnoreCase(Constants.RATE_CODES.LONGITUDE)) {
                    csDTO.setLongitude(Double.valueOf(csm.getValue()));
                } else if (csm.getRateCode().equalsIgnoreCase(Constants.RATE_CODES.LATITUDE)) {
                    csDTO.setLatitude(Double.valueOf(csm.getValue()));
                } else if (csm.getRateCode().equalsIgnoreCase(Constants.RATE_CODES.INVERTER_NUMBER)) {
                    csDTO.setInverterNumber(csm.getValue());
                }
            });
            return csDTO;
        }
    }

    @Override
    public CustomerSubscriptionMaintenanceDTO getSubscriptionMappingMaintenanceRateCodes(Long id) {
        CustomerSubscription customerSubscription = subscriptionRepository.findByIdFetchCustomerSubscriptionMappings(id);
        CustomerSubscriptionMaintenanceDTO csmDTO = CustomerSubscriptionMaintenanceDTO.builder().build();
        Long maintenanceIntervalDays = null;
        Date lastMaintenanceDate = null;
        Long daysRemaining = null;
        Date nextDateOfMaintenance = null;
        Date today = null;
        if (customerSubscription == null) {
            return null;
        } else {
            csmDTO.setId(customerSubscription.getId());
            for (CustomerSubscriptionMapping csm : customerSubscription.getCustomerSubscriptionMappings()) {
                if (csm.getRateCode().equalsIgnoreCase(Constants.RATE_CODES.MAINTENANCE_INTERVAL) && csm.getValue() != null) {
                    maintenanceIntervalDays = Long.valueOf(csm.getValue());
                }
                if (csm.getRateCode().equalsIgnoreCase(Constants.RATE_CODES.LAST_MAINTENANCE_DT) && csm.getValue() != null) {
                    try {
                        lastMaintenanceDate = dateFormat.parse(csm.getValue());
                        today = dateFormat.parse(dateFormat.format(new Date()));

                    } catch (ParseException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                } else if (maintenanceIntervalDays != null && lastMaintenanceDate != null) {
                    daysRemaining = getRemainingMaintenanceDays(today, lastMaintenanceDate, maintenanceIntervalDays);
                    nextDateOfMaintenance = geNextMaintenanceDate(today, daysRemaining);

                    csmDTO.setLastMaintenanceDate(lastMaintenanceDate);
                    csmDTO.setMaintenanceIntervalDays(maintenanceIntervalDays);
                    csmDTO.setNextMaintenanceDate(nextDateOfMaintenance);
                    csmDTO.setDaysRemaining(daysRemaining);
                    maintenanceIntervalDays = null;
                    lastMaintenanceDate = null;

                }
            }
        }
        return csmDTO;
    }

    private Long getRemainingMaintenanceDays(Date today, Date lastMaintenanceDate, Long maintenanceIntervalDays) {
        long differenceDays = Utility.getDifferenceDays(today, lastMaintenanceDate);
        Long daysRemaining = maintenanceIntervalDays - differenceDays;
        return daysRemaining;
    }

    private Date geNextMaintenanceDate(Date today, Long daysRemaining) {
        Integer daysRemainingIntValue = daysRemaining == null ? null : Math.toIntExact(daysRemaining);
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, daysRemainingIntValue);
        return c.getTime();
    }

    //  Contracts
    @Autowired
    private UserLevelPrivilegeRepository userLevelPrivilegeRepository;
    @Autowired
    private ContractMappingRepository contractMappingRepository;

    @Override
    public List<CustomerSubscription> getPrivilegedCustomerSubscriptions() {
        //  get all user privileges from user level
        User user = userService.getLoggedInUser();
        List<UserLevelPrivilege> userLevelPrivileges = userLevelPrivilegeRepository.findByUser(user); //find by account fetch all
        //  get contracts from privileges
        Set<Contract> contracts = new HashSet<>();

        contracts.addAll(userLevelPrivileges.stream().filter(u -> u.getContract() != null)
                .map(m -> m.getContract()).collect(Collectors.toSet()));

        contracts.addAll(userLevelPrivileges.stream().filter(u -> u.getEntity() != null && u.getContract() == null && u.getOrganization() != null)
                .map(m -> m.getEntity()).collect(Collectors.toSet())
                .stream().flatMap(m -> m.getContracts().stream()).collect(Collectors.toSet()));

        contracts.addAll(userLevelPrivileges.stream().filter(u -> u.getOrganization() != null && u.getEntity() == null && u.getContract() == null)
                .map(m -> m.getOrganization()).collect(Collectors.toSet())
                .stream().flatMap(m -> m.getEntities().stream()).collect(Collectors.toSet())
                .stream().flatMap(m -> m.getContracts().stream()).collect(Collectors.toSet()));

        //  get contract mapping from contracts
//        return contractMappingRepository.findByContractIdIn(contracts.stream().map(m -> m.getId()).collect(Collectors.toList()))
//                .stream().flatMap(m -> m.getSubscriptions().stream()).collect(Collectors.toList());
        return contractMappingRepository
                .findByContractIdIn(contracts.stream().map(m -> m.getId()).collect(Collectors.toList()))
                .stream().map(m -> m.getSubContract()).collect(Collectors.toList());
    }

    @Override
    public String getSubscriptionStatus(Date activeDt, Date expiryDt) {
        String status = null;
        boolean isBefore;
        boolean areInSameDay;
        boolean isAfter;
        if (expiryDt != null) {
            isBefore = Utility.isBefore(expiryDt, new Date());
            areInSameDay = Utility.areInSameDay(expiryDt, new Date());
            //isAfter = Utility.isAfter(expiryDt, new Date());
            if (isBefore || areInSameDay) {
                status = "Expired";
                return status;
            }
        }
        if (activeDt != null) {
            isBefore = Utility.isBefore(activeDt, new Date());
            areInSameDay = Utility.areInSameDay(activeDt, new Date());
            isAfter = Utility.isAfter(activeDt, new Date());
            if (isBefore || areInSameDay) {
                status = "Active";
            } else if (isAfter) {
                status = "Inactive";
            }
            return status;
        }
        return status;
    }

    @Override
    public String findValueByRateCodeAndSubscription(String rateCode, CustomerSubscription subscription) {
        CustomerSubscriptionMapping customerSubscriptionMapping = customerSubscriptionMappingRepository.findByRateCodeAndSubscription(rateCode, subscription);
        if (customerSubscriptionMapping == null) {
            throw new NotFoundException(SubscriptionService.class, "No Subscription found with rate code: " + rateCode);
        } else return customerSubscriptionMapping.getValue();
    }

    @Override
    public List<VariantDTO> customerInverterVariants() {
        User currentUser = userService.getLoggedInUser();
        List<ExtDataStageDefinition> extDataStageDefinitions = null;
        if (currentUser.getUserType().getId() == 2) {
            extDataStageDefinitions = extDataStageDefinitionService.findAll();
        } else if (currentUser.getUserType().getId() == 1) {
            extDataStageDefinitions = extDataStageDefinitionService.findAllForCurrentUsers(currentUser.getAcctId());
        }
        List<VariantDTO> variantDTOS = null;
        try {
            String arrayToJson = objectMapper.writeValueAsString(extDataStageDefinitions);
            List<ExtDataStageDefinitionDTO> extDataStageDefinitionDTOS = objectMapper.readValue(arrayToJson, new TypeReference<List<ExtDataStageDefinitionDTO>>() {
            });
            if (!extDataStageDefinitionDTOS.isEmpty() || extDataStageDefinitionDTOS.size() > 0) {
                //call mongo query.
                VariantCustomerSubsDTO variantCustomerSubsDTO = VariantCustomerSubsDTO.builder()
                        .extDataStageDefinition(extDataStageDefinitionDTOS).byProductCodes("SCSG,SCSGN").byCustomerCodes("CN,DEVNO").build();
                variantDTOS =
                        dataExchange.customerInverterVariantAndSubscriptions(variantCustomerSubsDTO, DBContextHolder.getTenantName());
            }
            /*Map<String,List<String>> variants = new HashMap<>();
            String arrayToJson = objectMapper.writeValueAsString(extDataStageDefinitions);
            List<ExtDataStageDefinitionDTO> extDataStageDefinitionDTOS = objectMapper.readValue(arrayToJson, new TypeReference<List<ExtDataStageDefinitionDTO>>(){});
            if (!extDataStageDefinitionDTOS.isEmpty() || extDataStageDefinitionDTOS.size() > 0) {
                extDataStageDefinitionDTOS.forEach(ext -> {
                    if (variants.containsKey(ext.getRefId())) {
                        variants.get(ext.getRefId()).add(ext.getSubsId());
                    } else {
                        List<String> subsList = new ArrayList<>();
                        subsList.add(ext.getSubsId());
                        variants.put(ext.getRefId(), subsList);
                    }
                    ext.setVariantMap(variants);
                });
            }*/

        } catch (Exception ex) {
            ex.getMessage();
        }
        return variantDTOS;
    }

    @Override
    public BaseResponse createSubscriptionCollectionByVariantId(String variantId, String requestType, String subscriptionObject, Boolean isProjection) {
        BaseResponse<Object> baseResponse = null;
        Date startDate = null;
        Long invoiceTemplateId = null;
        String srcNo = null;
        try {
            JSONObject subscriptionJson = new JSONObject(subscriptionObject);
            baseResponse = dataExchange.createSubscriptionCollectionByVariantId(requestType, variantId, subscriptionObject);

            String subscriptionId = extractIdFromResponse(baseResponse);
            String productId = subscriptionJson.getJSONObject("product_group").optString("$id");
            String accountId = subscriptionJson.optString("account_id");

            JSONArray byCustomerArray = subscriptionJson.getJSONObject("measures").optJSONArray("by_customer");
            JSONArray byProductArray = subscriptionJson.getJSONObject("measures").optJSONArray("by_product");

            if (byCustomerArray != null) {
                startDate = extractStartDate(byCustomerArray);
                invoiceTemplateId = extractInvoiceTemplateId(byCustomerArray);
            }

            if (byProductArray != null) {
                srcNo = extractSrcNo(byProductArray);
            }

            saveCustomerSubscription(variantId, productId, startDate, accountId, subscriptionId, invoiceTemplateId, srcNo, requestType);
            stageMonitorService.getMongoSubscriptionsAndMeasures();
            stageMonitorService.transferSubscriptionsToStageDefinition();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return baseResponse;
    }

    @Override
    public List<InverterSubscriptionDTO> getCustomerVariants(List<String> variantIds) {
        List<InverterSubscriptionDTO> inverterSubscriptionDTOS = null;
        try {
            User currentUser = userService.getLoggedInUser();
            if (currentUser.getUserType().getId() == 2) {
                inverterSubscriptionDTOS = extDataStageDefinitionService.findAllInverterSubscriptionDTO(variantIds);
            } else if (currentUser.getUserType().getId() == 1) {
                inverterSubscriptionDTOS = extDataStageDefinitionService.findAllInverterSubscriptionDTOForCurrentUsers(variantIds, currentUser.getAcctId());
            }
            inverterSubscriptionDTOS = setSubscriptionList(inverterSubscriptionDTOS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return inverterSubscriptionDTOS;

    }

    @Override
    public List<InverterSubscriptionDTO> customerInverterSubsByVariantIds(List<String> variantIds) {
        List<InverterSubscriptionDTO> inverterSubscriptionDTOS = new ArrayList<>();

        try {
            User currentUser = userService.getLoggedInUser();
            List<ExtDataStageDefinition> extDataStageDefinitions;

            if (currentUser.getUserType().getId() == 2) {
                extDataStageDefinitions = extDataStageDefinitionService.findAllByRefIdIn(variantIds, "ACTIVE");
            } else if (currentUser.getUserType().getId() == 1) {
                extDataStageDefinitions = extDataStageDefinitionService.findAllByRefIdAndAcctIdIn(variantIds, currentUser.getAcctId(), "ACTIVE");
            } else {
                return inverterSubscriptionDTOS; // Return empty list if user type is neither "HO" nor "Customer"
            }

            ObjectMapper mapper = new ObjectMapper(); // Initialize ObjectMapper once

            extDataStageDefinitions.stream()
                    .map(extDataStageDefinition -> {
                        try {
                            Map<String, Object> map = mapper.readValue(extDataStageDefinition.getMpJson(), Map.class);
                            String devNo = (String) map.getOrDefault("DEVNO", "");
                            return InverterSubscriptionDTO.builder()
                                    .subsId(extDataStageDefinition.getSubsId())
                                    .subscriptionName(extDataStageDefinition.getSubscriptionName())
                                    .devNo(devNo)
                                    .subsStatus(extDataStageDefinition.getSubsStatus())
                                    .build();
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            return null; // Return null for failed mappings
                        }
                    })
                    .filter(Objects::nonNull) // Filter out null results
                    .forEach(inverterSubscriptionDTOS::add); // Add to the final list

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return inverterSubscriptionDTOS;
    }

    @Override
    public List<VariantDTO> customerInverterVariantsV2() {
        User currentUser = userService.getLoggedInUser();
        List<ExtDataStageDefinition> extDataStageDefinitions = null;
        if (currentUser.getUserType().getId() == 2) {
            extDataStageDefinitions = extDataStageDefinitionService.findAllUniqueProjects();
        } else if (currentUser.getUserType().getId() == 1) {
            extDataStageDefinitions = extDataStageDefinitionService.findAllForCurrentUsers(currentUser.getAcctId());
        }
        List<VariantDTO> variantDTOS = null;
        try {
            String arrayToJson = objectMapper.writeValueAsString(extDataStageDefinitions);
            List<ExtDataStageDefinitionDTO> extDataStageDefinitionDTOS = objectMapper.readValue(arrayToJson, new TypeReference<List<ExtDataStageDefinitionDTO>>() {
            });
            if (!extDataStageDefinitionDTOS.isEmpty() || extDataStageDefinitionDTOS.size() > 0) {

                variantDTOS = extDataStageDefinitionDTOS.stream()
                        .map(extDataStageDefinition -> VariantDTO.builder()
                                ._id(extDataStageDefinition.getRefId())
                                .variantAlias(extDataStageDefinition.getRefType())
                                .variantName(extDataStageDefinition.getRefType()) // Assuming refType maps to variantName
                                // Set other properties of VariantDTO as needed
                                .build())
                        .collect(Collectors.toList());
            }

        } catch (Exception ex) {
            ex.getMessage();
        }
        return variantDTOS;
    }

    @Override
    public List<SubscriptionCountDTO> countByCustomer() {
        return subscriptionsListRepositoryCustom.countByCustomer();
    }

    private List<InverterSubscriptionDTO> setSubscriptionList(List<InverterSubscriptionDTO> inverterSubscriptionDTOS) {
        inverterSubscriptionDTOS.forEach(dto -> dto.setSubsId(extDataStageDefinitionService.findAllSubscriptionsByVariantId(dto.getVariantId(), dto.getAcctId())));
        return inverterSubscriptionDTOS;
    }

    private String extractIdFromResponse(BaseResponse<Object> baseResponse) {
        return baseResponse.getMessage().split("id:")[1];
    }

    private Date extractStartDate(JSONArray byCustomerArray) {
        for (int i = 0; i < byCustomerArray.length(); i++) {
            JSONObject obj = byCustomerArray.getJSONObject(i);
            if ("S_SSDT".equals(obj.optString("code")) && !"null".equals(obj.optString("default_value")) && !"".equals(obj.optString("default_value").trim())) {
                return Utility.getDate(obj.getJSONObject("default_value").optString("$date"), Utility.SYSTEM_DATE_FORMAT);
            }
        }
        return null;
    }

    private Long extractInvoiceTemplateId(JSONArray byCustomerArray) {
        for (int i = 0; i < byCustomerArray.length(); i++) {
            JSONObject obj = byCustomerArray.getJSONObject(i);
            if ("INVRPTID".equals(obj.optString("code")) && !"null".equals(obj.optString("attribute_id_ref_id"))) {
                return Long.parseLong(obj.optString("attribute_id_ref_id"));
            }
        }
        return null;
    }

    private String extractSrcNo(JSONArray byProductArray) {
        for (int i = 0; i < byProductArray.length(); i++) {
            JSONObject obj = byProductArray.getJSONObject(i);
            if ("SCSGN".equals(obj.optString("code")) && !"null".equals(obj.optString("default_value"))) {
                return obj.optString("default_value");
            }
        }
        return null;
    }

    private void saveCustomerSubscription(String variantId, String productId, Date startDate, String accountId, String subscriptionId, Long invoiceTemplateId, String srcNo, String requestType) {
        CustomerSubscription customerSubscription = customerSubscriptionRepository.findByExtSubsId(subscriptionId);
        if (customerSubscription == null) {
            customerSubscription = new CustomerSubscription();
        }
        if (accountId != null && !accountId.trim().isEmpty()) {
            customerSubscription.setUserAccount(userService.findById(Long.parseLong(accountId)));
        }
        if (srcNo == null) {
            Variant variant = dataExchange.getSubscriptionMapping(subscriptionId,DBContextHolder.getTenantName()).getVariant();
            MeasureType srcNoType = variant.getMeasures().getByProduct().stream()
                            .filter(m -> m.getCode().equals("SCSGN"))
                            .findFirst().orElse(null);
            srcNo = (srcNoType !=null && srcNoType.getDefaultValue() !=null) ? srcNoType.getDefaultValue() : null;
        }
        customerSubscription.setSubscriptionTemplate(variantId);
        customerSubscription.setSubscriptionType(productId);
        customerSubscription.setStartDate(startDate);
        customerSubscription.setExtSubsId(subscriptionId);
        customerSubscription.setInvoiceTemplateId(invoiceTemplateId);
        customerSubscription.setGardenSrc(srcNo);

        customerSubscriptionRepository.save(customerSubscription);
    }

    @Override
    public Map<String, Object> getSubscriptionsByUserId(Long userId) {
        Map response = new HashMap<String, Object>();
        try {
            List<SubscriptionDetailTemplate> subscriptionDetailDTOS = subscriptionRepository.findSubsByUserId(userId);
            if (subscriptionDetailDTOS != null && !subscriptionDetailDTOS.isEmpty()) {
                return Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Subscription list for userid returned successfully", subscriptionDetailDTOS);
            }
        } catch (Exception ex) {
            return Utility.generateResponseMap(response, HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage(), null);
        }
        return Utility.generateResponseMap(response, HttpStatus.NO_CONTENT.toString(), "No subscriptions exists for this userId = " + userId, null);
    }

    @Override
    public List<CustomerSubscription> manageCustomerSubscriptionsForProjection() {
        List<CustomerSubscription> finalList = new ArrayList<>();
        List<ExtDataStageDefinition> projectionList = extDataStageDefinitionService.findMonthlyProjectionsForAllGardens();
        List<String> variantIds = projectionList.stream().map(ExtDataStageDefinition::getRefId).distinct().collect(Collectors.toList());
        List<String> projectionIds = projectionList.stream().map(ExtDataStageDefinition::getSubsId).distinct().collect(Collectors.toList());
        List<CustomerSubscription> existingProjections = customerSubscriptionRepository.findProjectionByProjectionIdsAndVariantIds(projectionIds, variantIds);
        projectionList.stream().forEach(projection -> {
            Long custSubsId = null;
            Optional<CustomerSubscription> foundCustSubsItem = existingProjections.stream()
                    .filter(existProjection ->
                            projection.getRefId().equalsIgnoreCase(existProjection.getSubscriptionTemplate())
                                    && projection.getSubsId().equalsIgnoreCase(existProjection.getExtSubsId()))
                    .findFirst();
            if (foundCustSubsItem.isPresent()) {
                custSubsId = foundCustSubsItem.get().getId();
            }
            finalList.add(CustomerSubscription.builder().id(custSubsId).subscriptionTemplate(projection.getRefId()).subscriptionType(projection.getGroupId())
                    .extSubsId(projection.getSubsId()).build());

        });

        return subscriptionRepository.saveAll(finalList);
    }

    @Override
    public List<CustomerSubscription> findAllCustomerSubscriptionsForProjection() {
        List<ExtDataStageDefinition> projectionList = extDataStageDefinitionService.findMonthlyProjectionsForAllGardens();
        List<String> variantIds = projectionList.stream().map(ExtDataStageDefinition::getRefId).distinct().collect(Collectors.toList());
        List<String> projectionIds = projectionList.stream().map(ExtDataStageDefinition::getSubsId).distinct().collect(Collectors.toList());
        List<CustomerSubscription> existingProjections = customerSubscriptionRepository.findProjectionByProjectionIdsAndVariantIds(projectionIds, variantIds);
        return existingProjections;
    }
}



