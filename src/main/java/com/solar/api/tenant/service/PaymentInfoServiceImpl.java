package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Message;
import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoMapper;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoWrapper;
import com.solar.api.tenant.mapper.payment.info.PaymentModeDTO;
import com.solar.api.tenant.mapper.tiles.dataexport.customerdetail.DataExportCustomerPaginationTile;
import com.solar.api.tenant.mapper.tiles.dataexport.customerdetail.DataExportCustomerTile;
import com.solar.api.tenant.mapper.tiles.dataexport.payment.DataExportPaymentPaginationTile;
import com.solar.api.tenant.mapper.tiles.dataexport.payment.DataExportPaymentTile;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.dataexport.payment.PaymentDataDTO;
import com.solar.api.tenant.model.dataexport.payment.PaymentExportData;
import com.solar.api.tenant.model.dataexport.powermonitoring.ExportDTO;
import com.solar.api.tenant.model.payment.billing.PaymentDetailsView;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.paymentDetailView.SearchParams;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.repository.PaymentDetailsViewRepository;
import com.solar.api.tenant.repository.PaymentInfoRepository;
import com.solar.api.tenant.repository.PaymentModeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
//@Transactional("tenantTransactionManager")
public class PaymentInfoServiceImpl implements PaymentInfoService {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private PaymentInfoRepository paymentInfoRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentDetailsViewRepository paymentDetailsViewRepository;
    @Autowired
    private PaymentModeRepository paymentModeRepository;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private CustomerDetailService customerDetailService;
    @Autowired
    private CalculationDetailsService calculationDetailsService;
    @Autowired
    private BillingHeadRepository billingHeadRepository;

    @Override
    public PaymentInfo addOrUpdate(PaymentInfo paymentInfo) {
//        PaymentInfo paymentInfoDB = findByAcctNoAndRoutingNoAndAcctType(paymentInfo.getAccountNumber(), paymentInfo.getRoutingNumber(), paymentInfo.getAccountType());
//        if (paymentInfo.getId() != null) {
//            if (paymentInfoDB != null && paymentInfoDB.getId() != paymentInfo.getId()) {
//                throw new ResponseStatusException(
//                        HttpStatus.CONFLICT, "Account detail already exists", null);
//            }
//            PaymentInfo paymentInfoData = findById(paymentInfo.getId());
//            paymentInfoData = PaymentInfoMapper.toUpdatedPaymentInfo(paymentInfoData, paymentInfo);
//            if (paymentInfo.getAcctId() != null) {
//                User account = userService.findById(paymentInfo.getAcctId());
//                paymentInfo.setPortalAccount(account);
//                paymentInfo.setIsPrimary(paymentInfo.getIsPrimary());
//            }
//            return paymentInfoRepository.save(paymentInfoData);
//        }
//        if (paymentInfoDB != null && paymentInfo.getId() == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT, "Account detail already exists", null);
//        }

        try {
            if (paymentInfo.getId() != null) {
                PaymentInfo paymentInfoData = findById(paymentInfo.getId());
                paymentInfoData = PaymentInfoMapper.toUpdatedPaymentInfo(paymentInfoData, paymentInfo);
                if (paymentInfo.getAcctId() != null) {
                    User account = userService.findById(paymentInfo.getAcctId());
                    paymentInfo.setPortalAccount(account);
                    paymentInfo.setIsPrimary(paymentInfo.getIsPrimary());
                }
                return paymentInfoRepository.save(paymentInfoData);
            }
            User account = userService.findById(paymentInfo.getAcctId());
            paymentInfo.setPortalAccount(account);
            paymentInfo.setIsPrimary(false);
            return paymentInfoRepository.save(paymentInfo);
        }catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Processing Error", null);
        }
    }

    @Override
    public PaymentInfo findById(Long id) {
        return paymentInfoRepository.findById(id).orElseThrow(() -> new NotFoundException(PaymentInfo.class, id));
    }

    @Override
    public PaymentInfo findByIdNoThrow(Long id) {
        return paymentInfoRepository.findById(id).orElse(null);
    }

    @Override
    public List<PaymentInfo> findByUserId(Long userId) {
        User user = userService.findById(userId);
        return paymentInfoRepository.findPaymentInfoByPortalAccount(user);
    }

    @Override
    public PaymentInfo findByPortalAccountAndPaymentSrcAlias(User portalAccount, String alias) {
        return paymentInfoRepository.findByPortalAccountAndPaymentSrcAlias(portalAccount, alias);
    }

    @Override
    public PaymentInfo findByPortalAccountAndPaymentSource(Long userId, String paymentSource) {
        User portalAccount = userService.findById(userId);
        return paymentInfoRepository.findByPortalAccountAndPaymentSource(portalAccount, paymentSource);
    }

    @Override
    public String getMaskedReferenceId(Long userId, String paymentSource) {
        PaymentInfo paymentInfo = findByPortalAccountAndPaymentSource(userId, paymentSource);
        String acctNumber = paymentInfo.getAccountNumber();
        return Utility.getMaskedString(acctNumber);
    }

    @Override
    public PaymentInfoWrapper getPaymentInfoByGardenId(String gardenId,
                                                       String month,
                                                       String paymentSource,
                                                       String billStatus) {
        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(gardenId);
        if (m.find()) {
            gardenId = m.group(1);
        }
        return paymentInfoRepository.getPaymentInfoByGardenId(gardenId, month, paymentSource, billStatus);
    }

    @Override
    public List<PaymentInfo> findAll() {
        return paymentInfoRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        paymentInfoRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        paymentInfoRepository.deleteAll();
    }

    @Override
    public List<PaymentDetailsView> comprehensiveSearch(SearchParams searchParams) {
        Long firstParam = searchParams.getAttValue();
        String secondParams = searchParams.getAttDependentValue();
        String thirdParam = searchParams.getMonth();
        String fourthParam = searchParams.getBillingStatus();
        String fifthParam = searchParams.getSource() == null ? "" : searchParams.getSource();
//        projectPartner.setProjectId(projectPartnerUpdate.getProjectId() == null ? projectPartner.getProjectId() :
//        projectPartnerUpdate.getProjectId());

        if (firstParam == 1) {
            return paymentDetailsViewRepository.getByAccount(
                    Arrays.stream(secondParams.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(thirdParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fourthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fifthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()));
        } else if (firstParam == 2) {
            return paymentDetailsViewRepository.getBySubscriptionType(
                    Arrays.stream(secondParams.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(thirdParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fourthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fifthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()));
        } else if (firstParam == 3) {
            return paymentDetailsViewRepository.getBySubscriptionId(
                    Arrays.stream(secondParams.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(thirdParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fourthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fifthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()));
        } else if (firstParam == 4) {
            return paymentDetailsViewRepository.getByGardenSRC(Arrays.stream(secondParams.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(thirdParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fourthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fifthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()));
        } else if (firstParam == 5) {
            return paymentDetailsViewRepository.getByPremiseNumber(Arrays.stream(secondParams.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(thirdParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fourthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fifthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()));
        } else if (firstParam == 6) {
            return paymentDetailsViewRepository.getByInvoiceId(
                    Arrays.stream(secondParams.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(thirdParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fourthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()),
                    Arrays.stream(fifthParam.split(",")).map(id -> id.trim().toString()).collect(Collectors.toList()));
        }
        return null;
    }

    @Override
    public List<PaymentDetailsView> getAllPaymentDetailsView() {
        return paymentDetailsViewRepository.getAll();
    }

    @Override
    public List<String> getAllPaymentModes() {
        List<PaymentModeDTO> paymentModeDTOS = paymentModeRepository.listPaymentMode();
        List<String> paymentModes = paymentModeDTOS.stream().map(PaymentModeDTO::getPaymentMode).collect(Collectors.toList());
        return paymentModes;
    }

    @Override
    public List<String> getAllPaymentStatus() {
        List<String> paymentStatus = new ArrayList<>();
        paymentStatus.add(Constants.PAYMENT_STATUS.inprogress);
        paymentStatus.add(Constants.PAYMENT_STATUS.completed);
        paymentStatus.add(Constants.PAYMENT_STATUS.paid);
        paymentStatus.add(Constants.PAYMENT_STATUS.failed);
        return paymentStatus;

    }

    @Override
    public PaymentInfo findByAcctNoAndRoutingNoAndAcctType(String acctNo, String routingNo, String accountType) {
        return paymentInfoRepository.findByAccountNumberAndRoutingNumberAndAccountType(acctNo, routingNo, accountType);
    }

    @Override
    public BaseResponse loadFilterEmployeeData(String exportDTO) {
        PaymentExportData paymentExportData = new PaymentExportData();
        try {
            if (exportDTO != null) {
                paymentExportData = new ObjectMapper().readValue(exportDTO, PaymentExportData.class);
                if (paymentExportData.getCustomerType() != null && paymentExportData.getCustomers() == null && !paymentExportData.getCustomerType().isEmpty()) {
                    paymentExportData.setCustomers(customerDetailService.getCustomerByCustomerType(paymentExportData.getCustomerType()));
                } else {
                    List<Long> accountId = paymentExportData.getCustomers().stream().map(PaymentDataDTO::getAccntId).distinct().collect(Collectors.toList());
                    List<BillingHead> billingHeadList = billingHeadService.getBillingInfo(accountId);
                    List<String> status = billingHeadList.stream().map(BillingHead::getBillStatus).distinct().collect(Collectors.toList());
                    List<Long> billId = billingHeadList.stream().map(BillingHead::getId).distinct().collect(Collectors.toList());
                    List<PaymentDataDTO> period = billingHeadList.stream().map(BillingHead::getBillingMonthYear).distinct().map(PaymentDataDTO::new).collect(Collectors.toList());
                    paymentExportData.setPeriod(period);
                    paymentExportData.setStatus(status);
                    paymentExportData.setBillId(billId);
                    List<PaymentDataDTO> sourceAndError = calculationDetailsService.findSourceAndError(accountId);
                    List<String> errors =  sourceAndError.stream()
                            .filter(paymentData -> paymentData.getError() != null && !paymentData.getError().trim().isEmpty())
                            .map(paymentData -> {
                                String errorCode = paymentData.getError();
                                String errorMessage = Message.get(errorCode).name().replace("E_", "");
                                return errorCode + " - " + errorMessage;
                            })
                            .distinct()
                            .collect(Collectors.toList());
                    errors.add(0, "NA");
                    List<PaymentDataDTO> source = new ArrayList<>(sourceAndError.stream()
                            .collect(Collectors.toMap(
                                    PaymentDataDTO::getSourceId,
                                    xItem -> {        // create a new x item with the text field set to null
                                        PaymentDataDTO newItem = new PaymentDataDTO();
                                        newItem.setSourceId(xItem.getSourceId());
                                        newItem.setSource(xItem.getSource());
                                        newItem.setError(null);
                                        return newItem;
                                    },
                                    (existing, replacement) -> existing))
                            .values());
                    paymentExportData.setSource(source);
                    paymentExportData.setErrors(errors);
                }
            } else {
                paymentExportData.setCustomerType(paymentInfoRepository.getCustomerType());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(paymentExportData).build();
    }

    @Override
    public BaseResponse getBillingPaymentExportData(String custType, String custId, String period, String billId, String status, String source, String error,Integer pageNumber, Integer pageSize) {
        DataExportPaymentPaginationTile result = new DataExportPaymentPaginationTile();
        try {
            Page<DataExportPaymentTile> page = null;
            List<String> customerTypeList = stringToList(custType);
            List<Long> customerIdList = stringToLongList(custId);
            List<String> periodList = stringToList(period);
            List<Long> billIdList = stringToLongList(billId);
            List<String> statusList = stringToList(status);
            List<String> sourceList = stringToList(source);
            List<String> errorList = stringToErrorList(error);
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            page = billingHeadRepository.getPaymentExportTile(customerTypeList, customerIdList, periodList, billIdList, statusList,sourceList,errorList,pageRequest);
            result.setDataExportPaymentTileList(page.getContent());
            result.setTotalPages(page.getTotalPages());
            result.setTotalElements(page.getTotalElements());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }


    private static List<String> stringToList(String str) {
        return Arrays.stream(str.split(","))
                .filter(s -> s != null && !s.isEmpty() && !s.equalsIgnoreCase("null"))
                .collect(Collectors.toList());
    }

    private static List<Long> stringToLongList(String str) {
        return Arrays.stream(str.split(","))
                .filter(s -> s != null && !s.isEmpty() && !s.equalsIgnoreCase("null"))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    private static List<String> stringToErrorList(String str) {
        return Arrays.stream(str.split(","))
                .flatMap(s -> {
                    if (s != null && s.equalsIgnoreCase("NA")) {
                        return Stream.of(null, "", " ");
                    } else if (s != null && s.contains("-")) {
                        return Stream.of(s.split("-")[0].trim());
                    } else {
                        return Stream.of(s);
                    }
                })
//                .filter(s -> s != null && !s.isEmpty() && !s.equalsIgnoreCase("null"))
                .collect(Collectors.toList());
    }
}
