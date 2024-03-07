package com.solar.api.saas.service.process.migration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.saas.service.process.migration.parser.MigrationParser;
import com.solar.api.saas.service.process.migration.parser.vista.mapper.PaymentMapper;
import com.solar.api.saas.service.process.upload.UploadResponse;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionDetail;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionHead;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.ESubscriptionStatus;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.EUserStatus;
import com.solar.api.tenant.model.user.Stage;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.ERole;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.repository.PaymentTransactionDetailRepository;
import com.solar.api.tenant.repository.PaymentTransactionHeadRepository;
import com.solar.api.tenant.repository.StageRepository;
import com.solar.api.tenant.repository.SubscriptionRateMatrixHeadRepository;
import com.solar.api.tenant.service.*;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
//@Transactional("masterTransactionManager")
public class MigrationServiceImpl implements MigrationService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static String DEFAULT_PASSWORD = "novel1234+";
    @Autowired
    private MigrationParserFactory parserFactory;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private BillInvoiceService billInvoiceService;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private SubscriptionRateMatrixHeadRepository headRepository;
    @Autowired
    private StageRepository stageRepository;
    @Autowired
    private PaymentTransactionHeadRepository transactionHeadRepository;
    @Autowired
    private PaymentTransactionDetailRepository transactionDetailRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private Utility utility;

    @Override
    public UploadResponse migrate(EMigrationParserLocation parser, String entity, MultipartFile file,
                                  JobManagerTenant jobManagerTenant, Boolean isLegacy) {
        if (file == null) {
            return null;
        }
        try {
            Class clazz = Class.forName(EMigrationEntitiy.get(entity).getEntityPath());
            try (FileInputStream in = (FileInputStream) file.getInputStream()) {
                if (clazz == User.class) {
                    return importUsersFromCSV(parser, in, null);
                } else if (clazz == Address.class) {
                    return importAddressesFromCSV(parser, in, null);
                } else if (clazz == PaymentInfo.class) {
                    return importPaymentInfosFromCSV(parser, in, null);
                } else if (clazz == CustomerSubscription.class) {
                    return importSubscriptionMappingsFromCSV(parser, in, null, isLegacy);
                } else if (clazz == PaymentTransactionDetail.class) {
                    return importPaymentsFromCSV(parser, in, jobManagerTenant);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    @Async
    @Transactional
    public UploadResponse migrate(EMigrationParserLocation parser, String entity, File file,
                                  ObjectNode requestMessage, Boolean isLegacy) {
        if (file == null) {
            return null;
        }
        JobManagerTenant jobManagerTenant = null;
        try {
            Class clazz = Class.forName(EMigrationEntitiy.get(entity).getEntityPath());
            try (FileInputStream in = new FileInputStream(file)) {
                if (clazz == User.class) {
                    jobManagerTenant = jobManagerTenantService.add(EJobName.FILE_IMPORT_USERS.toString(),
                            requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
                    return importUsersFromCSV(parser, in, jobManagerTenant);
                } else if (clazz == Address.class) {
                    jobManagerTenant = jobManagerTenantService.add(EJobName.FILE_IMPORT_ADDRESSES.toString(),
                            requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
                    return importAddressesFromCSV(parser, in, jobManagerTenant);
                } else if (clazz == PaymentInfo.class) {
                    jobManagerTenant = jobManagerTenantService.add(EJobName.FILE_IMPORT_PAYMENT_INFOS.toString(),
                            requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
                    return importPaymentInfosFromCSV(parser, in, jobManagerTenant);
                } else if (clazz == CustomerSubscription.class) {
                    jobManagerTenant = jobManagerTenantService.add(EJobName.FILE_IMPORT_SUBSCRIPTIONS.toString(),
                            requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
                    return importSubscriptionMappingsFromCSV(parser, in, jobManagerTenant, isLegacy);
                } else if (clazz == PaymentTransactionDetail.class) {
                    jobManagerTenant = jobManagerTenantService.add(EJobName.FILE_IMPORT_PAYMENTS.toString(),
                            requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
                    return importPaymentsFromCSV(parser, in, jobManagerTenant);
                }
            }
        } catch (Exception e) {
            if (jobManagerTenant != null) {
                jobManagerTenant.setStatus(EJobStatus.FAILED.toString());
                jobManagerTenant.setErrors(true);
                jobManagerTenant.setLog(e.getMessage().getBytes());
                jobManagerTenantService.saveOrUpdate(jobManagerTenant);
            }
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public UploadResponse importUsersFromCSV(EMigrationParserLocation parser, InputStream inputStream,
                                             JobManagerTenant jobManagerTenant) throws IOException {
        String location = parser.getLocation();
        MigrationParser migrationParser =
                parserFactory
                        .getMigrationParser(location);
        List<User> migratedUsers = migrationParser.importCustomersFromCSV(inputStream);
        AtomicInteger migratedCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();
        List<Long> createdIds = new ArrayList<>();
        List<Long> updatedIds = new ArrayList<>();
        List<String> migratedExternalIds = new ArrayList<>();
        List<String> updatedExternalIds = new ArrayList<>();
        final AtomicBoolean[] toAdd = {new AtomicBoolean(false)};
        List<String> passwords = new ArrayList<>();
        List<User> usersToSave = new ArrayList<>();
        List<Stage> stagesToSave = new ArrayList<>();
//        String encodedDefaultPassword = encoder.encode(DEFAULT_PASSWORD);
        String finalDefaultEmail = "customerservice@novelenergy.biz";
        migratedUsers.forEach(user -> {
            Stage stage = stageRepository.findByExternalId(user.getExternalId());
            if (stage != null) {
                user.setAcctId(stage.getUser().getAcctId());
                user.setCreatedAt(stage.getUser().getCreatedAt());
                updatedCount.getAndIncrement();
                updatedIds.add(stage.getUser().getAcctId());
                updatedExternalIds.add(user.getExternalId());
            } else {
                migratedCount.getAndIncrement();
                toAdd[0].set(true);
            }
            if (!user.getEmailAddress().isEmpty()) {
                // if email address is available for the customer, please input same email in both user and email fields
                user.setUserName(user.getEmailAddress());
            } else {
                // If user doesn’t have an Email address Please keep user column blank and email address to be a
                // default novel email id
                user.setEmailAddress(finalDefaultEmail);
                // In cases the user column is blank the system will auto generate user id as FirstName + portal id:
                // example SAM421 or COLIN211 (not case sensitive on login)
            }
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                passwords.add(user.getPassword());
                user.setPassword(encoder.encode(user.getPassword()));
            }
            /*else {
                user.setPassword(encodedDefaultPassword); // Set after saving
                passwords.add(DEFAULT_PASSWORD);
            }*/

            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            Set<Role> roles = new HashSet<>();
            roles.add(roleService.findByName(ERole.ROLE_CUSTOMER.toString()));
            user.setRoles(roles);

            user.setStatus(EUserStatus.ACTIVE.getStatus());
            Date date = new Date();
            if (user.getActiveDate() == null) {
                user.setActiveDate(date);
            }
            if (user.getRegisterDate() == null) {
                user.setRegisterDate(date);
            }
            usersToSave.add(user);
            if (toAdd[0].get()) {
                migratedExternalIds.add(user.getExternalId());
                toAdd[0] = new AtomicBoolean(false);
            }
        });
        List<User> savedUsers = userService.saveAll(usersToSave);
        savedUsers.forEach(user -> {
            if (!updatedIds.contains(user.getAcctId())) {
                createdIds.add(user.getAcctId());
                stagesToSave.add(Stage.builder()
                        .user(user)
                        .externalId(user.getExternalId())
                        .build());
            }
        });
        stageRepository.saveAll(stagesToSave);
        savedUsers = postSaveCustomers(savedUsers);
        List<String> userNames = new ArrayList<>();
        savedUsers.forEach(user -> {
            userNames.add(user.getUserName());
        });
        LOGGER.info(updatedCount.get() + " updated and " + migratedCount.get() + " migrated from external system");
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EMigrationEntitiy.CUSTOMER.toString())
                .migrated(migratedCount.get())
                .updated(updatedCount.get())
                .createdIds(createdIds)
                .updatedIds(updatedIds)
                .migratedExternalIds(migratedExternalIds)
                .updatedExternalIds(updatedExternalIds)
                .userNames(userNames)
                .passwords(passwords)
                .build();
    }

    private List<User> postSaveCustomers(List<User> usersToDb) {
        List<User> usersWithUserName = new ArrayList<>();
        usersToDb.forEach(user -> {
            if (user.getUserName() == null || user.getUserName().isEmpty()) {
                user.setUserName(user.getFirstName().trim() + user.getAcctId());
                usersWithUserName.add(user);
            }
            // acct_id + first_name + "!"
            if (user.getPassword() == null) {
                user.setPassword(encoder.encode(user.getAcctId() + user.getFirstName() + "!"));
            }
        });
        return userService.saveAll(usersWithUserName);
    }

    @Override
    public UploadResponse importAddressesFromCSV(EMigrationParserLocation parser, InputStream inputStream,
                                                 JobManagerTenant jobManagerTenant) throws IOException {
        String location = parser.getLocation();
        MigrationParser migrationParser =
                parserFactory
                        .getMigrationParser(location);
        List<Address> migratedAddresses = migrationParser.importAddressesFromCSV(inputStream);
        AtomicInteger migratedCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();
        List<Long> createdIds = new ArrayList<>();
        List<Long> updatedIds = new ArrayList<>();
        List<String> migratedExternalIds = new ArrayList<>();
        List<String> updatedExternalIds = new ArrayList<>();
        final AtomicBoolean[] toAdd = {new AtomicBoolean(false)};
        List<String> invalidExternalIds = new ArrayList<>();
        List<Address> addressesToSave = new ArrayList<>();
        migratedAddresses.forEach(address -> {
            Stage stage = stageRepository.findByExternalId(address.getExternalId());
            if (stage == null) {
                invalidExternalIds.add(address.getExternalId());
            } else {
                Address addressFromDb = addressService.findByUserAccountAndAlias(stage.getUser(), address.getAlias());
                address.setUserAccount(stage.getUser());
                if (addressFromDb == null) {
                    migratedCount.getAndIncrement();
                    toAdd[0].set(true);
                } else {
                    address.setId(addressFromDb.getId());
                    address.setCreatedAt(addressFromDb.getCreatedAt());
                    updatedCount.getAndIncrement();
                    updatedIds.add(addressFromDb.getId());
                    updatedExternalIds.add(address.getExternalId());
                }
                addressesToSave.add(address);
                if (toAdd[0].get()) {
                    migratedExternalIds.add(address.getExternalId());
                    toAdd[0] = new AtomicBoolean(false);
                }
            }
        });
        List<Address> savedAddresses = addressService.save(addressesToSave);
        savedAddresses.forEach(address -> {
            if (!updatedIds.contains(address.getAcctId())) {
                createdIds.add(address.getId());
            }
        });
        LOGGER.info(updatedCount.get() + " updated and " + migratedCount.get() + " migrated from external system");
        if (!invalidExternalIds.isEmpty()) {
            LOGGER.info("Following invalid external ids found: " + invalidExternalIds.toArray(new String[0]));
        }
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EMigrationEntitiy.ADDRESS.toString())
                .migrated(migratedCount.get())
                .updated(updatedCount.get())
                .createdIds(createdIds)
                .updatedIds(updatedIds)
                .migratedExternalIds(migratedExternalIds)
                .updatedExternalIds(updatedExternalIds)
                .invalidExternalIds(invalidExternalIds)
                .build();
    }

    @Override
    public UploadResponse importPaymentInfosFromCSV(EMigrationParserLocation parser,
                                                    InputStream inputStream,
                                                    JobManagerTenant jobManagerTenant) throws IOException {
        String location = parser.getLocation();
        MigrationParser migrationParser =
                parserFactory
                        .getMigrationParser(location);
        List<PaymentInfo> migratedPaymentInfos = migrationParser.importPaymentInfoFromCSV(inputStream);
        AtomicInteger migratedCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();
        List<Long> createdIds = new ArrayList<>();
        List<Long> updatedIds = new ArrayList<>();
        List<String> migratedExternalIds = new ArrayList<>();
        List<String> updatedExternalIds = new ArrayList<>();
        final AtomicBoolean[] toAdd = {new AtomicBoolean(false)};
        List<String> invalidExternalIds = new ArrayList<>();
        migratedPaymentInfos.forEach(paymentInfo -> {
            Stage stage = stageRepository.findByExternalId(paymentInfo.getExternalId());
            if (stage == null) {
                invalidExternalIds.add(paymentInfo.getExternalId());
            } else {
                PaymentInfo paymentInfoFromDb =
                        paymentInfoService.findByPortalAccountAndPaymentSrcAlias(stage.getUser(),
                                paymentInfo.getPaymentSrcAlias());
                paymentInfo.setPortalAccount(stage.getUser());
                paymentInfo.setAcctId(stage.getUser().getAcctId());
                if (paymentInfoFromDb == null) {
                    migratedCount.getAndIncrement();
                    toAdd[0].set(true);
                } else {
                    paymentInfo.setId(paymentInfoFromDb.getId());
                    paymentInfo.setCreatedAt(paymentInfoFromDb.getCreatedAt());
                    updatedCount.getAndIncrement();
                    updatedIds.add(paymentInfoFromDb.getId());
                    updatedExternalIds.add(paymentInfo.getExternalId());
                }
                PaymentInfo pInfo = paymentInfoService.addOrUpdate(paymentInfo);
                if (toAdd[0].get()) {
                    createdIds.add(pInfo.getId());
                    migratedExternalIds.add(paymentInfo.getExternalId());
                    toAdd[0] = new AtomicBoolean(false);
                }
            }
        });
        LOGGER.info(updatedCount.get() + " updated and " + migratedCount.get() + " migrated from external system");
        if (!invalidExternalIds.isEmpty()) {
            LOGGER.info("Following invalid external ids found: " + invalidExternalIds.toArray(new String[0]));
        }
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EMigrationEntitiy.PAYMENT_INFO.toString())
                .migrated(migratedCount.get())
                .updated(updatedCount.get())
                .createdIds(createdIds)
                .updatedIds(updatedIds)
                .migratedExternalIds(migratedExternalIds)
                .updatedExternalIds(updatedExternalIds)
                .invalidExternalIds(invalidExternalIds)
                .unmappedExternalIds(stageRepository.findExternalIdsWithoutPaymentInfo())
                .build();
    }

    @Override
    public UploadResponse importSubscriptionMappingsFromCSV(EMigrationParserLocation parser,
                                                            InputStream inputStream, JobManagerTenant jobManagerTenant,
                                                            Boolean isLegacy) throws IOException {
        String location = parser.getLocation();
        MigrationParser migrationParser =
                parserFactory
                        .getMigrationParser(location);
        Map<CustomerSubscription, List<CustomerSubscriptionMapping>> migratedSubscriptionMappings =
                migrationParser.importSubscriptionMappingsFromCSV(inputStream);
        AtomicInteger migratedCount = new AtomicInteger();
        List<Long> createdIds = new ArrayList<>();
        List<String> migratedExternalIds = new ArrayList<>();
        List<String> invalidExternalIds = new ArrayList<>();
        List<MeasureDefinitionTenantDTO> measureDefinitions =
                measureDefinitionOverrideService.findByCodes(migratedSubscriptionMappings.values().stream().flatMap(List::stream).collect(Collectors.toList()).stream().map(m -> m.getRateCode()).collect(Collectors.toSet()));
        migratedSubscriptionMappings.entrySet().forEach(mapping -> {
            CustomerSubscription key = mapping.getKey();
            String externalId = key.getExternalId();
            Stage stage = stageRepository.findByExternalId(externalId);
            if (stage == null) {
                invalidExternalIds.add(externalId);
            } else {
                User user = stage.getUser();
                key.setUserAccount(user);
                key.setUserAccountId(user.getAcctId());
                key.setSubscriptionStatus(ESubscriptionStatus.INACTIVE.getStatus());
                SubscriptionRateMatrixHead matrixHead =
                        headRepository.findById(key.getSubscriptionRateMatrixId()).get();
                key.setSubscriptionTemplate(matrixHead.getSubscriptionTemplate());
                key.setCustomerSubscriptionMappings(new ArrayList<>());
                mapping.getValue().forEach(m -> {
                    m.setSubscription(key);
                    m.setSubscriptionRateMatrixHead(matrixHead);
                    m.setMeasureDefinition(measureDefinitions.stream().filter(fd -> fd.getCode().equals(m.getRateCode())).findFirst().get());
                    m.setMeasureDefinitionId(measureDefinitions.stream().filter(fd -> fd.getCode().equals(m.getRateCode())).findFirst().get().getId());
                });
                key.setCustomerSubscriptionMappings(mapping.getValue());
                migratedExternalIds.add(externalId);
                migratedCount.getAndIncrement();
            }
        });

        List<CustomerSubscription> subscriptions =
                subscriptionService.addCustomerSubscriptions(migratedSubscriptionMappings.keySet().stream().collect(Collectors.toList()), isLegacy);
        createdIds.addAll(subscriptions.stream().map(s -> s.getId()).collect(Collectors.toList()));
        LOGGER.info(migratedCount.get() + " subscriptions migrated from external system");
        if (!invalidExternalIds.isEmpty()) {
            LOGGER.info("Following invalid external ids found: " + invalidExternalIds.toArray(new String[0]));
        }
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EMigrationEntitiy.CUSTOMER_SUBSCRIPTION.toString())
                .migrated(migratedCount.get())
                .createdIds(createdIds)
                .migratedExternalIds(migratedExternalIds)
                .invalidExternalIds(invalidExternalIds)
                .build();
    }

    @Override
    public UploadResponse importPaymentsFromCSV(EMigrationParserLocation parser, InputStream inputStream,
                                                JobManagerTenant jobManagerTenant) throws IOException {
        billInvoiceService.addPaymentTransactionHeadsForInvoices();
        String location = parser.getLocation();
        MigrationParser migrationParser =
                parserFactory
                        .getMigrationParser(location);
        List<PaymentTransactionDetail> transactionDetails = migrationParser.importCustomerPaymentFromCSV(inputStream);
        List<PaymentTransactionDetail> toSave = new ArrayList<>();
        List<Long> invalidInvoiceIds = new ArrayList<>();
        List<Long> addedDetailIds;
        List<Long> updatedDetailIds = new ArrayList<>();
        List<Long> invalidDetailIds = new ArrayList<>();
        List<String> invalidCodes = new ArrayList<>();
        transactionDetails.forEach(detail -> {
            Long invoiceRefId = detail.getInvoiceRefId();
            if (invoiceRefId == null) {
                return;
            }
            BillingInvoice billingInvoice;
            try {
                billingInvoice = billInvoiceService.findById(invoiceRefId);
            } catch (NotFoundException e) {
                // if invalid skip and log
                LOGGER.error("Invalid invoice_ref_id ", invoiceRefId, e);
                invalidInvoiceIds.add(invoiceRefId);
                return;
            }
            String paymentCode = detail.getPaymentCode();
            if (!"BILLING".equalsIgnoreCase(paymentCode)) {
                LOGGER.warn("payment_code is not BILLING (" + paymentCode + ")");
                invalidCodes.add(paymentCode);
                return;
            }
            addToSaveable(detail, billingInvoice, toSave, updatedDetailIds, invalidDetailIds);
        });
        List<PaymentTransactionDetail> details = transactionDetailRepository.saveAll(toSave);
        addedDetailIds = details.stream().map(d -> d.getPayDetId()).collect(Collectors.toList());
        addedDetailIds.removeAll(updatedDetailIds);
        addedDetailIds.removeAll(invalidDetailIds);

        List<Long> markedPaidIds = postImportPayments(details);
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EMigrationEntitiy.PAYMENT_TRANSACTION_DETAIL.toString())
                .invalidInvoiceIds(invalidInvoiceIds)
                .addedDetailIds(addedDetailIds)
                .updatedDetailIds(updatedDetailIds)
                .invalidDetailIds(invalidDetailIds)
                .invalidCodes(invalidCodes)
                .markedPaidIds(markedPaidIds)
                .build();
    }

    private void addToSaveable(PaymentTransactionDetail detail, BillingInvoice billingInvoice,
                               List<PaymentTransactionDetail> toSave,
                               List<Long> updatedDetailIds,
                               List<Long> invalidDetailIds) {
        BillingHead billingHead = billingHeadService.findByInvoice(billingInvoice);
        if (billingHead.getBillStatus().equals(EBillStatus.PAID.getStatus())) {
            LOGGER.info("Cannot process billing invoice " + billingInvoice.getId() + " is " + EBillStatus.PAID.getStatus());
            return;
        }
        Long payDetId = detail.getPayDetId();
        PaymentTransactionHead transactionHead = transactionHeadRepository.findByInvoice(billingInvoice);
        List<PaymentTransactionDetail> details =
                transactionDetailRepository.findByPaymentTransactionHead(transactionHead);
        Date reconDate = detail.getReconDate();
        if (reconDate != null) {
            if (Utility.isBefore(DateTime.now().toDate(), reconDate)) {
                detail.setReconDate(null);
                detail.setReconExpectedDate(reconDate);
            }
        }
        if ((detail.getIssuerId() != null && !detail.getIssuerId().isEmpty()) || "MANUAL".equals(detail.getIssuerReconStatus())) {
            detail.setIssuerReconStatus(EReconStatus.get("COMPLETED").getStatus());
        }
        detail.setAmt(utility.round(detail.getAmt(), utility.getCompanyPreference().getRounding()));
        if (payDetId == null) {
            detail.setLineSeqNo((details.size() +
                    toSave.stream().filter(s -> s.getPayDetId() == null && s.getInvoiceRefId().longValue() == billingInvoice.getId().longValue()).count() + 1));
            detail.setPaymentTransactionHead(transactionHead);
            toSave.add(detail);
        } else {
            Optional<PaymentTransactionDetail> paymentTransactionDetailDb =
                    transactionDetailRepository.findById(payDetId);
            if (!paymentTransactionDetailDb.isPresent()) {
                // if present and not in record skip and log
                LOGGER.warn("Invalid pay_det_id " + payDetId);
                invalidDetailIds.add(payDetId);
                return;
            } else {
                PaymentTransactionDetail paymentTransactionDetail = paymentTransactionDetailDb.get();
                if ("INVALID".equalsIgnoreCase(paymentTransactionDetail.getStatus())) {
                    // if invalid status then override whole record and log
                    paymentTransactionDetail = PaymentMapper.resetPaymentTransactionDetail(paymentTransactionDetail,
                            detail);
                    LOGGER.info("PaymentTransactionDetail record " + payDetId + " has been revalidated");
                } else {
                    paymentTransactionDetail =
                            PaymentMapper.toUpdatedPaymentTransactionDetail(paymentTransactionDetail, detail);
                }
                paymentTransactionDetail.setInvoiceRefId(billingInvoice.getId());
                toSave.add(paymentTransactionDetail);
                updatedDetailIds.add(payDetId);
            }
        }
    }

    private List<Long> postImportPayments(List<PaymentTransactionDetail> details) {
        List<Long> markedPaidIds = new ArrayList<>();
        details.stream().filter(utility.distinctByKey(PaymentTransactionDetail::getPaymentTransactionHead)).forEach(detail -> {
            PaymentTransactionHead transactionHead = detail.getPaymentTransactionHead();
            BillingInvoice invoice = transactionHead.getInvoice();
            List<PaymentTransactionDetail> paymentDetails =
                    transactionDetailRepository.findByPaymentTransactionHead(transactionHead);
            Double totalAmt = paymentDetails.stream().mapToDouble(d -> d.getAmt()).sum();
            int rounding = utility.getCompanyPreference().getRounding();
            totalAmt = utility.round(totalAmt, rounding);
            transactionHead.setNet(totalAmt);
            transactionHeadRepository.save(transactionHead);
            BillingHead billingHead = billingHeadService.findByInvoice(invoice);
            Double invoiceAmount = utility.round(billingHead.getAmount(), rounding);
            Double variance = totalAmt - invoiceAmount;
            if (variance >= 0) {
                billingHead.setBillStatus(EBillStatus.PAID.getStatus());
                markedPaidIds.add(billingHeadService.addOrUpdateBillingHead(billingHead).getId());
            } else {
                LOGGER.info("Variance " + variance + " less than total invoice");
            }
        });
        return markedPaidIds;
    }

}
