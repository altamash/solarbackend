package com.solar.api.tenant.service;

import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionDetailDTO;
import com.solar.api.saas.service.integration.mongo.response.subscription.VariantDTO;
import com.solar.api.tenant.mapper.subscription.SubscriptionInfoTemplate;
import com.solar.api.tenant.mapper.subscription.SubscriptionTemplate;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionMaintenanceDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionRateCodeDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.SubscriptionCountDTO;
import com.solar.api.tenant.mapper.subscription.subscriptionType.SubscriptionTypeDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.paymentDetailView.SearchParams;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.InverterSubscriptionDTO;
import com.solar.api.tenant.model.subscription.*;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixDetail;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRatesDerived;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.BaseResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SubscriptionService {

    // SubscriptionType ////////////////////////////////////////////////////////
    SubscriptionType addOrUpdateSubscriptionType(SubscriptionType subscriptionType);

    List<SubscriptionType> addSubscriptionTypes(List<SubscriptionType> subscriptionTypes);

    SubscriptionType findSubscriptionTypeById(Long id);

    SubscriptionType findSubscriptionTypeByCode(String code);

    List<SubscriptionType> findSubscriptionTypeByCodeIn(List<String> codes);

    SubscriptionType findSubscriptionTypeBySubscriptionName(String subscriptionName);

    List<SubscriptionTypeDTO> findAllSubscriptionTypesWithPrimaryGroup();

    List<SubscriptionType> findAllSubscriptionTypes(String status);

    void deleteSubscriptionType(Long subscriptionTypeId);

    void deleteAllSubscriptionTypes();

    // SubscriptionRateMatrixHead /////////////////////////////////////////////////
    SubscriptionRateMatrixHead addOrUpdateSubscriptionRateMatrixHead(SubscriptionRateMatrixHead subscriptionRateMatrixHead);

    List<SubscriptionRateMatrixHead> addSubscriptionRateMatrixHeads(List<SubscriptionRateMatrixHead> subscriptionRateMatrixHeads);

    SubscriptionRateMatrixHead findSubscriptionRateMatrixHeadById(Long subscriptionRateMatrixHeadId);

    SubscriptionRateMatrixHead findSubscriptionRateMatrixHeadBySubscriptionTemplate(String subscriptionTemplate);

    List<SubscriptionRateMatrixHead> findSubscriptionRateMatrixHeadBySubscriptionCodeAndActive(String subscriptionCode, Boolean active);

    List<SubscriptionRateMatrixHead> findSubscriptionRateMatrixHeadsByIdsIn(List<Long> ids);

    SubscriptionRateMatrixHead findByIdFetchDetails(Long subscriptionRateMatrixHeadId);

    List<SubscriptionRateMatrixHead> findAllSubscriptionRateMatrixHeadsFetchDetails();

    List<SubscriptionRateMatrixHead> findAllSubscriptionRateMatrixHeads();

    void deleteSubscriptionRateMatrixHead(Long subscriptionRateMatrixId);

    void deleteAllSubscriptionRateMatrixHeads();

    // SubscriptionRateMatrixDetail /////////////////////////////////////////////////
    SubscriptionRateMatrixDetail addOrUpdateSubscriptionRateMatrix(SubscriptionRateMatrixDetail subscriptionRateMatrixDetail);

    List<SubscriptionRateMatrixDetail> add(List<SubscriptionRateMatrixDetail> subscriptionRateMatrixDetails);

    SubscriptionRateMatrixDetail findSubscriptionRateMatrixDetailById(Long subscriptionRateMatrixDetailId);

    List<SubscriptionRateMatrixDetail> findBySubscriptionRateMatrixId(Long id);

    SubscriptionRateMatrixDetail findBySubscriptionRateMatrixIdAndRateCode(Long id, String code);

    List<String> findRateCodesBySubscriptionRateMatrixIdAndVaryByCustomer(Long subscriptionRateMatrixId,
                                                                          Boolean varyByCustomer);

    List<UserDTO> findCustomerInverterSubscriptions();

    SubscriptionRateMatrixDetail findBySubscriptionCodeAndSubscriptionRateMatrixIdAndRateCode(String subscriptionCode
            , Long subscriptionRateMatrixId, String rateCode);

    List<SubscriptionRateMatrixDetail> findAllSubscriptionRateMatrixDetails();

    List<SubscriptionRateMatrixDetail> findAllSubscriptionRateMatrixDetailsByRateCode(String rateCode);

    List<SubscriptionRateMatrixDetail> getRequiredForUpload(Long subscriptionRateMatrixId);

    List<String> getRequiredCodesForSubscriptionMapping(Long subscriptionRateMatrixId);

    void deleteSubscriptionRateMatrixDetail(Long subscriptionRateMatrixDetailId);

    void deleteAllSubscriptionRateMatrices();

    List<SubscriptionRateMatrixDetail> findDefaultValueForPaymentDownload();

    List<SubscriptionRateMatrixDetail> findByDefaultValue(String defaultValue);

    // CustomerSubscriptionMapping /////////////////////////////////////////////////
    CustomerSubscriptionMapping addOrUpdateCustomerSubscriptionMapping(CustomerSubscriptionMapping customerSubscriptionMapping);

    List<CustomerSubscriptionMapping> addAllCustomerSubscriptionMappings(List<CustomerSubscriptionMapping> customerSubscriptionMappings);

    CustomerSubscriptionMapping findCustomerSubscriptionMappingById(Long id);

    Optional<CustomerSubscriptionMapping> findCustomerSubscriptionMappingOptionalById(Long id);

    List<CustomerSubscriptionMapping> findCustomerSubscriptionMappingByRateCode(String rateCode);

    List<CustomerSubscriptionMapping> findCustomerSubscriptionMappingByfindByRateCodeValueMatrixHead(String rateCode,
                                                                                                     String value,
                                                                                                     SubscriptionRateMatrixHead subscriptionRateMatrixHead);

    CustomerSubscriptionMapping findCustomerSubscriptionMappingByRateCodeAndSubscription(String rateCode,
                                                                                         CustomerSubscription subscription);

    List<CustomerSubscriptionMapping> findBySubscription(CustomerSubscription subscription);

    List<CustomerSubscriptionMapping> findAllCustomerSubscriptionMappings();

    List<CustomerSubscriptionMapping> getBySubscriptionAndSubscriptionRateMatrixId(CustomerSubscription subscription,
                                                                                   User userAccount);

    List<CustomerSubscriptionMapping> getMappingsForCalculationOrderedBySequence(CustomerSubscription subscription,
                                                                                 Long subscriptionRateMatrixHeadId);

    List<CustomerSubscriptionMapping> getMappingsWithStaticValues(CustomerSubscription subscription,
                                                                  Long subscriptionRateMatrixHeadId);

    CustomerSubscriptionMapping getRolloverDate(CustomerSubscription subscription);

    String findCumulativeKWDCofActiveSubs(Long subscriptionRateMatrixHeadId);

    String findCumulativeKWDCofInactiveSubs(Long subscriptionRateMatrixHeadId);

    String findCumulativeKWDCofInvalidSubs(Long subscriptionRateMatrixHeadId);

    Double gardenCapacityConsumed(Long subscriptionRateMatrixHeadId);

    void deleteCustomerSubscriptionMapping(Long customerSubscriptionMappingId);

    void deleteAllCustomerSubscriptionMappings();

    List<CustomerSubscriptionMapping> findByValue(String value);

    // CustomerSubscription /////////////////////////////////////////////////
    CustomerSubscription addOrUpdateCustomerSubscription(CustomerSubscription customerSubscription,
                                                         boolean isSubsActive, Boolean isLegacy);

    List<CustomerSubscription> addCustomerSubscriptions(List<CustomerSubscription> customerSubscription,
                                                        Boolean isLegacy);

    CustomerSubscription findCustomerSubscriptionById(Long id);

    CustomerSubscription findCustomerSubscriptionByIdNoThrow(Long id);

    CustomerSubscriptionMapping getRateCode(CustomerSubscription customerSubscription, List<String> rateCodes);

    List<CustomerSubscription> findCustomerSubscriptionByUserAccount(Long userId);

    List<CustomerSubscription> findAllBySubscriptionRateMatrixId(Long subscriptionRateMatrixId);

    List<SubscriptionTemplate> listCustomerSubscriptionByUserAccount(Long userId);

    List<Long> findForTrueUp(Long subscriptionRateMatrixId);

    List<CustomerSubscription> findForTrueUpCustomerSubscriptionObject(Long subscriptionRateMatrixId);

    List<CustomerSubscription> findActiveBySubscriptionRateMatrixId(Long subscriptionRateMatrixId);

    List<CustomerSubscription> findActiveSubscriptionsBySubscriptionRateMatrixId(Long subscriptionRateMatrixId);

    List<CustomerSubscription> findActiveBySubscriptionRateMatrixIds(List<Long> subscriptionRateMatrixIds);

    List<Long> findActiveIdsBySubscriptionRateMatrixIds(List<Long> subscriptionRateMatrixIds);

    List<CustomerSubscription> findBySubscriptionStatusAndSubscriptionType(String subscriptionStatus,
                                                                           String subscriptionType);

    CustomerSubscription findByIdFetchCustomerSubscriptionMappings(Long id);

    List<SubscriptionInfoTemplate> findActiveSubscriptionInfos(User userAccount);

    List<CustomerSubscription> findInactiveSubscriptionsTillToday(Date toDate);

    List<CustomerSubscription> findInactiveSubscriptionsTillTodayForGarden(Date toDate, Long subscriptionRateMatrixId);

    List<CustomerSubscription> findAllCustomerSubscriptions();

    List<CustomerSubscription> findBySubscriptionStatus(String status);

    List<Long> findIdsBySubscriptionStatus(String status);

    List<Long> findBySubscriptionTypesIn(List<String> subscriptionType);

    void deleteCustomerSubscription(Long customerSubscriptionId);

    void deleteAllCustomerSubscriptions();

    String markForDeletion(Long customerSubscriptionId);

    String deleteSubscription(Long customerSubscriptionId);

    List<CustomerSubscription> searchMarkedForDeletion(SearchParams searchParams);

    // SubscriptionRatesDerived /////////////////////////////////////////////
    List<SubscriptionRatesDerived> addSubscriptionRatesDerived(List<SubscriptionRatesDerived> subscriptionRatesDeriveds);

    List<SubscriptionRatesDerived> getAllSubscriptionRatesDerived();

    SubscriptionRatesDerived findByConditionExprAndSubscriptionCodeAndCalcGroup(String conditionExpr,
                                                                                String subscriptionCode,
                                                                                String calcGroup);

    List<SubscriptionRatesDerived> findBySubscriptionCodeAndCalcGroup(String subscriptionCode, String calcGroup);

    // ComprehensiveSearch /////////////////////////////////////////////
    List<CustomerSubscriptionsListView> comprehensiveSearch(SearchParams searchParams);

    List<CustomerSubscriptionsListView> getAll();

    //List<CustomerSubscription> getAllAdhocSubscriptionTermination(String subsStatus, String rateCode);

    List<SubscriptionTerminationTemplate> terminationBatchQuery();

    List<SubscriptionTerminationTemplate> getAllAutoTerminationNotification(String rateCode, String value);

    //List<CustomerSubscription> getAllAutoSubscriptionTerminationOnEndDate(String rateCode, String value);

    List<Long> getAdhocTerminationFutureInvoicingDate(String subsStatus, List<String> billStatuses,
                                                      Date terminationDate);

    String updateTerminationDate(Map<String, String> terminationParam);

    CustomerSubscriptionRateCodeDTO getSubscriptionMappingLatLonRateCodes(Long id);

    CustomerSubscriptionMaintenanceDTO getSubscriptionMappingMaintenanceRateCodes(Long id);

    //  Contracts
    List<CustomerSubscription> getPrivilegedCustomerSubscriptions();

    String getSubscriptionStatus(Date activeDt, Date expiryDt);

    String findValueByRateCodeAndSubscription(String rateCode, CustomerSubscription subscription);
    List<VariantDTO> customerInverterVariants();

    BaseResponse createSubscriptionCollectionByVariantId(String variantId, String requestType, String subscriptionObject, Boolean isProjection);

    List<InverterSubscriptionDTO> getCustomerVariants(List<String> variantIds);
    List<InverterSubscriptionDTO> customerInverterSubsByVariantIds(List<String> variantIds);

    List<VariantDTO> customerInverterVariantsV2();

    Map<String, Object> getSubscriptionsByUserId(Long userId);

    List<SubscriptionCountDTO> countByCustomer();
    List<CustomerSubscription> manageCustomerSubscriptionsForProjection();
    List<CustomerSubscription> findAllCustomerSubscriptionsForProjection();
}


