package com.solar.api.tenant.service.paymentManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.permission.userLevel.EUserLevel;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.service.integration.mongo.DataExchange;


import com.solar.api.tenant.mapper.billing.calculation.CustomerDetailDTO;
import com.solar.api.tenant.mapper.billing.paymentManagement.PaymentManagementGraphDTO;
import com.solar.api.tenant.mapper.billing.paymentManagement.PaymentManagementGraphTemplate;
import com.solar.api.tenant.mapper.billing.paymentManagement.StripePaymentIntentDTO;
import com.solar.api.tenant.mapper.tiles.paymentManagement.CustomerPaymentDashboardDataTile;
import com.solar.api.tenant.mapper.tiles.paymentManagement.CustomerPaymentDashboardGroupByTile;
import com.solar.api.tenant.mapper.tiles.paymentManagement.CustomerPaymentDashboardTile;
import com.solar.api.tenant.mapper.workOrder.MongoCustomerDetailWoDTO;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionDetail;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionHead;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.process.billing.EPaymentCode;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;


import java.text.DecimalFormat;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PaymentManagementServiceImpl implements PaymentManagementService {
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private BillingHeadRepository billingHeadRepository;
    @Autowired
    private CustomerDetailRepository customerDetailRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private StripeCustomerMappingService stripeCustomerMappingService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private BillInvoiceService billInvoiceService;

    @Autowired
    private PaymentTransactionHeadRepository paymentTransactionHeadRepository;
    @Autowired
    private PaymentTransactionDetailRepository paymentTransactionDetailRepository;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private TenantConfigRepository tenantConfigRepository;
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private Gson gson = new Gson();
    @Value("${app.stripeApiPrivateKey}")
    private String stripePrivateKey;

    /**
     * Description: Api to return data for payment dashboard x
     * if tennat logs in they will see invoices for all customers (paid, invoiced, in-payment states)
     * if a customer logs in they will only see their own unpaid invoices
     * Created by: Ibtehaj
     * Created at: 2/28/2023
     *
     * @param response
     * @return
     */
    @Override
    public Map getCustomerPaymentDashboard(Map response, String groupBy, List<String> periodList) {

        List<CustomerPaymentDashboardTile> customerPaymentDashboardTileList = new ArrayList<>();
        List<CustomerPaymentDashboardTile> queryResult = new ArrayList<>();
        User currentUser = userService.getLoggedInUser();
        List<String> billingStatus = Arrays.asList(EBillStatus.PAID.getStatus(), EBillStatus.INVOICED.getStatus(), EBillStatus.IN_PAYMENT.getStatus(), EBillStatus.PUBLISHED.getStatus());
        try {
                if (currentUser.getUserType().getId() == 2) {
                    queryResult = billingHeadRepository.getCustomerPaymentDashboardTile(billingStatus, periodList);
                } else if (currentUser.getUserType().getId() == 1) {
                    queryResult = billingHeadRepository.getCustomerPaymentDashboardTile(billingStatus, periodList, currentUser.getUserName());
                }
                    customerPaymentDashboardTileList.addAll(queryResult);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }
        String jsonTrackerList = getGroupByData(groupBy, customerPaymentDashboardTileList);
        response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "list returned successfully", jsonTrackerList);

        return response;
    }

    /**
     * Description: Api to return data for new customer graph
     * Created by: Ibtehaj
     * Created at: 2/28/2023
     *
     * @return
     */
    @Override
    public Map getNewCustomerGraph() {
        Map response = new HashMap();
        User currentUser = userService.getLoggedInUser();
        List<PaymentManagementGraphDTO> result = new ArrayList<>();
        List<Integer> currentYearCount = new ArrayList<>();
        List<Integer> lastYearCount = new ArrayList<>();
        Map<String, Integer> currentYearMap = new HashMap<>();
        Map<String, Integer> lastYearMap = new HashMap<>();
        List<PaymentManagementGraphDTO> data = new ArrayList<>();
        try {
            if (currentUser.getUserType().getId() == 2) {
                data = toPaymentManagementGraphDTOList(customerDetailRepository.getNewCustomerGraph(true, true));
            } else if (currentUser.getUserType().getId() == 1) {
                data = getNewCustomerSubscriptionsGraph();
            }
            List<String> uniqueMonths = data.stream().map(x -> x.getMonth()).distinct().collect(Collectors.toList());
            Integer currentYear = Year.now().getValue();
            Integer lastYear = currentYear - 1;

            List<PaymentManagementGraphDTO> currentYearList = data.stream().filter(x -> x.getYear().equals(currentYear)).collect(Collectors.toList());
            List<PaymentManagementGraphDTO> lastYearList = data.stream().filter(x -> x.getYear().equals(lastYear)).collect(Collectors.toList());
            currentYearMap = fillChartMap(uniqueMonths, currentYearList, currentYearMap);
            lastYearMap = fillChartMap(uniqueMonths, lastYearList, lastYearMap);

            Collection<Integer> currentYearValues = currentYearMap.values();
            Collection<Integer> lastYearValues = lastYearMap.values();
            currentYearCount = new ArrayList<>(currentYearValues);
            lastYearCount = new ArrayList<>(lastYearValues);

            result.add(PaymentManagementGraphDTO.builder().month(String.join(",", lastYearMap.keySet())).build());
            result.add(PaymentManagementGraphDTO.builder().year(currentYear).ListCount(currentYearCount).build());
            result.add(PaymentManagementGraphDTO.builder().year(lastYear).ListCount(lastYearCount).build());
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Data found successfully", gson.toJson(result));

        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data not found", null);
        }
        return response;
    }

    /**
     * Description: Api to return data for customer type graph
     * Created by: Ibtehaj
     * Created at: 2/28/2023
     *
     * @return
     */
    @Override
    public Map getCustomerTypeGraph() {
        Map response = new HashMap();
        try {
            List<PaymentManagementGraphDTO> data = customerDetailRepository.getCustomerTypeGraph(true, true);
            List<String> labels = data.stream().map(PaymentManagementGraphDTO::getLabel).collect(Collectors.toList());
            List<Long> count = data.stream().map(PaymentManagementGraphDTO::getCount).collect(Collectors.toList());
            List<Integer> countInt = count.stream().map(Long::intValue).collect(Collectors.toList());
            PaymentManagementGraphDTO result = PaymentManagementGraphDTO.builder().label(String.join(",", labels)).ListCount(countInt).build();
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Data found successfully", gson.toJson(result));
        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data not found", null);
        }
        return response;
    }

    /**
     * Description: Api to return data for customer project graph
     * Created by: Ibtehaj
     * Created at: 03/03/2023
     *
     * @return
     */
    @Override
    public Map getCustomersByProjectGraph() {
        Map response = new HashMap();
        List<PaymentManagementGraphDTO> resultList = new ArrayList<>();
        User currentUser = userService.getLoggedInUser();
        Map<String, List<MongoCustomerDetailWoDTO>> subscriptionData = new HashMap<>();
        try {
            if (currentUser.getUserType().getId() == 2) {
                subscriptionData = (Map<String, List<MongoCustomerDetailWoDTO>>) dataExchange.getAllCustomerListSubscriptionGroupByVariant().get("data");
                resultList = createPaymentManagementGraphDTOList(subscriptionData);
            } else if (currentUser.getUserType().getId() == 1) {
                subscriptionData = (Map<String, List<MongoCustomerDetailWoDTO>>) dataExchange.getAllCustomerListSubscriptionGroupByAccountId().get("data");
                Map<String, List<MongoCustomerDetailWoDTO>> subscriptionDataMap = subscriptionData.get(String.valueOf(currentUser.getAcctId())).stream().collect(
                        Collectors.groupingBy(MongoCustomerDetailWoDTO::getSubAlias));
                resultList = createPaymentManagementGraphDTOList(subscriptionDataMap);
            }

            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Data found successfully", gson.toJson(resultList));
        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data not found", null);
        }

        return response;
    }

    /**
     * Description: Api to generate payment intent and return client secret in response
     * Created by: Ibtehaj
     * Created at: 03/03/2023
     *
     * @return
     */
    @Override
    public Map generatePaymentIntent(StripePaymentIntentDTO stripePaymentIntentDTO, Long compKey) {
        Map response = new HashMap();
        try {
            Optional<TenantConfig> stripeKey = tenantConfigService.findByParameter(AppConstants.STRIPE_API_KEY);
            Stripe.apiKey = String.valueOf(decodeBase64String(stripeKey.get().getText()));
        } catch (Exception e) {
            LOGGER.error("Error while fetching stripe key" , e.getMessage());
        }
        String customerId = null;
        stripePaymentIntentDTO.setTenantId(compKey);
        try {
            Optional<TenantConfig> tenantConfig = tenantConfigService.findByParameter(AppConstants.STRIPE_BANK_ACCOUNT);
            if (tenantConfig.isPresent() && tenantConfig.get().getText().equalsIgnoreCase("Yes")) {

                if (stripePaymentIntentDTO.getCustomerId() != null) {
                    customerId = stripePaymentIntentDTO.getCustomerId();
                }
                PaymentIntentCreateParams params = paymentIntentCreateParams(stripePaymentIntentDTO, customerId);
                PaymentIntent paymentIntent = PaymentIntent.create(params);
                response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Successfully Created Secret Key", paymentIntent.getClientSecret());
            } else {
                response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), AppConstants.STRIPE_BANK_ACCOUNT + " Can Not Be No/Null ", null);
                return response;
            }
        } catch (Exception e) {
            LOGGER.error("Error while creating payment intent" + e.getMessage());
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), AppConstants.STRIPE_BANK_ACCOUNT + " Can Not Be Null ", null);
            return response;
        }
        return response;
    }

    /**
     * Description: Api to save transaction information in dataBase tables
     * Created by: Ibtehaj
     * Created at: 03/06/2023
     *
     * @return
     */
    @Override
    public Map paymentResponse(String paymentIntentId, Long compKey) {
        Map response = new HashMap();
        Long acctId;
        try {
            Optional<TenantConfig> stripeKey = tenantConfigService.findByParameter(AppConstants.STRIPE_API_KEY);
            Stripe.apiKey = String.valueOf(decodeBase64String(stripeKey.get().getText()));
        } catch (Exception e) {
            LOGGER.error("Error while fetching stripe key" , e.getMessage());
        }
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            StripePaymentIntentDTO stripePaymentIntentDTO =
                    new ObjectMapper().readValue(paymentIntent.getMetadata().get(AppConstants.STRIPE_PAYMENT_INTENT_DTO),
                            StripePaymentIntentDTO.class);
            acctId = createStripeCustomer(stripePaymentIntentDTO.getCustomerEmail(), stripePaymentIntentDTO);//Creating stripe customer and saving in database
            String type =  paymentIntent.getCharges().getData().get(0).getPaymentMethodDetails().getType();
            String sentenceCaseType = Utility.toSentenceCase(type);
            for (String ids : stripePaymentIntentDTO.getBillHeadId_InvoiceId()) {
                String[] parts = ids.split("-");
                Long billHeadId = Long.parseLong(parts[0]);
                Long invoiceId = Long.parseLong(parts[1]);
                createPaymentTransactionHead(billHeadId, invoiceId, acctId, paymentIntentId, sentenceCaseType);
            }
            response = Utility.generateResponseMap(response, HttpStatus.CREATED.toString(), "Payment Success", null);
        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.SERVICE_UNAVAILABLE.toString(), "Error Processing Payment", null);
        }
        return response;
    }

    private List<PaymentManagementGraphDTO> getNewCustomerSubscriptionsGraph() {
        List<PaymentManagementGraphDTO> result = new ArrayList<>();
        Map<YearMonth, List<MongoCustomerDetailWoDTO>> subscriptionData = (Map<YearMonth, List<MongoCustomerDetailWoDTO>>) dataExchange.getAllCustomerListSubscriptionGroupByCreatedAt().get("data");
        Map<YearMonth, List<MongoCustomerDetailWoDTO>> filteredSubscriptionData = subscriptionData.entrySet()
                .stream()
                .filter(map -> (map.getKey().getYear() == Year.now().getValue()) || (map.getKey().getYear() == Year.now().getValue() - 1))
                .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
        Map<YearMonth, List<MongoCustomerDetailWoDTO>> treeMap = new TreeMap<>(filteredSubscriptionData);
        for (YearMonth ym : treeMap.keySet()) {
            result.add(PaymentManagementGraphDTO.builder().year(ym.getYear()).month(ym.getMonth().toString()).count(Long.valueOf(treeMap.get(ym).size())).build());
        }
        return result;
    }

    private List<PaymentManagementGraphDTO> createPaymentManagementGraphDTOList(Map<String, List<MongoCustomerDetailWoDTO>> data) {
        List<PaymentManagementGraphDTO> resultList = new ArrayList<>();
        for (String variantName : data.keySet()) {
            resultList.add(PaymentManagementGraphDTO.builder()
                    .label(variantName).
                    count(Long.valueOf(data.get(variantName).size())).
                    build());
        }
        return resultList;
    }

    private List<PaymentManagementGraphDTO> toPaymentManagementGraphDTOList(List<PaymentManagementGraphTemplate> paymentManagementGraphTemplate) {
        List<PaymentManagementGraphDTO> paymentManagementGraphDTOList = paymentManagementGraphTemplate.stream().map(payManageGraphTemplate -> toPaymentManagementGraphDTO(payManageGraphTemplate)).collect(Collectors.toList());
        return paymentManagementGraphDTOList;
    }

    private PaymentManagementGraphDTO toPaymentManagementGraphDTO(PaymentManagementGraphTemplate paymentManagementGraphTemplate) {
        return PaymentManagementGraphDTO.builder().year(paymentManagementGraphTemplate.getYear())
                .month(paymentManagementGraphTemplate.getMonth())
                .count(Long.valueOf(paymentManagementGraphTemplate.getCount()))
                .build();
    }

    private PaymentIntentCreateParams paymentIntentCreateParams(StripePaymentIntentDTO stripePaymentIntentDTO, String customerId) {
        return new PaymentIntentCreateParams.Builder()
                .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
                .setCustomer(customerId)
                .setCurrency("usd")
                .putMetadata(AppConstants.STRIPE_PAYMENT_INTENT_DTO, gson.toJson(stripePaymentIntentDTO))
                .setAmount(stripePaymentIntentDTO.getAmount() * 100L) //createPayment... dollar amount * cents ... 15 * 100 cents = 15$
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
    }

    private Long createStripeCustomer(String customerEmail, StripePaymentIntentDTO stripePaymentIntentDTO) {
        Stripe.apiKey = stripePrivateKey;
        Map<String, Object> params = new HashMap<>();
        Entity entity = entityService.findByEmailAddressAndEntityTypeAndIsDeleted(customerEmail, EUserLevel.CUSTOMER.getName(), false);
        UserLevelPrivilege userLevelPrivilege = entity.getUserLevelPrivileges().get(0);
        if (stripePaymentIntentDTO.getCustomerId() == null) {
            params.put("email", customerEmail);
            params.put("name", entity.getEntityName());
            try {
                Customer customer = Customer.create(params);
                stripeCustomerMappingService.create(entity, userLevelPrivilege, customer.getId());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return userLevelPrivilege.getUser().getAcctId();
    }

    private void createPaymentTransactionHead(Long billHeadId, Long invoiceId, Long acctId, String referenceId, String paymentMethod) {
        BillingInvoice billingInvoice = billInvoiceService.findById(invoiceId);
        BillingHead billHead = billingHeadService.findById(billHeadId);
        PaymentTransactionHead paymentTransactionHead = paymentTransactionHeadRepository.save(PaymentTransactionHead.builder()
                .paymentCode(EPaymentCode.BILLING.getCode())
                .invoice(billingInvoice)
                .custAccountId(acctId)
                .net(billHead.getAmount()).build());
        createPaymentTransactionDetail(paymentTransactionHead, billHead.getAmount(), referenceId, paymentMethod); //Creating entry in payment transaction detail
        updateBillHeadStatus(billHead); //Updating status to paid in BillHead
    }

    private void createPaymentTransactionDetail(PaymentTransactionHead paymentTransactionHead, Double amount, String referenceId, String paymentMethod) {
        PaymentTransactionDetail paymentTransactionDetail = paymentTransactionDetailRepository.save(PaymentTransactionDetail.builder()
                .paymentTransactionHead(paymentTransactionHead)
                .amt(amount)
                .tranDate(new Date())
                .referenceId(referenceId)
                .status(EBillStatus.PAID.getStatus())
                .issuer(paymentMethod)
                .lineSeqNo(1l).build());

    }

    private void updateBillHeadStatus(BillingHead billHead) {
        billHead.setBillStatus(EBillStatus.PAID.getStatus());
        billingHeadService.save(billHead);
    }

    private List<CustomerPaymentDashboardTile> setSubscriptionName(List<CustomerPaymentDashboardTile> customerPaymentDashboardTile, List<MongoCustomerDetailWoDTO> mongoCustomerDetailWoDTOS, String variantName) {
        mongoCustomerDetailWoDTOS.forEach(mongoList -> {
            customerPaymentDashboardTile.forEach(tileList -> {
                if (mongoList.getSubId().equalsIgnoreCase(tileList.getSubscriptionId())) {
                    tileList.setSubscriptionName(mongoList.getSubValueCN());
                    tileList.setSource(variantName);
                }
            });
        });
        return customerPaymentDashboardTile;
    }

    private Map fillChartMap(List<String> uniqueMonths, List<PaymentManagementGraphDTO> dataList, Map<String, Integer> dataMap) {
        uniqueMonths.forEach(month -> {
            dataList.forEach(chartData -> {
                if (month.equalsIgnoreCase(chartData.getMonth())) {
                    dataMap.put(chartData.getMonth(), chartData.getCount().intValue());
                } else {
                    if (!dataMap.containsKey(month)) {
                        dataMap.put(month, 0);
                    }
                }
            });
        });
        return dataMap;
    }

    private String getGroupByData(String groupBy, List<CustomerPaymentDashboardTile> customerPaymentDashboardTileList) {
        String jsonTrackerList = null;
        List<CustomerPaymentDashboardGroupByTile> resultGroupByList = new ArrayList<>();
        Map<String, List<CustomerPaymentDashboardTile>> paymentDashboardGroupBy = null;
        switch (groupBy) {
            case "NONE":
                jsonTrackerList = gson.toJson(customerPaymentDashboardTileList);
                break;
            case "SOURCE":
                paymentDashboardGroupBy = customerPaymentDashboardTileList.stream().collect(Collectors.groupingBy(CustomerPaymentDashboardTile::getSource));
                jsonTrackerList = gson.toJson(getCalTrackerGroupBySource(paymentDashboardGroupBy, resultGroupByList, groupBy));
                break;
            case "CUSTOMER":
                paymentDashboardGroupBy = customerPaymentDashboardTileList.stream().filter(e-> (e.getCustomerDetailDTO() != null && e.getCustomerDetailDTO().getCustomerName() != null)).collect(Collectors.groupingBy(grouping -> grouping.getCustomerDetailDTO().getCustomerName()));
                jsonTrackerList = gson.toJson(getCalTrackerGroupBySource(paymentDashboardGroupBy, resultGroupByList, groupBy));
                break;
            case "SUBSCRIPTION":
                paymentDashboardGroupBy = customerPaymentDashboardTileList.stream().filter(e -> e.getSubscriptionName() != null).collect(Collectors.groupingBy(CustomerPaymentDashboardTile::getSubscriptionName));
                jsonTrackerList = gson.toJson(getCalTrackerGroupBySource(paymentDashboardGroupBy, resultGroupByList, groupBy));
                break;
            case "PAYMENT TYPE":
            case "PAYMENTTYPE":
                paymentDashboardGroupBy = customerPaymentDashboardTileList.stream().filter(e-> e.getPaymentType() != null).collect(Collectors.groupingBy(CustomerPaymentDashboardTile::getPaymentType));
                jsonTrackerList = gson.toJson(getCalTrackerGroupBySource(paymentDashboardGroupBy, resultGroupByList, groupBy));
                break;
        }
        return jsonTrackerList;
    }

    private List<CustomerPaymentDashboardGroupByTile> getCalTrackerGroupBySource(Map<String, List<CustomerPaymentDashboardTile>> paymentDashboardTrackerMap,
                                                                                 List<CustomerPaymentDashboardGroupByTile> paymentDashboardGroupByTiles, String groupBy) {
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        for (String groupedBy : paymentDashboardTrackerMap.keySet()) {
            Double totalInvoicedAmount = paymentDashboardTrackerMap.get(groupedBy).stream().filter(tile -> tile.getInvoicedAmount() != null).mapToDouble(CustomerPaymentDashboardTile::getInvoicedAmount).sum();
            Double totalPaidAmount = paymentDashboardTrackerMap.get(groupedBy).stream().filter(tile -> tile.getPaidAmount() != null).mapToDouble(CustomerPaymentDashboardTile::getPaidAmount).sum();
            Double totalRemainingAmount = paymentDashboardTrackerMap.get(groupedBy).stream().filter(tile -> tile.getRemainingAmount() != null).mapToDouble(CustomerPaymentDashboardTile::getRemainingAmount).sum();

            switch (groupBy) {
                case "SOURCE":
                    paymentDashboardGroupByTiles.add(CustomerPaymentDashboardGroupByTile.builder()
                            .data(CustomerPaymentDashboardTile.builder().source(groupedBy)
                                    .invoicedAmount(Double.parseDouble(numberFormat.format(totalInvoicedAmount)))
                                    .paidAmount(Double.parseDouble(numberFormat.format(totalPaidAmount)))
                                    .remainingAmount(Double.parseDouble(numberFormat.format(totalRemainingAmount)))
                                    .billStatus("-").build())
                            .children(tileToDataMapper(paymentDashboardTrackerMap.get(groupedBy))).build());
                    break;
                case "SUBSCRIPTION":
                    paymentDashboardGroupByTiles.add(CustomerPaymentDashboardGroupByTile.builder()
                            .data(CustomerPaymentDashboardTile.builder().subscriptionName(groupedBy)
                                    .invoicedAmount(Double.parseDouble(numberFormat.format(totalInvoicedAmount)))
                                    .paidAmount(Double.parseDouble(numberFormat.format(totalPaidAmount)))
                                    .remainingAmount(Double.parseDouble(numberFormat.format(totalRemainingAmount)))
                                    .billStatus("-").build())
                            .children(tileToDataMapper(paymentDashboardTrackerMap.get(groupedBy))).build());
                    break;
                case "CUSTOMER":
                    String image = paymentDashboardTrackerMap.get(groupedBy).get(0).getCustomerDetailDTO().getProfileUrl() != null ? paymentDashboardTrackerMap.get(groupedBy).get(0).getCustomerDetailDTO().getProfileUrl() : null;
                    paymentDashboardGroupByTiles.add(CustomerPaymentDashboardGroupByTile.builder()
                            .data(CustomerPaymentDashboardTile.builder().customerDetailDTO(CustomerDetailDTO.builder()
                                            .customerName(groupedBy).profileUrl(image).build())
                                    .invoicedAmount(Double.parseDouble(numberFormat.format(totalInvoicedAmount)))
                                    .paidAmount(Double.parseDouble(numberFormat.format(totalPaidAmount)))
                                    .remainingAmount(Double.parseDouble(numberFormat.format(totalRemainingAmount)))
                                    .billStatus("-").build())
                            .children(tileToDataMapper(paymentDashboardTrackerMap.get(groupedBy))).build());
                    break;
                case "PAYMENT TYPE":
                case "PAYMENTTYPE":
                    paymentDashboardGroupByTiles.add(CustomerPaymentDashboardGroupByTile.builder()
                            .data(CustomerPaymentDashboardTile.builder().paymentType(groupedBy)
                                    .invoicedAmount(Double.parseDouble(numberFormat.format(totalInvoicedAmount)))
                                    .paidAmount(Double.parseDouble(numberFormat.format(totalPaidAmount)))
                                    .remainingAmount(Double.parseDouble(numberFormat.format(totalRemainingAmount)))
                                    .billStatus("-").build())
                            .children(tileToDataMapper(paymentDashboardTrackerMap.get(groupedBy))).build());
                    break;
            }

        }
        return paymentDashboardGroupByTiles;
    }

    private List<CustomerPaymentDashboardDataTile> tileToDataMapper(List<CustomerPaymentDashboardTile> customerPaymentDashboardTileList) {
        List<CustomerPaymentDashboardDataTile> customerPaymentDashboardDataTileList = new ArrayList<>();
        customerPaymentDashboardTileList.stream().forEach(tile -> {
            customerPaymentDashboardDataTileList.add(CustomerPaymentDashboardDataTile.builder().data(tile).build());
        });
        return customerPaymentDashboardDataTileList;
    }

    @Override
    public String decodeBase64String(String encodedString) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
            String decodedString = new String(decodedBytes);
            return decodedString;
        } catch (Exception e) {
            return "Invalid input : " + e.getMessage();
        }
    }

    @Override
    public BaseResponse encodeStripeKey(String stripeKey) {
        byte[] encodedKey = Base64.getEncoder().encode(stripeKey.getBytes());
        try {
            Optional<TenantConfig> stripe = tenantConfigService.findByParameter(AppConstants.STRIPE_API_KEY);
            if (stripe.isPresent()) {
                TenantConfig tenantConfig = stripe.get();
                tenantConfig.setText(new String(encodedKey));
                tenantConfigRepository.save(tenantConfig);
            }else {
                TenantConfig tenantConfig = new TenantConfig();
                tenantConfig.setCategory(AppConstants.Payment.CATEGORY);
                tenantConfig.setDescription(AppConstants.Payment.DESCRIRPTION);
                tenantConfig.setFormat(AppConstants.Payment.FORMAT);
                tenantConfig.setVarType(AppConstants.Payment.VARTYPE);
                tenantConfig.setText(new String(encodedKey));
                tenantConfig.setParameter(AppConstants.STRIPE_API_KEY);
                tenantConfigRepository.save(tenantConfig);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.UNPROCESSABLE_ENTITY.value()).message("Error while saving or updating stripe key.").data(new String(encodedKey)).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message("Saved successfully").data(new String(encodedKey)).build();
    }
}
