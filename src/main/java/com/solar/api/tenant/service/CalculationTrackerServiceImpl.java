package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mchange.util.AlreadyExistsException;
import com.solar.api.AppConstants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Message;
import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionDetailTemplate;
import com.solar.api.tenant.mapper.billing.calculation.CalculationTrackerPeriodDTO;
import com.solar.api.tenant.mapper.billingCredits.BillingCreditsManualDTO;
import com.solar.api.tenant.mapper.billingCredits.BillingCreditsManualMasterDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.mapper.tiles.calculationTracker.CalculationTrackerGroupByTile;
import com.solar.api.tenant.mapper.tiles.calculationTracker.CalculationTrackerTile;
import com.solar.api.tenant.mapper.workOrder.MongoCustomerDetailWoDTO;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.billing.calculation.CalculationTracker;
import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.billing.calculation.CalculationTrackerMapper.toUpdatedCalculationTracker;

@Service
public class CalculationTrackerServiceImpl implements CalculationTrackerService {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private CalculationTrackerRepository calculationTrackerRepository;
    @Autowired
    private CalculationDetailsRepository calculationDetailsRepository;
    @Lazy
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private BillingHeadRepository billingHeadRepository;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private PhysicalLocationService physicalLocationService;
    @Autowired
    private BillingDetailRepository billingDetailRepository;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private Gson gson;
    @Autowired
    private BillingCreditsService billingCreditsService;
    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;
    @Autowired
    private BillingCreditsRepository billingCreditsRepository;
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;

    @Override
    public CalculationTracker addOrUpdate(CalculationTracker calculationTracker) throws AlreadyExistsException {
        if (calculationTracker.getId() != null) {
            CalculationTracker calculationTrackerData = findById(calculationTracker.getId());
            if (calculationTrackerData == null) {
                throw new NotFoundException(CalculationTracker.class, calculationTracker.getId());
            }
            return calculationTrackerRepository.save(toUpdatedCalculationTracker(calculationTrackerData,
                    calculationTracker));
        }
        return calculationTrackerRepository.save(calculationTracker);
    }

    @Override
    public CalculationTracker findById(Long id) {
        return calculationTrackerRepository.findById(id).orElseThrow(() -> new NotFoundException(CalculationTracker.class, id));
    }

    @Override
    public List<CalculationTracker> findAll() {
        return calculationTrackerRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        calculationTrackerRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        calculationTrackerRepository.deleteAll();
    }

    @Override
    public CalculationDetails updateBillingLog(Long billHeadId, String state) {
        Optional<CalculationDetails> calculationDetailsDB = calculationDetailsRepository.findBySourceId(billHeadId);
        if (calculationDetailsDB.isPresent()) {
            calculationDetailsDB.get().setState(state);
            return calculationDetailsRepository.save(calculationDetailsDB.get());
        }
        return null;
    }

    @Override
    public CalculationDetails updateBillingLogCalculation(Long billHeadId, String state) {
        Optional<CalculationDetails> calculationDetailsDB = calculationDetailsRepository.findBySourceId(billHeadId);
        if (calculationDetailsDB.isPresent()) {
            calculationDetailsDB.get().setState(state);
            calculationDetailsDB.get().setAttemptCount(0);
            calculationDetailsDB.get().setErrorMessage(null);
            calculationDetailsDB.get().setErrorInd("N");
            calculationDetailsDB.get().setPublishState("READY");
            return calculationDetailsRepository.save(calculationDetailsDB.get());
        }
        return null;
    }

    @Override
    public CalculationDetails updateBillingLogInvoice(Long billHeadId, String state, Long invoiceId, Date invoiceDate, Date dueDate) {
        String invoiceDateString = Utility.getDateString(invoiceDate, Utility.INVOICE_SHORT_MONTH_DATE_FORMAT);
        String dueDateString = Utility.getDateString(dueDate, Utility.INVOICE_SHORT_MONTH_DATE_FORMAT);
        Optional<CalculationDetails> calculationDetailsDB = calculationDetailsRepository.findBySourceId(billHeadId);
        if (calculationDetailsDB.isPresent()) {
            calculationDetailsDB.get().setState(state);
            calculationDetailsDB.get().setInvoiceId(invoiceId);
            calculationDetailsDB.get().setLockedInd(true);
            String template = calculationDetailsDB.get().getPrevInvHtmlView();
            if (template.contains("PAYMENT NOTICE")) {
                template = template.replace("PAYMENT NOTICE",
                        "<div style=\"background-color:#1BC77A;padding: 4px 10px;position: absolute;top:-55px;border-radius: 0 0 4px 4px;color: #fff;font-weight: 500;font-size: 16px;\"> IN PAYMENT </div>PAYMENT NOTICE");
            } else if (template.contains("INVOICE")) {
                template = template.replace("INVOICE",
                        "<div style=\"background-color:#1BC77A;padding: 4px 10px;position: absolute;top:-55px;border-radius: 0 0 4px 4px;color: #fff;font-weight: 500;font-size: 16px;\"> IN PAYMENT </div>INVOICE");
            }
            template = template.replace("INV-", "INV-" + invoiceId);
            template = template.replace("InvoiceDate</td><td style=\"font-size:16px; color:#212121; margin-bottom:0px;\"> ", "Invoice Date</td><td style=\"font-size:16px; color:#212121; margin-bottom:0px;\">" + invoiceDateString);
            template = template.replace("Due Date</td><td style=\"font-size:18px; color:#fd2121; margin-bottom:0px;\"> ", "Due Date</td><td style=\"font-size:18px; color:#fd2121; margin-bottom:0px;\">" + dueDateString);
            template = template.replace("Invoice Date</div><div class=\"text\"> ", "Invoice Date</div><div class=\"text\"> " + invoiceDateString);
            template = template.replace("Due Date</div><div class=\"text blue-text\"> ", "Due Date</div><div class=\"text blue-text\"> " + dueDateString);
            calculationDetailsDB.get().setPrevInvHtmlView(template);
            return calculationDetailsRepository.save(calculationDetailsDB.get());
        }
        return null;
    }

    @Override
    public CalculationDetails updateBillingLogError(Long billHeadId, String error) {

        try {
            Optional<CalculationDetails> calculationDetailsDB = calculationDetailsRepository.findBySourceId(billHeadId);
            Integer maxErrorCount = Integer.parseInt(tenantConfigService.findByParameter(AppConstants.BillReAttemptCount).get().getText());
            if (calculationDetailsDB.isPresent()) {
                if (calculationDetailsDB.get().getAttemptCount() < maxErrorCount && calculationDetailsDB.get().getAttemptCount() != -1) {
                    calculationDetailsDB.get().setAttemptCount(calculationDetailsDB.get().getAttemptCount() + 1);
                    calculationDetailsDB.get().setErrorInd("Y");
                    calculationDetailsDB.get().setErrorMessage(error);
                } else if (calculationDetailsDB.get().getAttemptCount() == maxErrorCount && calculationDetailsDB.get().getReCalcInd() != null && calculationDetailsDB.get().getReCalcInd()) {
                    calculationDetailsDB.get().setAttemptCount(-1);
                    calculationDetailsDB.get().setErrorInd("Y");
                    calculationDetailsDB.get().setErrorMessage(error);
                } else {
                    BillingHead billingHead = billingHeadService.findById(billHeadId);
                    billingHead.setBillStatus(EBillStatus.ERROR.getStatus());
                    calculationDetailsDB.get().setState(EBillStatus.ERROR.getStatus());
                    calculationDetailsDB.get().setErrorMessage(error);
                    billingHeadService.toUpdateMapper(billingHead);
                }

                return calculationDetailsRepository.save(calculationDetailsDB.get());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }

        return null;
    }

    @Override
    public Map getCalculationTrackerList(Map response, String groupBy, List<String> periodList) {
        String jsonTrackerList = "";
        Map<String, List<MongoCustomerDetailWoDTO>> subscriptionData = null;
        Map<String, List<CalculationTrackerTile>> calTrackerGroupBy = null;
        List<String> activeIds = null;
        List<CalculationTrackerTile> resultList = new ArrayList<>();
        List<CalculationTrackerGroupByTile> resultGroupByList = new ArrayList<>();
        try {
            resultList.addAll(fillMissingFields(billingHeadRepository.findCalculationTrackerTileBySubsId(periodList)));
            switch (groupBy) {
                case "NONE":
                    jsonTrackerList = gson.toJson(resultList);
                    break;
                case "SOURCE":
                    calTrackerGroupBy = resultList.stream().collect(Collectors.groupingBy(CalculationTrackerTile::getSourceName));
                    jsonTrackerList = gson.toJson(getCalTrackerGroupBySource(calTrackerGroupBy, resultGroupByList, groupBy));
                    break;
                case "CUSTOMER":
                    calTrackerGroupBy = resultList.stream().collect(Collectors.groupingBy(grouping -> grouping.getCustomerDetailDTO().getCustomerName()));
                    jsonTrackerList = gson.toJson(getCalTrackerGroupBySource(calTrackerGroupBy, resultGroupByList, groupBy));
                    break;
                case "STATUS":
                    calTrackerGroupBy = resultList.stream().collect(Collectors.groupingBy(CalculationTrackerTile::getStatus));
                    jsonTrackerList = gson.toJson(getCalTrackerGroupBySource(calTrackerGroupBy, resultGroupByList, groupBy));
                    break;
            }
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "list returned successfully", jsonTrackerList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }
        return response;
    }

    @Override
    public Map getBillingPeriodList() {
        String jsonTrackerList = "";
        Map response = new HashMap();
        try {
            List<CalculationTrackerPeriodDTO> resultMapList = calculationTrackerRepository.findAllBillingPeriod();

            jsonTrackerList = gson.toJson(resultMapList);
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "list returned successfully", jsonTrackerList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }

        return response;
    }

    private List<CalculationTrackerTile> fillMissingFields(List<CalculationTrackerTile> calculationTrackerTileList) {
        calculationTrackerTileList.forEach(calcList -> {
            calcList.setBillingDetailList(billingDetailRepository.getBillingDetails(calcList.getBillHeadId()));
            if (calcList.getErrorDesc() != null && !calcList.getErrorDesc().equalsIgnoreCase("") && !calcList.getErrorDesc().equalsIgnoreCase(" ")) {
                calcList.setError(Message.get(calcList.getErrorDesc()).name().replace("E_", ""));
            }
            BillingCredits billingCredits = billingCreditsService.findByPremiseNoAndMonthAndGardenSrc(calcList.getPremiseNo(), calcList.getPeriod(), calcList.getGardenSrc());
            Double creditValue = getCreditValue(billingCredits);
            String creditType = getCreditType(billingCredits);
            calcList.setCreditValue(creditValue);
            calcList.setCreditType(creditType);
            List<PortalAttributeValueTenantDTO> attributes = attributeOverrideService.findByPortalAttributeName(creditType);
            Optional<Long> creditTypeId = attributes.stream().map(PortalAttributeValueTenantDTO::getId).findFirst();
            calcList.setCreditTypeId(creditTypeId.isPresent() ? creditTypeId.get() : null);
        });
        return calculationTrackerTileList;
    }

    private List<CalculationTrackerGroupByTile> getCalTrackerGroupBySource(Map<String, List<CalculationTrackerTile>> calculationTrackerMap,
                                                                           List<CalculationTrackerGroupByTile> calculationTrackerGroupByTiles, String groupBy) {
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        for (String groupedBy : calculationTrackerMap.keySet()) {
            Integer total = calculationTrackerMap.get(groupedBy).size();
            Long completed = calculationTrackerMap.get(groupedBy).stream().filter(x -> x.getStatus().equalsIgnoreCase(EBillStatus.INVOICED.getStatus())).count();
            Double totalAmount = calculationTrackerMap.get(groupedBy).stream().filter(tile -> tile.getAmount() != null).mapToDouble(CalculationTrackerTile::getAmount).sum();

            Double percentage = Double.parseDouble(numberFormat.format(completed.doubleValue() / total.doubleValue())) * 100;
            switch (groupBy) {
                case "SOURCE":
                    calculationTrackerGroupByTiles.add(CalculationTrackerGroupByTile.builder().source(groupedBy).total(total)
                            .completed(completed.intValue()).percentage(percentage).totalAmount(totalAmount)
                            .calculationTrackerTileList(calculationTrackerMap.get(groupedBy)).build());
                    break;
                case "STATUS":
                    calculationTrackerGroupByTiles.add(CalculationTrackerGroupByTile.builder().status(groupedBy).total(total)
                            .completed(completed.intValue()).percentage(percentage).totalAmount(totalAmount)
                            .calculationTrackerTileList(calculationTrackerMap.get(groupedBy)).build());
                    break;
                case "CUSTOMER":
                    String image = calculationTrackerMap.get(groupedBy).get(0).getCustomerDetailDTO().getProfileUrl() != null ? calculationTrackerMap.get(groupedBy).get(0).getCustomerDetailDTO().getProfileUrl() : null;
                    calculationTrackerGroupByTiles.add(CalculationTrackerGroupByTile.builder().
                            customer(groupedBy).image(image).total(total)
                            .completed(completed.intValue()).percentage(percentage).totalAmount(totalAmount)
                            .calculationTrackerTileList(calculationTrackerMap.get(groupedBy)).build());
                    break;
            }

        }
        return calculationTrackerGroupByTiles;
    }

    @Override
    public Map getCalculationTrackerListByUserId(Map response, List<String> periodList, Long userId) {
        String jsonTrackerList = "";
        List<String> activeIds = null;
        List<CalculationTrackerTile> resultList = new ArrayList<>();
        try {
            List<SubscriptionDetailTemplate> subscriptionDetailDTOS = subscriptionRepository.findSubsByUserId(userId);
            Map<String, List<SubscriptionDetailTemplate>> subsMap = subscriptionDetailDTOS.stream().collect(Collectors.groupingBy(SubscriptionDetailTemplate::getVariantId));
            for (String variantId : subsMap.keySet()) {
                String variantName = subsMap.get(variantId).stream().map(SubscriptionDetailTemplate::getVariantName).findFirst().get();
                activeIds = subsMap.get(variantId).stream().map(SubscriptionDetailTemplate::getSubId).collect(Collectors.toList());
                List<String> billStatus = new ArrayList<>();
                billStatus.add(EBillStatus.INVOICED.getStatus());
                billStatus.add(EBillStatus.PAID.getStatus());
                billStatus.add(EBillStatus.PUBLISHED.getStatus());
                resultList.addAll(setSubscriptionNameAndLocation(billingHeadRepository.findCalculationTrackerTileBySubsIdAndStatus(activeIds, periodList, variantName, billStatus), subsMap.get(variantId)));
            }
            jsonTrackerList = gson.toJson(resultList);
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "list returned successfully", jsonTrackerList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }
        return response;

    }

    public List<CalculationTrackerTile> setSubscriptionNameAndLocation(List<CalculationTrackerTile> calculationTrackerTileList,
                                                                       List<SubscriptionDetailTemplate> subscriptionDetailTemplateList) {
        calculationTrackerTileList.forEach(calcList -> {
            subscriptionDetailTemplateList.forEach(mongoList -> {
                if (calcList.getSubsId().equalsIgnoreCase(mongoList.getSubId())) {
                    if (mongoList.getSiteLocationId() != null) {
                        PhysicalLocation location = physicalLocationService.findById(mongoList.getSiteLocationId());
                        calcList.setAddress(location.getAdd1() + " , " + location.getAdd2() + " , " + location.getAdd3() + " , " + location.getZipCode());
                    }
                    calcList.setBillingDetailList(billingDetailRepository.getBillingDetails(calcList.getBillHeadId()));
                    calcList.setSubsName(mongoList.getSubName());
                    if (calcList.getErrorDesc() != null && !calcList.getErrorDesc().equalsIgnoreCase("") && !calcList.getErrorDesc().equalsIgnoreCase(" ")) {
                        calcList.setError(Message.get(calcList.getErrorDesc()).name().replace("E_", ""));
                    }
                }
            });
        });
        return calculationTrackerTileList;
    }

    @Override
    public BaseResponse addManualCredits(String credits) {
        BillingCreditsManualMasterDTO manualMasterDTO = new BillingCreditsManualMasterDTO();
        List<BillingCredits> finalList = new ArrayList<>();
        try {
            manualMasterDTO = new ObjectMapper().readValue(credits, BillingCreditsManualMasterDTO.class);
            List<BillingCreditsManualDTO> billingCreditsManualDTOS = manualMasterDTO.getCredits();
            billingCreditsManualDTOS.stream().forEach(dto -> {
                Long creditId = null;
                String[] parts = dto.getPeriod().split("-");
                String year = parts[1];
                String month = parts[0];
                String period = year + "-" + month;
                if (dto.getPremiseNo() != null && dto.getGardenSrc() != null && dto.getCreditValue() != null && dto.getPeriod() != null
                        && dto.getSubsId() != null && !dto.getPremiseNo().trim().equals("")
                        && !dto.getGardenSrc().trim().equals("") && !dto.getPeriod().trim().equals("") && !dto.getSubsId().trim().equals("")) {
                    BillingCredits existingCredits = billingCreditsService.findByPremiseNoAndMonthAndGardenSrc(dto.getPremiseNo(), period, dto.getGardenSrc());
                    if (existingCredits != null) {
                        creditId = existingCredits.getId();
                    }
                    Double mpa = dto.getCreditType().equalsIgnoreCase(AppConstants.MPA) ? dto.getCreditValue() : 0d;
                    Double creditValue = dto.getCreditType().equalsIgnoreCase(AppConstants.UTILITY_CREDITS) ? dto.getCreditValue() : 0d;
                    finalList.add(BillingCredits.builder().id(creditId).tariffRate(0d).subscriptionCode(dto.getSubsId())
                            .mpa(mpa).jobId(null).importType(AppConstants.MANUAL).gardenId(dto.getGardenSrc())
                            .creditValue(creditValue).creditCodeVal(dto.getPremiseNo()).creditCodeType(AppConstants.CREDIT_CODE_TYPE_S).calendarMonth(period).imported(false).build());
                }

            });
            if (finalList.size() > 0) {
                billingCreditsRepository.saveAll(finalList);
            } else {
                return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("Issue in data").data(null).build();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_SAVED_SUCCESSFULLY).data(null).build();
    }

    private String getCreditType(BillingCredits billingCredits) {
        Double mpa = billingCredits != null ? (billingCredits.getMpa() != null ? billingCredits.getMpa() : null) : null;
        Double creditValue = billingCredits != null ? (billingCredits.getCreditValue() != null ? billingCredits.getCreditValue() : null) : null;

        if (creditValue == null) return (mpa == null) ? "" : AppConstants.MPA;
        if (mpa == null) return AppConstants.UTILITY_CREDITS;

        return (mpa > creditValue) ? AppConstants.MPA : AppConstants.UTILITY_CREDITS;
    }

    private Double getCreditValue(BillingCredits billingCredits) {
        Double mpa = billingCredits != null ? (billingCredits.getMpa() != null ? billingCredits.getMpa() : null) : null;
        Double creditValue = billingCredits != null ? (billingCredits.getCreditValue() != null ? billingCredits.getCreditValue() : null) : null;

        if (creditValue == null) return (mpa == null) ? 0d : mpa;
        if (mpa == null) return creditValue;

        return Math.max(mpa, creditValue);
    }
}
