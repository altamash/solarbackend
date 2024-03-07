package com.solar.api.tenant.service;

import com.google.gson.Gson;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.WorkflowHookMaster;
import com.solar.api.saas.repository.workflow.WorkflowHookMasterRepository;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.Variant;
import com.solar.api.tenant.mapper.billing.billingHead.*;
import com.solar.api.tenant.mapper.billing.calculation.CalculationTrackerTemplate;
import com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.mapper.tiles.calculationTracker.CalculationTrackerGroupByTile;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.billing.tansStage.TransStageHead;
import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.contract.EEntityType;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.process.rule.RuleExecutionLog;
import com.solar.api.tenant.model.stage.billing.ExtDataStageDefinitionBilling;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.ESubscriptionStatus;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.BillingDetailRepository;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.process.customer.CustomerPaymentInfo;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import com.solar.api.tenant.service.process.rule.RuleExecutionLogService;
import com.solar.api.tenant.service.tansStage.TransStageHeadService;
import com.solar.api.tenant.service.tansStage.TransStageTempService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;


@Service
//@Transactional("tenantTransactionManager")
public class BillingHeadServiceImpl implements BillingHeadService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BillingHeadRepository billingHeadRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private Utility utility;
    @Autowired
    private CustomerPaymentInfo customerPaymentInfo;
    @Autowired
    private CalculationDetailsService calculationDetailsService;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private BillingDetailRepository billingDetailRepository;
    @Autowired
    private PhysicalLocationService physicalLocationService;
    @Autowired
    private CalculationTrackerService calculationTrackerService;
    @Autowired
    private CompanyPreferenceService companyPreferenceService;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private PortalAttributeOverrideService portalAttributeOverrideService;
    @Autowired
    private WorkflowHookMasterRepository workFlowHookMasterRepository;
    @Autowired
    private RuleExecutionLogService ruleExecutionLogService;
    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;
    @Autowired
    private ExtDataStageDefinitionBillingService extDataStageDefinitionBillingService;

    @Autowired
    private MasterTenantService masterTenantService;

    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;

    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;
    @Autowired
    private TransStageHeadService transStageHeadService;
    @Autowired
    private TransStageTempService transStageTempService;
    @Autowired
    private ExtDataStageDefinitionService extDataStageDefinitionService;

    @Override
    public List<BillingHead> findByBillStatus(String status) {
        return billingHeadRepository.findByBillStatus(status);
    }

    @Override
    public BillingHead addOrUpdateBillingHead(BillingHead billingHead) {
        int rounding = utility.getCompanyPreference().getRounding();
        if (billingHead.getId() != null) {
            BillingHead billingHeadData = findByIdFetchBillingDetails(billingHead.getId());
            if (billingHeadData == null) {
                throw new NotFoundException(BillingHead.class, billingHead.getId());
            }
            billingHeadData = BillingHeadMapper.toUpdatedBillingHead(billingHeadData, billingHead);
            if (billingHead.getUserAccount() != null) {
                billingHead.setUserAccountId(billingHead.getUserAccountId());
            } else {
                if (billingHead.getUserAccountId() != null) {
                    User userAccount = userService.findById(billingHead.getUserAccountId());
                    billingHead.setUserAccount(userAccount);
                }
            }
            CustomerSubscription subscription =
                    subscriptionRepository.findById(billingHead.getSubscriptionId()).orElseThrow(() -> new NotFoundException(CustomerSubscription.class, billingHead.getSubscriptionId()));
            billingHead.setSubscription(subscription);
            billingHeadData.setAmount(utility.roundBilling(billingHeadData.getAmount(), rounding));
            return billingHeadRepository.save(billingHeadData);
        }
        User userAccount = userService.findById(billingHead.getUserAccountId());
        billingHead.setUserAccount(userAccount);
        CustomerSubscription subscription =
                subscriptionRepository.findById(billingHead.getSubscriptionId()).orElseThrow(() -> new NotFoundException(CustomerSubscription.class, billingHead.getSubscriptionId()));
        billingHead.setSubscription(subscription);
        billingHead.setAmount(utility.roundBilling(billingHead.getAmount(), rounding));
        return billingHeadRepository.save(billingHead);
    }

    @Override
    public BillingHead save(BillingHead billingHead) {
        if (billingHead.getAmount() != null) {
            billingHead.setAmount(utility.roundBilling(billingHead.getAmount(),
                    utility.getCompanyPreference().getRounding()));
        }
        return billingHeadRepository.save(billingHead);
    }

    @Override
    public BillingHead toUpdateMapper(BillingHead billingHead) {
        if (billingHead.getId() != null) {
            BillingHead billingHeadData = findById(billingHead.getId());
            if (billingHeadData == null) {
                throw new NotFoundException(BillingHead.class, billingHead.getId());
            }
            billingHeadData = BillingHeadMapper.toUpdatedBillingHead(billingHeadData,
                    billingHead);
            return billingHeadRepository.save(billingHeadData);
        }
        return billingHeadRepository.save(billingHead);
    }

    @Override
    public List<BillingHead> addOrUpdateBillingHeads(List<BillingHead> billingHeads) {
        int rounding = utility.getCompanyPreference().getRounding();
        billingHeads.forEach(head -> {
            if (head.getAmount() != null) {
                head.setAmount(utility.roundBilling(head.getAmount(), rounding));
            }
        });
        return billingHeadRepository.saveAll(billingHeads);
    }

    @Override
    public List<BillingHead> addBillingHeadsBySubscriptionAndMonth(List<BillingHead> billingHeads) {
        List<BillingHead> addedBillingHeads = new ArrayList<>();
        billingHeads.forEach(billingHead -> {
            if (billingHeadRepository.findBySubscriptionIdAndBillingMonthYear(billingHead.getSubscriptionId(),
                    billingHead.getBillingMonthYear()) == null) {
                addedBillingHeads.add(billingHeadRepository.save(billingHead));
            } else {
                LOGGER.warn("BillingHead for month " + billingHead.getBillingMonthYear() + " already exists!");
            }
        });
        return addedBillingHeads;
    }

    @Override
    public BillingHead findById(Long id) {
        return billingHeadRepository.findById(id).orElseThrow(() -> new NotFoundException(BillingHead.class, id));
    }

    @Override
    public List<BillingHead> findAllByIds(List<Long> ids) {
        return billingHeadRepository.findAllById(ids);
    }

    @Override
    public BillingHead findByIdFetchBillingDetails(Long id) {
        return billingHeadRepository.findByIdFetchBillingDetails(id);
    }

    @Override
    public List<BillingHead> findByUserAccountId(Long userAccountId) {
        User userAccount = userService.findById(userAccountId);
        return findByUserAccount(userAccount);
    }

    @Override
    public List<BillingHead> findByUserAccount(User userAccount) {
        return billingHeadRepository.findByUserAccount(userAccount);
    }

    @Override
    public List<BillingHead> findBySubscriptionId(Long subscriptionId) {
        return billingHeadRepository.findBySubscriptionId(subscriptionId);
    }

    @Override
    public List<BillingHead> findLastTwelveMonths(List<String> monthYears, Long subscriptionId) {
        return billingHeadRepository.findLastTwelveMonths(monthYears, subscriptionId);
    }

    @Override
    public List<BillingHead> findBySubscriptionIdFetchBillingDetails(Long subscriptionId) {
        return billingHeadRepository.findBySubscriptionIdFetchBillingDetails(subscriptionId);
    }

    /**
     * Description: New method to return calculation Tracker List
     *
     * @param
     * @return
     * @throws Exception
     */
    @Override
    public Map findBySubscriptionIdFetchBillingDetails() throws Exception {
        Map response = new HashMap();
        List<BillingHead> billingHeadList = null;

        List<CalculationTrackerGroupByTile> calculationTrackerTileList = new ArrayList<>();

        try {
            List<ExtDataStageDefinitionBilling> billingList = extDataStageDefinitionBillingService.findBySubStatus(ESubscriptionStatus.ACTIVE.getStatus());
            Map<String, List<ExtDataStageDefinitionBilling>> billingListGrouped =
                    billingList.stream().collect(Collectors.groupingBy(w -> w.getRefId()));
            for (String variantId : billingListGrouped.keySet()) {
                Long locId = extDataStageDefinitionBillingService.findLocIdByVariantId(variantId);
                String variantAlias = extDataStageDefinitionBillingService.findVariantAliasByVariantId(variantId);
                PhysicalLocation location = physicalLocationService.findById(locId);
                List<ExtDataStageDefinitionBilling> billingItems = billingListGrouped.get(variantId);
                List<String> subIds = billingItems.stream().map(billItem -> billItem.getSubsId()).collect(Collectors.toList());
                List<CalculationTrackerTemplate> billingHeadListTemp = billingHeadRepository.findBySubscriptionIdList(subIds);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.put("data", null);
            response.put("message", e.getMessage());
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value() + "");
        }
        Gson gson = new Gson();
        String jsonTrackerList = gson.toJson(calculationTrackerTileList);
        response.put("code", HttpStatus.OK);
        response.put("message", "list returned successfully ");
        response.put("data", jsonTrackerList);

        return response;
    }

    @Override
    public BillingHead findBySubscriptionIdAndBillingMonthYear(Long subscriptionId, String billingMonthYear) {
        return billingHeadRepository.findBySubscriptionIdAndBillingMonthYear(subscriptionId, billingMonthYear);
    }

    @Override
    public BillingHead findByCustProdIdAndBillingMonthYear(String custProdId, String billingMonthYear) {
        return billingHeadRepository.findByCustProdIdAndBillingMonthYear(custProdId, billingMonthYear);
    }

    @Override
    public List<Object> findBySubscriptionIdAndIdGtEqId(Long subscriptionId, Long id) {
        return billingHeadRepository.findBySubscriptionIdAndId(subscriptionId, id);
    }

    @Override
    public List<BillingHead> findBySubscriptionStatus(String subscriptionStatus) {
        return billingHeadRepository.findBySubscriptionStatus(subscriptionStatus);
    }

    @Override
    public BillingHead findLastBillHead(Long subscriptionId, Long billHeadId) {
        return billingHeadRepository.findLastBillHead(subscriptionId, billHeadId);
    }

    @Override
    public List<BillingHead> findAllWithoutPaymentTransactionHead() {
        return billingHeadRepository.findAllWithoutPaymentTransactionHead();
    }

    @Override
    public BillingHead findByInvoice(BillingInvoice invoice) {
        return billingHeadRepository.findByInvoice(invoice);
    }

    @Override
    public List<PaymentTransactionPreprocess> getPreprocessTransactionWithSubscriptionType(String subscriptionType,
                                                                                           String billingMonthYear) {
        return billingHeadRepository.getPreprocessTransactionWithSubscriptionType(subscriptionType, billingMonthYear);
    }

    @Override
    public List<PaymentTransactionHeadDetailDTO> getUnpaidTransactionWithSubscriptionTypeAndMonth(String subscriptionType,
                                                                                                  String monthYear) {
        List<PaymentTransactionHeadDetailDTO> paymentTransactionHeadDetailDTOList = customerPaymentInfo.roundOffUnpaidPaymentWithCompanyPreference(
                billingHeadRepository.getPaymentTransactionWithSubscriptionTypeForPremise(subscriptionType, monthYear),
                billingHeadRepository.getPaymentTransactionWithSubscriptionTypeForAcctNo(subscriptionType, monthYear));
        return paymentTransactionHeadDetailDTOList;
    }

    @Override
    public List<PaymentTransactionLineItemsDetailMasterDTO> getUnReconciledTransactionWithSubscriptionTypeAndMonth(String subscriptionType, String monthYear) {
        List<PaymentTransactionLineItemsDetailDTO> paymentTransactionLineItemsDetailDTO =
                customerPaymentInfo.verifyUnReconcilePaymentWithTenantSetting(billingHeadRepository.getPaymentUnreconciledHeadDetailWithSubscriptionType(subscriptionType, monthYear));
        Map<Long, List<PaymentTransactionLineItemsDetailDTO>> map = paymentTransactionLineItemsDetailDTO.stream().collect(Collectors.groupingBy(pth -> pth.getPaymentId()));
        List<PaymentTransactionLineItemsDetailMasterDTO> headDetailMasterList = new ArrayList<>();
        for (Map.Entry<Long, List<PaymentTransactionLineItemsDetailDTO>> obj : map.entrySet()) {
            PaymentTransactionLineItemsDetailDTO paymentDTO = obj.getValue().stream().findAny().get();
            headDetailMasterList.add(new PaymentTransactionLineItemsDetailMasterDTO(paymentDTO.getHeadId(), paymentDTO.getInvoiceAmount(),
                    paymentDTO.getSubscriptionId(), paymentDTO.getInvoiceId(), paymentDTO.getOutstandingAmount(), paymentDTO.getSubscriptionName(), paymentDTO.getGarden(), paymentDTO.getPaymentId(),
                    obj.getValue()));
        }
        return headDetailMasterList;
    }

    /*this method used to take all unreconciled with ReconStatus(paid, inprogress) for autoreconciliation
       here we are not using any month filter which we use in */
    @Override
    public List<PaymentTransactionLineItemsDetailDTO> getUnReconciledTransactionForAutoReconcile() {
        List<PaymentTransactionLineItemsDetailDTO> paymentInfoTemplateDtos = billingHeadRepository.getPaymentDetailAutoReconcileRecords();
        return paymentInfoTemplateDtos;
    }

    @Override
    public List<PaymentTransactionLineItemsDetailMasterDTO> getReverseTransactionWithSubscriptionTypeAndMonth(String subscriptionType, String year) {
        List<PaymentTransactionLineItemsDetailDTO> paymentTransactionLineItemsDetailDTO = customerPaymentInfo.verifyReversePaymentWithTenantSetting(billingHeadRepository.getPaymentReversalHeadDetailWithSubscriptionType(subscriptionType, year));
        Map<Long, List<PaymentTransactionLineItemsDetailDTO>> map = paymentTransactionLineItemsDetailDTO.stream().collect(Collectors.groupingBy(pth -> pth.getPaymentId()));
        List<PaymentTransactionLineItemsDetailMasterDTO> headDetailMasterList = new ArrayList<>();
        for (Map.Entry<Long, List<PaymentTransactionLineItemsDetailDTO>> obj : map.entrySet()) {

            PaymentTransactionLineItemsDetailDTO paymentDTO = obj.getValue().stream().findAny().get();
            headDetailMasterList.add(new PaymentTransactionLineItemsDetailMasterDTO(paymentDTO.getHeadId(), paymentDTO.getInvoiceAmount(),
                    paymentDTO.getSubscriptionId(), paymentDTO.getInvoiceId(), paymentDTO.getOutstandingAmount(), paymentDTO.getSubscriptionName(), paymentDTO.getGarden(), paymentDTO.getPaymentId(),
                    obj.getValue()));
        }
        return headDetailMasterList;
    }

    @Override
    public List<PaymentTransactionPreprocess> getPreprocessTransactionWithSubscriptionRateMatrixId(Long subscriptionId, String billingMonthYear) {
        return billingHeadRepository.getPreprocessTransactionWithSubscriptionRateMatrixId(subscriptionId,
                billingMonthYear);
    }

    // will modify it for monthly graph
    @Override
    public List<PaymentTransactionGraphDTO> getPaymentGraphTransaction(String subscriptionType, String billingYear) throws ParseException {
        List<PaymentTransactionGraphDTO> paymentTransactionUnpaid = billingHeadRepository.getPaymentTransactionUnpaid(subscriptionType, billingYear);
        List<PaymentTransactionGraphDTO> paymentTransactionUnreconciled = billingHeadRepository.getPaymentTransactionUnreconciled(subscriptionType, billingYear);
        List<PaymentTransactionGraphDTO> paymentTransactionReconciled = billingHeadRepository.getPaymentTransactionReconciled(subscriptionType, billingYear);
        List<String> months = Utility.getBarXAxisLabelsForMonths();
        List<PaymentTransactionGraphDTO> graphList = new ArrayList<>();
        months.forEach(month -> {
            int size = graphList.size();
            Double unpaid = 0.0, unreconciled = 0.0, reconciled = 0.0;
            unpaid = paymentTransactionUnpaid.stream().filter(m -> Utility.getMonthFromDate(m.getBilling_month()).equals(month)).mapToDouble(PaymentTransactionGraphDTO::getAmount).sum();
            unreconciled = paymentTransactionUnreconciled.stream().filter(m -> Utility.getMonthFromDate(m.getBilling_month()).equals(month)).mapToDouble(PaymentTransactionGraphDTO::getAmount).sum();
            reconciled = paymentTransactionReconciled.stream().filter(m -> Utility.getMonthFromDate(m.getBilling_month()).equals(month)).mapToDouble(PaymentTransactionGraphDTO::getAmount).sum();
            PaymentTransactionGraphDTO paymentTransactionGraphDTO = new PaymentTransactionGraphDTO(unpaid, unreconciled, reconciled, month);
            graphList.add(paymentTransactionGraphDTO);
        });


        return graphList;
    }

    @Override
    public List<PaymentTransactionGraphDTO> getPaymentYearlyGraphTransaction(String subscriptionType, String billingYear) throws ParseException {
        List<PaymentTransactionGraphDTO> paymentTransactionUnpaid = billingHeadRepository.getPaymentTransactionUnpaid(subscriptionType, billingYear);
        List<PaymentTransactionGraphDTO> paymentTransactionUnreconciled = billingHeadRepository.getPaymentTransactionUnreconciled(subscriptionType, billingYear);
        List<PaymentTransactionGraphDTO> paymentTransactionReconciled = billingHeadRepository.getPaymentTransactionReconciled(subscriptionType, billingYear);
        List<String> months = Utility.getBarXAxisLabelsForMonths();
        List<Double> unpaidMonthlyGraphList = new ArrayList<>();
        List<Double> unReconciledMonthlyGraphList = new ArrayList<>();
        List<Double> reconciledMonthlyGraphList = new ArrayList<>();

        List<PaymentTransactionGraphDTO> graphList = new ArrayList<>();
        months.forEach(month -> {
            double unpaid = 0.0, unreconciled = 0.0, reconciled = 0.0;
            unpaid = paymentTransactionUnpaid.stream().filter(m -> Utility.getMonthFromDate(m.getBilling_month()).equals(month)).mapToDouble(PaymentTransactionGraphDTO::getAmount).sum();
            unreconciled = paymentTransactionUnreconciled.stream().filter(m -> Utility.getMonthFromDate(m.getBilling_month()).equals(month)).mapToDouble(PaymentTransactionGraphDTO::getAmount).sum();
            reconciled = paymentTransactionReconciled.stream().filter(m -> Utility.getMonthFromDate(m.getBilling_month()).equals(month)).mapToDouble(PaymentTransactionGraphDTO::getAmount).sum();

            unpaidMonthlyGraphList.add(utility.round(unpaid, utility.getCompanyPreference().getRounding()));
            unReconciledMonthlyGraphList.add(utility.round(unreconciled, utility.getCompanyPreference().getRounding()));
            reconciledMonthlyGraphList.add(utility.round(reconciled, utility.getCompanyPreference().getRounding()));
        });
        graphList.add(PaymentTransactionGraphDTO.builder().label("Unpaid").data(
                unpaidMonthlyGraphList.toArray(new Double[unpaidMonthlyGraphList.size()])).build());
        graphList.add(PaymentTransactionGraphDTO.builder().label("Paid (Unreconciled)").data(
                unReconciledMonthlyGraphList.toArray(new Double[unReconciledMonthlyGraphList.size()])).build());
        graphList.add(PaymentTransactionGraphDTO.builder().label("Paid (Reconciled)").data(
                reconciledMonthlyGraphList.toArray(new Double[reconciledMonthlyGraphList.size()])).build());
        return graphList;
    }

    @Override
    public List<BillingHead> findAll() {
        return billingHeadRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        billingHeadRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        billingHeadRepository.deleteAll();
    }

    /**
     * JasperInvoice
     * TestFunction
     *
     * @param subscriptionId
     * @return
     */
    @Override
    public List<BillingHead> invoiceBySubscription(Long subscriptionId) {
        return billingHeadRepository.invoiceBySubscription(subscriptionId);
    }

    @Override
    public String skipBillHead(Long billingHeadId, Long skipFlag, Boolean billSkip) {
        BillingHead billingHead = findById(billingHeadId);
        String response = null;
        billingHead.setBillSkip(billSkip);
        if (billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) ||
                billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) ||
                billingHead.getBillStatus().equals(EBillStatus.SKIPPED.getStatus())) {
            if (skipFlag != AppConstants.SKIPPED) {
                billingHead.setBillStatus(EBillStatus.SKIPPED.getStatus());
            } else {
                if (billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus())) {
                    billingHead.setBillStatus(EBillStatus.CALCULATED.getStatus());
                } else if (billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus())) {
                    billingHead.setBillStatus(EBillStatus.PENDING.getStatus());
                } else if (billingHead.getBillStatus().equals(EBillStatus.SKIPPED.getStatus())) {
                    billingHead.setBillStatus(EBillStatus.PENDING.getStatus());
                }
            }
            toUpdateMapper(billingHead);
            response = "BillingHead Id: " + billingHead.getId() + " has been marked as " + billingHead.getBillStatus();
        } else {
            response = "Cannot mark " + billingHead.getBillStatus() + " status as " + EBillStatus.SKIPPED.getStatus();
        }
        return response;
    }

    @Override
    public void skipBillHeadV1(BillingHead billingHead, Long skipFlag, Boolean billSkip) {
        try {
            billingHead.setBillSkip(billSkip);
            if (billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) ||
                    billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) ||
                    billingHead.getBillStatus().equals(EBillStatus.SKIPPED.getStatus())) {
                if (skipFlag != AppConstants.SKIPPED) {
                    billingHead.setBillStatus(EBillStatus.SKIPPED.getStatus());
                } else {
                    if (billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus())) {
                        billingHead.setBillStatus(EBillStatus.CALCULATED.getStatus());
                    } else if (billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus())) {
                        billingHead.setBillStatus(EBillStatus.PENDING.getStatus());
                    } else if (billingHead.getBillStatus().equals(EBillStatus.SKIPPED.getStatus())) {
                        billingHead.setBillStatus(EBillStatus.PENDING.getStatus());
                    }
                }
                toUpdateMapper(billingHead);
                calculationTrackerService.updateBillingLog(billingHead.getId(), billingHead.getBillStatus());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    @Override
    @Async
    public void bulkSkipBillHeadV1(String billingHeadIds, Long skipFlag, Boolean billSkip) {
        List<Long> rowIds =
                Arrays.stream(billingHeadIds.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        List<BillingHead> billingHeadList = findAllByIds(rowIds);
        billingHeadList.forEach(billingHead -> {
            skipBillHeadV1(billingHead, skipFlag, billSkip);
        });
    }

    @Override
    public void updateBillingHeadProcessPaymentLock(List<Long> ids, Boolean lockInd) {
        billingHeadRepository.updateBillingHeadPaymentLockByIds(ids, lockInd);
    }

    @Override
    public void updateBillingHeadProcessReconcileLock(List<Long> ids, Boolean lockInd) {
        billingHeadRepository.updateBillingHeadReconcileLockByIds(ids, lockInd);
    }

    @Override
    public void updateBillingHeadProcessReverseLock(List<Long> ids, Boolean lockInd) {
        billingHeadRepository.updateBillingHeadReverseLockByIds(ids, lockInd);
    }

    @Override
    public List<Long> findByPaymentLockedInd(Boolean paymentLockedInd) {
        return billingHeadRepository.getBillingHeadByPaymentLocked(paymentLockedInd);
    }

    @Override
    public List<Long> findByReverseLockedInd(Boolean reverseLockedInd) {
        return billingHeadRepository.getBillingHeadByReverseLocked(reverseLockedInd);
    }

    @Override
    public List<Long> findByReconcileLockedInd(Boolean reconcileLockedInd) {
        return billingHeadRepository.getBillingHeadByReconcileLocked(reconcileLockedInd);
    }

    @Override
    public Map<String, String> getInvoicePlaceholders(BillingHead billingHead) {
        User user = userService.findById(billingHead.getUserAccountId());
        Map<String, String> placeholderValues = new HashMap<>();

        PhysicalLocation location = null;
        String hookConstant = null;
        PortalAttributeTenantDTO portalAttributeTenant = null;
        try {
            String firstName = user.getFirstName();
            String lastName = user.getLastName() != null ? user.getLastName() : " ";
            CustomerSubscription customerSubscription = customerSubscriptionRepository.findById(billingHead.getSubscriptionId()).get();
            TransStageHead transStageHead = transStageHeadService.findBySubsId(customerSubscription.getExtSubsId());
            if (transStageHead != null) {
                List<TransStageTemp> transStageTempList = transStageTempService.findAllByTJobId(transStageHead.getTjobId());
                Variant variant = dataExchange.getSubscriptionMapping(String.valueOf(customerSubscription.getExtSubsId()),
                        DBContextHolder.getTenantName()).getVariant();

                String invoiceTemplate = transStageTempList.stream().filter(rt -> rt.getMeasCode().equals(AppConstants.SYSTEM_INVOICE_HTML_TEMPLATE)).map(TransStageTemp::getValue).findFirst().orElse(null);
                String gardenName = variant.getVariantAlias();
                String SCSGNDetail =
                        transStageTempList.stream().filter(rt -> rt.getMeasCode().equals("SCSGN")).map(TransStageTemp::getValue).findFirst().orElse(null);
                String premiseNo =
                        transStageTempList.stream().filter(rt -> rt.getMeasCode().equals("S_PN")).map(TransStageTemp::getValue).findFirst().orElse(null);
                String discountCode =
                        transStageTempList.stream()
                                .filter(rt -> Arrays.asList(Constants.DISCOUNT_RATE_CODES.S_DSC, Constants.DISCOUNT_RATE_CODES.DSCP).contains(rt.getMeasCode())).map(TransStageTemp::getValue).findFirst().orElse(null);
                if (variant.getSitePhysicalLocId() != null) {
                    location = physicalLocationService.findById(Long.valueOf(variant.getSitePhysicalLocId()));
                }

                String billingAdd = getFormattedLocation(physicalLocationService.findById(Long.parseLong(transStageTempList.stream().filter(rt -> rt.getMeasCode().equals("SADD")).map(TransStageTemp::getValue).findFirst().orElse(null))));

                portalAttributeTenant = portalAttributeOverrideService.findByIdFetchPortalAttributeValues(customerSubscription.getInvoiceTemplateId());
                if (portalAttributeTenant != null) {
                    Optional<WorkflowHookMaster> workflowHookMaster = workFlowHookMasterRepository.findById(portalAttributeTenant.getWfId());
                    hookConstant = workflowHookMaster.isPresent() ? workflowHookMaster.get().getHookConstant() : null;
                }
                CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(Long.valueOf(DBContextHolder.getTenantName().replace("ec", "")));
                List<TenantConfig> tenantConfig = tenantConfigService.findAllByParameterIn(Arrays.asList(AppConstants.RemitNameParamTenantConfig, AppConstants.RemitAddressParamTenantConfig, AppConstants.FooterNoteParamTenantConfig));
                List<BillingDetail> billingDetailList = billingDetailRepository.findByBillingHead(billingHead);
                Optional<BillingDetail> optionalMpa = billingDetailList.stream().filter(s -> "MPA".equalsIgnoreCase(s.getBillingCode())).findFirst();
                Optional<BillingDetail> optionalAbcre = billingDetailList.stream().filter(s -> "ABCRE".equalsIgnoreCase(s.getBillingCode())).findFirst();
                BillingDetail mpa = optionalMpa.isPresent() ? optionalMpa.get() : null;
                BillingDetail abcre = optionalAbcre.isPresent() ? optionalAbcre.get() : null;

                List<RuleExecutionLog> ruleExecutionLogs =
                        ruleExecutionLogService.findAllByBillIdAndRateCodeOrderByIdDesc(billingHead.getId(), "SRTE");

                Set<BillingDetail> billingDetailSet = new HashSet<>(billingDetailList);
                //*********************************ITERATIONS*********************************//
                placeholderValues = iterations(placeholderValues, billingDetailSet);
                //*********************************ITERATIONS*********************************//
                placeholderValues = fillPlaceHolderValues(placeholderValues, companyPreference, firstName, lastName, billingHead, billingAdd,
                        gardenName, customerSubscription.getExtSubsId(), premiseNo, invoiceTemplate, discountCode, location, SCSGNDetail, mpa, abcre, ruleExecutionLogs,
                        tenantConfig, portalAttributeTenant, hookConstant, billingDetailSet);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return placeholderValues;
    }

    private Map iterations(Map<String, String> placeholderValues, Set<BillingDetail> billingDetailList) {
        int lineNo = 0;
        for (BillingDetail bd : billingDetailList) {
            Double lineAmount = bd.getValue();
            String description = null;
            PortalAttributeValueTenantDTO billCodeValue = attributeOverrideService.findByAttributeValue(bd.getBillingCode());
            if (billCodeValue != null) {
                description = billCodeValue.getDescription();
            }
            String billCode = null;
            if (description != null) {
                billCode = description;
            } else {
                continue;
            }
            if (bd.getAddToBillAmount() || (bd.getBillingCode().equalsIgnoreCase(Constants.BILLING_CODES.ABCRE) && bd.getValue() != 0)) {
                ++lineNo;
                placeholderValues.put(billCode, "$".concat(String.format("%.2f", lineAmount)));
            }
        }
        placeholderValues.put("lineNo", String.valueOf(lineNo));
        return placeholderValues;
    }

    private Map fillPlaceHolderValues(Map<String, String> placeholderValues, CompanyPreference companyPreference,
                                      String firstName, String lastName, BillingHead billingHead, String billingAdd, String gardenName, String subsId,
                                      String premiseNo, String invoiceTemplate, String discountCode, PhysicalLocation location, String SCSGNDetail, BillingDetail mpa, BillingDetail abcre,
                                      List<RuleExecutionLog> ruleExecutionLogs, List<TenantConfig> tenantConfig, PortalAttributeTenantDTO portalAttributeTenant, String hookConstant, Set<BillingDetail> billingDetailSet) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        placeholderValues.put("tenant_name", companyPreference.getCompanyName() != null ? companyPreference.getCompanyName() : " ");
        placeholderValues.put("billing_value", decimalFormat.format(billingHead.getAmount()));
        placeholderValues.put("first_last_name", firstName + " " + lastName);
        placeholderValues.put("invoice_id", billingHead.getInvoice() != null ? billingHead.getInvoice().getId().toString() : " ");
//        placeholderValues.put("bill_head_id", String.valueOf(billingDetailSet));
        placeholderValues.put("customer_no", billingHead.getUserAccountId() + "-" + subsId + "-" + premiseNo);
        placeholderValues.put("invoice_date", billingHead.getInvoice() != null ? billingHead.getInvoice().getDateOfInvoice().toString() : " ");
        placeholderValues.put("billing_month", Utility.getFullMonthYear(Integer.parseInt(billingHead.getBillingMonthYear().split("-")[0]), Integer.parseInt(billingHead.getBillingMonthYear().split("-")[1])));
        placeholderValues.put("billing_address", billingAdd != null ? billingAdd : " ");
        placeholderValues.put("due_date", billingHead.getDueDate() != null ? billingHead.getDueDate().toString() : " ");
        placeholderValues.put("garden_name", gardenName != null ? gardenName : " ");
        placeholderValues.put("garden_src", SCSGNDetail != null ? SCSGNDetail : " ");
        placeholderValues.put("mpa", mpa != null ? (mpa.getValue() != null ? decimalFormat.format(mpa.getValue()) : " ") : " ");
        placeholderValues.put("srte", ruleExecutionLogs.size() == 0 ? "" :
                ruleExecutionLogs.get(0).getReturnedValue() != null ?
                        ruleExecutionLogs.get(0).getReturnedValue().toString().concat("/kWh") : "");
        placeholderValues.put("abcre", abcre != null ? (abcre.getValue() != null ? decimalFormat.format(abcre.getValue()) : " ") : " ");
        placeholderValues.put("invoice_template", invoiceTemplate != null ? invoiceTemplate : " ");
        placeholderValues.put("dscp", discountCode != null ? discountCode : String.valueOf(0.0));
        placeholderValues.put("garden_address", getFormattedLocation(location));
        placeholderValues.put("total", billingHead.getAmount() != null ? decimalFormat.format(billingHead.getAmount()) : String.valueOf(0.0));
        placeholderValues.put("remit_name", tenantConfig.size() != 0 ? tenantConfig.stream().filter(rtName -> rtName.getParameter().equalsIgnoreCase(AppConstants.RemitNameParamTenantConfig)).findFirst().orElse(null).getText() : " ");
        placeholderValues.put("remit_address", tenantConfig.size() != 0 ? tenantConfig.stream().filter(rtName -> rtName.getParameter().equalsIgnoreCase(AppConstants.RemitAddressParamTenantConfig)).findFirst().orElse(null).getText() : " ");
        placeholderValues.put("remit_note", tenantConfig.size() != 0 ? tenantConfig.stream().filter(rtName -> rtName.getParameter().equalsIgnoreCase(AppConstants.FooterNoteParamTenantConfig)).findFirst().orElse(null).getText() : " ");
        placeholderValues.put("wfId", portalAttributeTenant != null ? portalAttributeTenant.getWfId().toString() : null);
        placeholderValues.put("hookConstant", hookConstant);
        placeholderValues.put("company_logo", companyPreference.getLogo() != null ? companyPreference.getLogo() : null);

        return placeholderValues;
    }

    @Async
    @Override
    public void extractPendingBillsForCalTracker(String period) {
        List<String> periodList = new ArrayList<>();
        if (period != null) {
            periodList = Arrays.stream(period.split(",")).filter(s -> s != null && !s.equalsIgnoreCase("null")).collect(Collectors.toList());
        }
        if (period == null || periodList.size() == 0) {
            periodList = Arrays.asList(Utility.getLastMonth());
        }
        List<BillingHead> billingHeadList = billingHeadRepository.getPendingBillsByBillingPeriod(periodList);
        List<Long> billHeadIds = billingHeadList.stream().map(BillingHead::getId).collect(Collectors.toList());
        List<CalculationDetails> existingCalculationDetails = calculationDetailsService.findAllBySourceIds(billHeadIds);
        if (existingCalculationDetails.size() > 0) {
            billingHeadList.removeIf(bill -> existingCalculationDetails.stream().anyMatch(calDetail -> calDetail.getSourceId().equals(bill.getId())));
        }
        billingHeadList.forEach(billingHeadTemp -> {  //Creating Calculation Detail Items
            calculationDetailsService.addOrUpdate(CalculationDetails.builder().source("BILLING")
                    .sourceId(billingHeadTemp.getId())
                    .attemptCount(0)
                    .publishState("NA")
                    .state(billingHeadTemp.getBillStatus()).build());
        });
    }

    private String getFormattedLocation(PhysicalLocation location) {
        return location.getAdd1() != null ? location.getAdd1() : "" + " , " + location.getAdd2() != null ? location.getAdd2() : "" + " , "
                + location.getZipCode() != null ? location.getZipCode() : "" + " , " + location.getAdd3() != null ? location.getAdd3() : "";
    }

    /**
     * @param response
     * @return
     * @Description: this method use to return billing status data for pie chart
     * @created_by : sana
     * @created_date: 4/4/2023
     */

    @Override
    public Map getBillingByStatusData(Map response, List<String> periodList) {

        List<AdminBillingDashboardTile> queryResult = new ArrayList<>();
        User currentUser = userService.getLoggedInUser();
        List<String> billingStatus = new ArrayList<>();
        billingStatus.add(EBillStatus.PAID.getStatus());
        billingStatus.add(EBillStatus.IN_PAYMENT.getStatus());
        billingStatus.add(EBillStatus.INVOICED.getStatus());
        billingStatus.add(EBillStatus.PUBLISHED.getStatus());
        List countList = new ArrayList();
        try {

            if (currentUser.getUserType().getId() == 2) {
                queryResult = billingHeadRepository.getBillingByStatus(billingStatus, periodList);
            }
            if (queryResult.size() > 0) {
                OptionalLong paid = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.PAID.getStatus())).mapToLong(AdminBillingDashboardTile::getStatusCount).findFirst();
                OptionalLong invoiced = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.INVOICED.getStatus())).mapToLong(AdminBillingDashboardTile::getStatusCount).findFirst();
                OptionalLong published = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.PUBLISHED.getStatus())).mapToLong(AdminBillingDashboardTile::getStatusCount).findFirst();
                OptionalLong inPayment = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.IN_PAYMENT.getStatus())).mapToLong(AdminBillingDashboardTile::getStatusCount).findFirst();
                countList.add(paid.isPresent() ? paid.getAsLong() : 0);
                countList.add(inPayment.isPresent() ? inPayment.getAsLong() : 0);
                countList.add(invoiced.isPresent() ? invoiced.getAsLong() : 0);
                countList.add(published.isPresent() ? published.getAsLong() : 0);
            }

            response.put("labels", billingStatus.toArray());
            response.put("datasets", countList.toArray());
            response.put("code", HttpStatus.OK.toString());
            response.put("message", "list returned successfully");

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
            response.put("labels", billingStatus.toArray());
            response.put("datasets", countList.toArray());
        }

        return response;
    }

    /**
     * @param response
     * @return
     * @Description: this method use to return billing status data for bar chart.. it shows customer type data group by
     * billing head status
     * @created_by : sana
     * @created_date: 4/4/2023
     */

    @Override
    public Map getCustomerTypeGroupByBillingStatusData(Map response, List<String> periodList) {

        List<AdminBillingDashboardTile> queryResult = new ArrayList<>();
        User currentUser = userService.getLoggedInUser();
        List<String> billingStatus = new ArrayList<>();
        billingStatus.add(EBillStatus.PAID.getStatus());
        billingStatus.add(EBillStatus.IN_PAYMENT.getStatus());
        billingStatus.add(EBillStatus.INVOICED.getStatus());
        billingStatus.add(EBillStatus.PUBLISHED.getStatus());

        List<String> customerTypes = new ArrayList<>();
        customerTypes.add(EEntityType.INDIVIDUAL.getEntityType());
        customerTypes.add(EEntityType.NONPROFIT.getEntityType());
        customerTypes.add(EEntityType.RESIDENTIAL.getEntityType());
        customerTypes.add(EEntityType.COMMERCIAL.getEntityType());
        customerTypes.add(EEntityType.INDUSTRIAL.getEntityType());

        List<BillingStatusGraphDTO> dataSets = new ArrayList<>();
        try {
            if (currentUser.getUserType().getId() == 2) {
                queryResult = billingHeadRepository.getCustomerTypeDataGroupByBillingStatus(billingStatus, periodList);
            }
            if (queryResult.size() > 0) {
                List<com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile> paid = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.PAID.getStatus())).collect(Collectors.toList());
                List<com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile> invoiced = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.INVOICED.getStatus())).collect(Collectors.toList());
                ;
                List<com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile> published = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.PUBLISHED.getStatus())).collect(Collectors.toList());
                ;
                List<com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile> inPayment = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.IN_PAYMENT.getStatus())).collect(Collectors.toList());
                ;

                dataSets.add(BillingStatusGraphDTO.builder().label(EBillStatus.PAID.getStatus()).data(getDataSet(paid)).build());
                dataSets.add(BillingStatusGraphDTO.builder().label(EBillStatus.IN_PAYMENT.getStatus()).data(getDataSet(inPayment)).build());
                dataSets.add(BillingStatusGraphDTO.builder().label(EBillStatus.INVOICED.getStatus()).data(getDataSet(invoiced)).build());
                dataSets.add(BillingStatusGraphDTO.builder().label(EBillStatus.PUBLISHED.getStatus()).data(getDataSet(published)).build());

            }

            response.put("labels", customerTypes.toArray());
            response.put("datasets", dataSets);
            response.put("code", HttpStatus.OK.toString());
            response.put("message", "list returned successfully");

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
            response.put("labels", customerTypes.toArray());
            response.put("datasets", dataSets);
        }

        return response;
    }

    @Override
    public Map getBillingStatusComparisonData(Map response) {
        List<AdminBillingDashboardTile> queryResult = new ArrayList<>();
        User currentUser = userService.getLoggedInUser();
        List<BillingStatusGraphDTO> dataSets = new ArrayList<>();
        List<String> billingStatus = new ArrayList<>();
        List<String> periodList = new ArrayList<>();
        billingStatus.add(EBillStatus.PAID.getStatus());
        billingStatus.add(EBillStatus.IN_PAYMENT.getStatus());
        billingStatus.add(EBillStatus.INVOICED.getStatus());
        billingStatus.add(EBillStatus.PUBLISHED.getStatus());
        try {
            for (int i = 5; i >= 0; i--) {
                YearMonth date = YearMonth.now().minusMonths(i);
                periodList.add(date.format(DateTimeFormatter.ofPattern("MM-yyyy")));
            }
            if (currentUser.getUserType().getId() == 2) {
                queryResult = billingHeadRepository.getBillingStatusComparisonData(periodList, billingStatus);
            }
            if (queryResult.size() > 0) {
                List<com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile> paid = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.PAID.getStatus())).collect(Collectors.toList());
                List<com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile> invoiced = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.INVOICED.getStatus())).collect(Collectors.toList());
                ;
                List<com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile> published = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.PUBLISHED.getStatus())).collect(Collectors.toList());
                ;
                List<com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile> inPayment = queryResult.stream().filter(result -> result.getBillStatus().equalsIgnoreCase(EBillStatus.IN_PAYMENT.getStatus())).collect(Collectors.toList());
                ;
                dataSets.add(BillingStatusGraphDTO.builder().label(EBillStatus.PAID.getStatus()).data(getDataSetForMonths(periodList, paid)).build());
                dataSets.add(BillingStatusGraphDTO.builder().label(EBillStatus.IN_PAYMENT.getStatus()).data(getDataSetForMonths(periodList, inPayment)).build());
                dataSets.add(BillingStatusGraphDTO.builder().label(EBillStatus.INVOICED.getStatus()).data(getDataSetForMonths(periodList, invoiced)).build());
                dataSets.add(BillingStatusGraphDTO.builder().label(EBillStatus.PUBLISHED.getStatus()).data(getDataSetForMonths(periodList, published)).build());

            }

            response.put("labels", getLabelsMonthsName(periodList));
            response.put("datasets", dataSets);
            response.put("code", HttpStatus.OK.toString());
            response.put("message", "list returned successfully");

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
            response.put("labels", getLabelsMonthsName(periodList));
            response.put("datasets", dataSets);
        }

        return response;
    }

    private List<Long> getDataSet(List<com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile> queryResult) {
        Optional<AdminBillingDashboardTile> individualOptional = queryResult.stream().filter(obj -> obj.getCustomerType().equalsIgnoreCase(EEntityType.INDIVIDUAL.name())).findFirst();
        Optional<AdminBillingDashboardTile> nonprofitOptional = queryResult.stream().filter(obj -> obj.getCustomerType().equalsIgnoreCase(EEntityType.NONPROFIT.name())).findFirst();
        Optional<AdminBillingDashboardTile> residentailOptional = queryResult.stream().filter(obj -> obj.getCustomerType().equalsIgnoreCase(EEntityType.RESIDENTIAL.name())).findFirst();
        Optional<AdminBillingDashboardTile> commercialOptional = queryResult.stream().filter(obj -> obj.getCustomerType().equalsIgnoreCase(EEntityType.COMMERCIAL.name())).findFirst();
        Optional<AdminBillingDashboardTile> industrialOptional = queryResult.stream().filter(obj -> obj.getCustomerType().equalsIgnoreCase(EEntityType.INDUSTRIAL.name())).findFirst();
        List<Long> data = new ArrayList<>();
        data.add(individualOptional.isPresent() ? individualOptional.get().getCustomerTypeCount() : 0);
        data.add(nonprofitOptional.isPresent() ? nonprofitOptional.get().getCustomerTypeCount() : 0);
        data.add(residentailOptional.isPresent() ? residentailOptional.get().getCustomerTypeCount() : 0);
        data.add(commercialOptional.isPresent() ? commercialOptional.get().getCustomerTypeCount() : 0);
        data.add(industrialOptional.isPresent() ? industrialOptional.get().getCustomerTypeCount() : 0);
        return data;
    }

    private List<Long> getDataSetForMonths(List<String> periodList, List<com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile> queryResult) {
        List<Long> data = new ArrayList<>();
        for (String dateStr : periodList) {
            Optional<AdminBillingDashboardTile> dataOptional = queryResult.stream().filter(obj -> obj.getBillingMonthYear().equalsIgnoreCase(dateStr)).findFirst();
            data.add(dataOptional.isPresent() ? dataOptional.get().getStatusCount() : 0);
        }
        return data;
    }

    private List<String> getLabelsMonthsName(List<String> periodList) {
        List<String> list = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat(Utility.MONTH_YEAR_FORMAT);
        SimpleDateFormat formatMonthDate = new SimpleDateFormat(Utility.MONTH_FORMAT);
        try {
            for (String str : periodList) {
                String monthName = formatMonthDate.format(format.parse(str));
                System.out.println(monthName);
                list.add(monthName);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return list;

    }

    @Override
    public List<BillingHead> saveAll(List<BillingHead> billingHeads) {
        return billingHeadRepository.saveAll(billingHeads);
    }

    @Override
    public CustomerSubscription findCustomerSubscriptionBySubscriptionId(Long subscriptionId) {
        return customerSubscriptionRepository.findById(subscriptionId).orElseThrow();
    }

    @Override
    public List<BillingHead> getBillingInfo(List<Long> accountId) {
        return billingHeadRepository.getBillingInfo(accountId);
    }

    @Override
    public List<BillingHead> manageBillingHeadForProjection(List<CustomerSubscription> customerSubscriptions, List<String> months) {
        List<BillingHead> finalBillingHeadList = new ArrayList<>();
        List<Long> subsId = customerSubscriptions.stream().map(CustomerSubscription::getId).distinct().collect(Collectors.toList());
        List<BillingHead> existingBillingHeads = billingHeadRepository.findBillingProjectionBySubsIdAndPeriod(subsId, months);
        customerSubscriptions.stream().forEach(customerSubscription -> {
            months.stream().forEach(month -> {
                Long billId = null;
                Optional<BillingHead> billingHeadOptional = existingBillingHeads.stream().filter(obj -> obj.getBillingMonthYear().trim().equals(month.trim()) && obj.getSubscriptionId().equals(customerSubscription.getId())).findFirst();
                if (billingHeadOptional.isPresent()) {
                    billId = billingHeadOptional.get().getId();
                }
                finalBillingHeadList.add(BillingHead.builder().id(billId).subscriptionId(customerSubscription.getId()).billingMonthYear(month).billStatus(EBillStatus.PENDING.getStatus()).build());
            });
        });
        Map<String, BillingHead> uniqueEntries = new HashMap<>();
        for (BillingHead head : finalBillingHeadList) {
            String key = head.getBillingMonthYear() + head.getSubscriptionId();
            if (!uniqueEntries.containsKey(key) || head.getId() != null) {
                uniqueEntries.put(key, head);
            }
        }

        List<BillingHead> distinctHeads = new ArrayList<>(uniqueEntries.values());
        List<BillingHead> dbResult =billingHeadRepository.saveAll(distinctHeads);
        List<Long> billHeadIds = dbResult.stream().map(BillingHead::getId).collect(Collectors.toList());
        List<CalculationDetails> existingCalculationDetails = calculationDetailsService.findAllBySourceIds(billHeadIds);
        if (existingCalculationDetails.size() > 0) {
            dbResult.removeIf(bill -> existingCalculationDetails.stream().anyMatch(calDetail -> calDetail.getSourceId().equals(bill.getId())));
        }
        dbResult.forEach(billingHeadTemp -> {  //Creating Calculation Detail Items
            calculationDetailsService.addOrUpdate(CalculationDetails.builder().source("BILLING")
                    .sourceId(billingHeadTemp.getId())
                    .attemptCount(0)
                    .publishState("NA")
                    .state(billingHeadTemp.getBillStatus()).build());
        });
        return dbResult;
    }

    @Override
    public List<BillingHead> findAllBillingHeadForProjection(List<CustomerSubscription> customerSubscriptions, List<String> months) {
        List<BillingHead> finalBillingHeadList = new ArrayList<>();
        List<Long> subsId = customerSubscriptions.stream().map(CustomerSubscription::getId).distinct().collect(Collectors.toList());
        List<BillingHead> existingBillingHeads = billingHeadRepository.findBillingProjectionBySubsIdAndPeriod(subsId, months);
        return existingBillingHeads;
    }

    public Map<String, String> getProjectionPlaceholders(BillingHead billingHead,List<String> months) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        Map<String, String> placeholderValues = new HashMap<>();

        try {

            ExtDataStageDefinition extDataStageDefinition = extDataStageDefinitionService.findByCustomerSubscriptionId(billingHead.getSubscriptionId());
            String systemSizeJson = Utility.getMeasureAsJson(extDataStageDefinition.getMpJson(), Constants.RATE_CODES.S_GS);
            List<RuleExecutionLog> ruleExecutionLogs =
                    ruleExecutionLogService.findAllByBillIdAndRateCodeOrderByIdDesc(billingHead.getId(), "SRTE");
            List<String> nextThreeMonths = Utility.getCapitalizedNextThreeMonths(months);
            CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(Long.valueOf(DBContextHolder.getTenantName().replace("ec", "")));

            placeholderValues.put("generated_at", Utility.formatUTCDate(Utility.getTodayUTC()));
            placeholderValues.put("garden_name", extDataStageDefinition.getRefType());
            placeholderValues.put("billing_category", Constants.PROJECTION_REVENUE.BILLING_CATEGORY);
            placeholderValues.put("system_size", systemSizeJson.trim().equals("") ? "0" : systemSizeJson);
            placeholderValues.put("location", extDataStageDefinition.getCustAdd() != null ? getFormattedLocation(physicalLocationService.findById(extDataStageDefinition.getCustAdd())) : "");
            placeholderValues.put("ppa_rate", ruleExecutionLogs.size() == 0 ? "" : ruleExecutionLogs.get(0).getReturnedValue() != null ? decimalFormat.format(ruleExecutionLogs.get(0).getReturnedValue()).concat("/kWh") : "");
            placeholderValues.put("category", Constants.PROJECTION_REVENUE.REVENUE_CATEGORY);
            placeholderValues.put("type", Constants.PROJECTION_REVENUE.REVENUE_TYPE);
            placeholderValues.put("description", Constants.PROJECTION_REVENUE.DESCRIPTION);
            placeholderValues.put("current_month", Utility.getCurrentMonth());
            placeholderValues.put("company_name", companyPreference.getCompanyName() != null ? companyPreference.getCompanyName() : " ");
            placeholderValues.put("company_logo", companyPreference.getLogo() != null ? companyPreference.getLogo() : null);
            placeholderValues.put("month_1", nextThreeMonths.get(0));
            placeholderValues.put("month_2", nextThreeMonths.get(1));
            placeholderValues.put("month_3", nextThreeMonths.get(2));
            placeholderValues.put("hookConstant", Constants.PROJECTION_REVENUE.PROJECTION_REVENUE_HOOK);
            placeholderValues.put("invoice_template", Constants.PROJECTION_REVENUE.INVOICE_TEMPLATE);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return placeholderValues;
    }
}
