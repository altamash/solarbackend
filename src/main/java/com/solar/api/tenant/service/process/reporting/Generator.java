package com.solar.api.tenant.service.process.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.PortalAttributeSAASService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDetailDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.process.rule.RuleExecutionLog;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixDetail;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.*;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import com.solar.api.tenant.service.process.rule.RuleExecutionLogService;
import net.sf.jasperreports.engine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Generator {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private RuleExecutionLogService ruleExecutionLogService;
    @Autowired
    private PortalAttributeSAASService portalAttributeSAASService;
    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private BillingDetailService billingDetailService;
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private Utility utility;


    /**
     * Delete Invoice Blobs
     */
    public void deleteBlob(String container, String fileName, Long compKey, String path) {
        storageService.deleteBlob(container,fileName,compKey,path);
    }


    /**
     * @param billHeadId
     * @throws Exception
     */
    public String generatePDF(Long billHeadId, Long compKey) throws Exception {
        BillingHead billingHead = billingHeadService.findByIdFetchBillingDetails(billHeadId);
        /**
         * To be used and improvised in future for bulk template fetching
         */
//        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
//            storageService.downloadToOutputStream(os, "saas", reportTemplate.getTemplateURI() + "/" +
//            reportTemplate.getTemplateName());
//            try (InputStream is = new ByteArrayInputStream(os.toByteArray())) {
//                template = IOUtils.toString(is, String.valueOf(StandardCharsets.UTF_8));
//            }
//        }
//        deleteBlob(appProfile, billingHead.getInvoice().getId()
//                + " - " + userService.findById(billingHead.getUserAccountId()).getFirstName().trim()
//                + " " + userService.findById(billingHead.getUserAccountId()).getLastName().trim()
//                + ", " + billingHead.getBillingMonthYear() + AppConstants.MIME_TYPE_PDF,
//                compKey, AppConstants.INVOICE_REPORT_PATH);
        //Generate PDF through Jasper
        byte[] byteArray = createPdfReport(billingHead);

        //Save PDF Invoice in AzureStorage
        try (ByteArrayOutputStream os = new ByteArrayOutputStream(byteArray.length)) {
            os.write(byteArray, 0, byteArray.length);
            try (ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray())) {
                return storageService.uploadInputStream(is, (long) os.size(), appProfile,
                        "tenant/" + compKey //utility.getCompKey()
                                + AppConstants.INVOICE_REPORT_PATH,
                        billingHead.getInvoice().getId()
                                + " - " + userService.findById(billingHead.getUserAccountId()).getFirstName().trim()
                                + " " + userService.findById(billingHead.getUserAccountId()).getLastName().trim()
                                + ", " + billingHead.getBillingMonthYear() + ".pdf", compKey, true);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    /**
     * @param billingHead
     * @throws JRException JasperReport Integration
     */
    private byte[] createPdfReport(final BillingHead billingHead) throws JRException, ParseException {
        String totalString = "";
        String gardenId = null;
        String premiseNo = null;

        String aname = "Arinna, LLC";
        String astreet = "2303 Wycliff Street";
        String aapartment = "Suite 300";
        String astate = "St. Paul, MN 55114";

        // Fetching the .jrxml file from the resources folder.
        final InputStream stream = this.getClass().getResourceAsStream("/invoice.jrxml");
        User user = userService.findById(billingHead.getUserAccountId());
        List<Address> addresses = addressService.findAddressByUserAccount(user.getAcctId());
        List<Address> billingAddress = addresses.stream().filter(s ->
                "Mailing".equalsIgnoreCase(s.getAddressType())).collect(Collectors.toList());

        List<BillingDetail> billingDetail = billingDetailService.findByBillingHeadId(billingHead.getId());

        //List<Address> shippingAddress = addresses.stream().filter(s ->
        //"Site".equalsIgnoreCase(s.getAddressType())).collect(Collectors.toList());
        //List<BillingDetail> addToBill1 = billingHead.getBillingDetails().stream().filter(s -> s.getAddToBillAmount
        // ()).collect(Collectors.toList());
        //List<BillingDetail> addToBill1 = billingDetail.stream().filter(s -> s.getAddToBillAmount()).collect
        // (Collectors.toList());
        //SubscriptionRateMatrixHead subscriptionRateMatrixHead = subscriptionService
        // .findSubscriptionRateMatrixHeadById(subscription.getSubscriptionRateMatrixId());

        CustomerSubscription subscription =
                subscriptionService.findCustomerSubscriptionById(billingHead.getSubscriptionId());
        SubscriptionRateMatrixDetail SCSGNDetail =
                subscriptionService.findSubscriptionRateMatrixHeadById(subscription.getSubscriptionRateMatrixId()).getSubscriptionRateMatrixDetails().stream()
                        .filter(matrix -> "SCSGN".equals(matrix.getRateCode())).findFirst().orElse(null);
        CustomerSubscriptionMapping customerSubscriptionMapping =
                subscriptionService.findCustomerSubscriptionMappingByRateCodeAndSubscription("PN", subscription);
        gardenId = SCSGNDetail.getDefaultValue();
        premiseNo = customerSubscriptionMapping.getValue();

        /**
         *
         * Dynamic values
         * below ListStream
         *
         * All rateCodes against addToBill = 1
         *
         */
        BillingDetail invoiceLoad = billingDetail.stream().filter(s ->
                "MPA".equalsIgnoreCase(s.getBillingCode())).findFirst().get();

        List<Address> shippingAddresses = addressService.findSiteAddressWithAlias("SADD", billingHead.getSubscriptionId(),
                billingHead.getUserAccountId());
        //get maxId
        List<RuleExecutionLog> ruleExecutionLogs =
                ruleExecutionLogService.findAllByBillIdAndRateCodeOrderByIdAsc(billingHead.getId(), "SRTE");

        //for discount
        CustomerSubscriptionMapping discountCode = subscriptionService.getRateCode(subscription,
                Arrays.asList(Constants.DISCOUNT_RATE_CODES.DSC, Constants.DISCOUNT_RATE_CODES.DSCP));

       /* List<RuleExecutionLog> rate = ruleExecutionLogs.stream().filter(s ->
                "SRTE".equalsIgnoreCase(s.getRateCode())).collect(Collectors.toList());
       */
        //PortalAttributeValue billingCodeDescription = portalAttributeService.findByAttributeValue(addToBill1.get(0)
        // .getBillingCode());
        // Compile the Jasper report from .jrxml to .japser
        final JasperReport report = JasperCompileManager.compileReport(stream);


        // Adding the additional parameters to the pdf.
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("SUB_INVOICE", "SUB_INVOICE");
        parameters.put("first_name", user.getFirstName());
        parameters.put("last_name", user.getLastName());
        parameters.put("remit_aname", aname);
        parameters.put("remit_astreet", astreet);
        parameters.put("remit_aapartment", aapartment);
        parameters.put("remit_astate", astate);

        parameters.put("ba_street", billingAddress.isEmpty() ? " " : billingAddress.get(0).getAddress1());
        parameters.put("ba_state", billingAddress.isEmpty() ? " " : billingAddress.get(0).getState());
        parameters.put("ba_city", billingAddress.isEmpty() ? " " : billingAddress.get(0).getCity());
        parameters.put("ba_country_code", billingAddress.isEmpty() ? " " : billingAddress.get(0).getCountryCode());
        parameters.put("ba_postal", billingAddress.isEmpty() ? " " : billingAddress.get(0).getPostalCode());

        /**
         * logic for unique result
         */
        Address shippingAddress = shippingAddresses.size() == 0 ? null : shippingAddresses.get(0);

        parameters.put("sa_street", shippingAddress != null ? shippingAddress.getAddress1() : " ");
        parameters.put("sa_state", shippingAddress != null ? shippingAddress.getState() : " ");
        parameters.put("sa_city", shippingAddress != null ? shippingAddress.getCity() : " ");
        parameters.put("sa_country_code", shippingAddress != null ? shippingAddress.getCountryCode() : " ");
        parameters.put("siteAlias", shippingAddress != null ? shippingAddress.getAlias() : " ");

        parameters.put("sa_postal", billingAddress.isEmpty() ? " " : billingAddress.get(0).getPostalCode());
        parameters.put("customer_number", user.getAcctId());
        //parameters.put("customer_reference", " ");
        parameters.put("subscription_id", billingHead.getSubscriptionId());
        parameters.put("invoice_number", billingHead.getInvoice() != null ? billingHead.getInvoice().getId() : " ");
        String convertedInvoiceDate = "";
        String convertedDueDate = "";
        try {
            convertedInvoiceDate = Utility.parseDaySuffix(billingHead.getInvoice().getDateOfInvoice());
            convertedDueDate = Utility.parseDaySuffix(billingHead.getInvoice().getDueDate());
//            convertedInvoiceDate = rateFunctions.parseDateFromTimestamp(String.valueOf(billingHead.getInvoice()
//            .getDateOfInvoice()));
//            convertedDueDate = rateFunctions.parseDateFromTimestamp(String.valueOf(billingHead.getInvoice()
//            .getDueDate()));
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("convertedInvoiceDate", convertedInvoiceDate);
            messageJson.put("convertedDueDate", convertedDueDate);
            LOGGER.error(messageJson.toPrettyString(), e);
        }
        String readableDate = null;
        readableDate =
                new DateFormatSymbols().getShortMonths()[Integer.parseInt(billingHead.getBillingMonthYear().split("-")[0]) - 1] +
                        " " + billingHead.getBillingMonthYear().split("-")[1];

        parameters.put("invoice_date", billingHead.getInvoice() != null ? convertedInvoiceDate : " ");
        parameters.put("due_date", billingHead.getInvoice() != null ? convertedDueDate : " ");
        parameters.put("disc_date", " ");
        parameters.put("trans", billingHead.getBillingMonthYear());

/*
        parameters.put("mth", readableDate);
        parameters.put("line", String.valueOf(billingDetail.get(0).getLineSeqNo()));
        parameters.put("desc", billingCodeDescription.getDescription());
*/

//        rateValue = Math.round(rate.get(0).getReturnedValue() * 100.0) / 100.0;
       /* rateValue = rate.size() > 0 ? rate.get(rate.size() - 1).getReturnedValue() : 0;
        parameters.put("rate", String.valueOf(rateValue) + " /kWh");

        mpa = String.valueOf(invoiceLoad.get(0).getValue() * 100.0 / 100.0);
        parameters.put("load", mpa.equals("0.0") ? " " : mpa);
*/
/*
        for (int k = 0; k < invoiceAmount.size(); k++) {
            amount += Math.round(invoiceAmount.get(k).getValue() * 100.0) / 100.0;
        }
        amountString = "$ " + String.valueOf(amount);
        parameters.put("amount", amountString);
*/

        parameters.put("premise_no", premiseNo != null ? premiseNo : " ");
        parameters.put("garden_id", gardenId != null ? gardenId : " ");

        parameters.put("monthlyProdCreditRate", ruleExecutionLogs.size() == 0 ? "" :
                ruleExecutionLogs.get(0).getReturnedValue() != null ?
                        ruleExecutionLogs.get(0).getReturnedValue().toString().concat("/kWh") : "");
        parameters.put("monthlyProdLoad", invoiceLoad.getValue().toString().concat(" kWhs"));
        parameters.put("discount", discountCode != null ? discountCode.getValue() : 0.0);
        //parameters.put("terms", "Net 15");
        parameters.put("terms", "Due Upon Receipt");
        parameters.put("discountRateCodeCheck", discountCode.getRateCode());

        //incase of ARR
        //if(discountCode!=null && discountCode.getRateCode().equals("DSCP")){
        //  parameters.put("terms", "Due Upon Receipt");
        //}

        // Fetching the employees from the data source.
        //final JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(Arrays.asList(billingHead));

        List<BillingDetailDTO> billingDetailDS = new ArrayList<>();

        Integer lineNo = 0;
        for (BillingDetail bd : billingHead.getBillingDetails()) {
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
            if (bd.getAddToBillAmount() || (bd.getBillingCode().equals(Constants.BILLING_CODES.ABCRE) && bd.getValue() != 0)) {
                billingDetailDS.add(BillingDetailDTO.builder()
                        .productionMonth(readableDate)
                        .lineSeqNo(++lineNo)
                        .billingCode(billCode)
                        //.rateCode(rateCode)
                        //.kwhsValue(val)
                        .lineAmount("$".concat(String.format("%.2f", lineAmount)))
                        .build());
            }
        }

        totalString = "$" + String.valueOf(String.format("%.2f", billingHead.getAmount()));
        parameters.put("total", totalString);
        parameters.put("billingDetailDS", billingDetailDS);

        // Filling the report with the employee data and additional parameters information.
        final JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

        // Users can change as per their project requirements or can take it as request input requirement.
        // For simplicity, this tutorial will automatically place the file under the "c:" drive.
        // If users want to download the pdf file on the browser, then they need to use the "Content-Disposition"
        // technique.
//        final String filePath = "C:\\Users\\Al Shaikh\\Desktop\\";
        // Export the report to a PDF file.
//        JasperExportManager.exportReportToPdfFile(print, filePath
//                + billingHead.getInvoice().getId()
//                + " - " + userService.findById(billingHead.getUserAccountId()).getFirstName() + " " + userService
//                .findById(billingHead.getUserAccountId()).getLastName()
//                + ", " + billingHead.getBillingMonthYear() + ".pdf");

        byte[] byteArrayOutputStream = JasperExportManager.exportReportToPdf(print);

        return byteArrayOutputStream;
    }

    private Map<String, Object> generateTemplateReport(final BillingHead billingHead) throws JRException, ParseException {
        String totalString = "";
        String gardenId = null;
        String premiseNo = null;

        String aname = "Arinna, LLC";
        String astreet = "2303 Wycliff Street";
        String aapartment = "Suite 300";
        String astate = "St. Paul, MN 55114";

        // Fetching the .jrxml file from the resources folder.
        //final InputStream stream = this.getClass().getResourceAsStream("/invoice.jrxml");
        User user = userService.findById(billingHead.getUserAccountId());
        List<Address> addresses = addressService.findAddressByUserAccount(user.getAcctId());
        List<Address> billingAddress = addresses.stream().filter(s ->
                "Mailing".equalsIgnoreCase(s.getAddressType())).collect(Collectors.toList());

        List<BillingDetail> billingDetail = billingDetailService.findByBillingHeadId(billingHead.getId());

        CustomerSubscription subscription =
                subscriptionService.findCustomerSubscriptionById(billingHead.getSubscriptionId());
        SubscriptionRateMatrixDetail SCSGNDetail =
                subscriptionService.findSubscriptionRateMatrixHeadById(subscription.getSubscriptionRateMatrixId()).getSubscriptionRateMatrixDetails().stream().filter(matrix -> "SCSGN".equals(matrix.getRateCode())).findFirst().orElse(null);
        CustomerSubscriptionMapping customerSubscriptionMapping =
                subscriptionService.findCustomerSubscriptionMappingByRateCodeAndSubscription("PN", subscription);
        gardenId = SCSGNDetail.getDefaultValue();
        premiseNo = customerSubscriptionMapping.getValue();

        /**
         *
         * Dynamic values
         * below ListStream
         *
         * All rateCodes against addToBill = 1
         *
         */
        BillingDetail invoiceLoad = billingDetail.stream().filter(s ->
                "MPA".equalsIgnoreCase(s.getBillingCode())).findFirst().get();

        List<Address> shippingAddresses = addressService.findSiteAddressWithAlias("SADD", billingHead.getSubscriptionId(),
                billingHead.getUserAccountId());
        //get maxId
        List<RuleExecutionLog> ruleExecutionLogs =
                ruleExecutionLogService.findAllByBillIdAndRateCodeOrderByIdAsc(billingHead.getId(), "SRTE");

        //for discount
        CustomerSubscriptionMapping discountCode = subscriptionService.getRateCode(subscription,
                Arrays.asList(Constants.DISCOUNT_RATE_CODES.DSC, Constants.DISCOUNT_RATE_CODES.DSCP));


        // Adding the additional parameters to the pdf.
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("SUB_INVOICE", "SUB_INVOICE");
        parameters.put("first_name", user.getFirstName());
        parameters.put("last_name", user.getLastName());
        parameters.put("remit_aname", aname);
        parameters.put("remit_astreet", astreet);
        parameters.put("remit_aapartment", aapartment);
        parameters.put("remit_astate", astate);

        parameters.put("ba_street", billingAddress.isEmpty() ? " " : billingAddress.get(0).getAddress1());
        parameters.put("ba_state", billingAddress.isEmpty() ? " " : billingAddress.get(0).getState());
        parameters.put("ba_city", billingAddress.isEmpty() ? " " : billingAddress.get(0).getCity());
        parameters.put("ba_country_code", billingAddress.isEmpty() ? " " : billingAddress.get(0).getCountryCode());
        parameters.put("ba_postal", billingAddress.isEmpty() ? " " : billingAddress.get(0).getPostalCode());

        /**
         * logic for unique result
         */
        Address shippingAddress = shippingAddresses.size() == 0 ? null : shippingAddresses.get(0);

        parameters.put("sa_street", shippingAddress != null ? shippingAddress.getAddress1() : " ");
        parameters.put("sa_state", shippingAddress != null ? shippingAddress.getState() : " ");
        parameters.put("sa_city", shippingAddress != null ? shippingAddress.getCity() : " ");
        parameters.put("sa_country_code", shippingAddress != null ? shippingAddress.getCountryCode() : " ");
        parameters.put("siteAlias", shippingAddress != null ? shippingAddress.getAlias() : " ");

        parameters.put("sa_postal", billingAddress.isEmpty() ? " " : billingAddress.get(0).getPostalCode());
        parameters.put("customer_number", user.getAcctId());
        //parameters.put("customer_reference", " ");
        parameters.put("subscription_id", billingHead.getSubscriptionId());
        parameters.put("invoice_number", billingHead.getInvoice() != null ? billingHead.getInvoice().getId() : " ");
        String convertedInvoiceDate = "";
        String convertedDueDate = "";
        try {
            convertedInvoiceDate = Utility.parseDaySuffix(billingHead.getInvoice().getDateOfInvoice());
            convertedDueDate = Utility.parseDaySuffix(billingHead.getInvoice().getDueDate());

        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("convertedInvoiceDate", convertedInvoiceDate);
            messageJson.put("convertedDueDate", convertedDueDate);
            LOGGER.error(messageJson.toPrettyString(), e);
        }
        String readableDate = null;
        readableDate =
                new DateFormatSymbols().getShortMonths()[Integer.parseInt(billingHead.getBillingMonthYear().split("-")[0]) - 1] +
                        " " + billingHead.getBillingMonthYear().split("-")[1];

        parameters.put("invoice_date", billingHead.getInvoice() != null ? convertedInvoiceDate : " ");
        parameters.put("due_date", billingHead.getInvoice() != null ? convertedDueDate : " ");
        parameters.put("disc_date", " ");
        parameters.put("trans", billingHead.getBillingMonthYear());

        parameters.put("premise_no", premiseNo != null ? premiseNo : " ");
        parameters.put("garden_id", gardenId != null ? gardenId : " ");

        parameters.put("monthlyProdCreditRate", ruleExecutionLogs.size() == 0 ? "" :
                ruleExecutionLogs.get(0).getReturnedValue() != null ?
                        ruleExecutionLogs.get(0).getReturnedValue().toString().concat("/kWh") : "");
        parameters.put("monthlyProdLoad", invoiceLoad.getValue().toString().concat(" kWhs"));
        parameters.put("discount", discountCode != null ? discountCode.getValue() : 0.0);
        //parameters.put("terms", "Net 15");
        parameters.put("terms", "Due Upon Receipt");
        parameters.put("discountRateCodeCheck", discountCode.getRateCode());

        //incase of ARR
        //if(discountCode!=null && discountCode.getRateCode().equals("DSCP")){
        //  parameters.put("terms", "Due Upon Receipt");
        //}

        // Fetching the employees from the data source.
        //final JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(Arrays.asList(billingHead));

        List<BillingDetailDTO> billingDetailDS = new ArrayList<>();

        Integer lineNo = 0;
        for (BillingDetail bd : billingHead.getBillingDetails()) {
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
            //String val= " " ;
            //String rateCode= " ";
            //Double returnVal= 0.0;

            if (bd.getAddToBillAmount() || (bd.getBillingCode().equals(Constants.BILLING_CODES.ABCRE) && bd.getValue() != 0)) {
                billingDetailDS.add(BillingDetailDTO.builder()
                        .productionMonth(readableDate)
                        .lineSeqNo(++lineNo)
                        .billingCode(billCode)
                        //.rateCode(rateCode)
                        //.kwhsValue(val)
                        .lineAmount("$".concat(String.format("%.2f", lineAmount)))
                        .build());
            }
        }

        totalString = "$" + String.valueOf(String.format("%.2f", billingHead.getAmount()));
        parameters.put("total", totalString);
        parameters.put("billingDetailDS", billingDetailDS);

        // Filling the report with the employee data and additional parameters information.
        //final JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

        // Users can change as per their project requirements or can take it as request input requirement.
        // For simplicity, this tutorial will automatically place the file under the "c:" drive.
        // If users want to download the pdf file on the browser, then they need to use the "Content-Disposition"
        // technique.
//        final String filePath = "C:\\Users\\Al Shaikh\\Desktop\\";
        // Export the report to a PDF file.
//        JasperExportManager.exportReportToPdfFile(print, filePath
//                + billingHead.getInvoice().getId()
//                + " - " + userService.findById(billingHead.getUserAccountId()).getFirstName() + " " + userService
//                .findById(billingHead.getUserAccountId()).getLastName()
//                + ", " + billingHead.getBillingMonthYear() + ".pdf");

        //byte[] byteArrayOutputStream = JasperExportManager.exportReportToPdf(print);
        //ObjectNode messageJson = new ObjectMapper().createObjectNode();
        //messageJson.put();
        return parameters;
    }

    public Map<String,Object> generateTemplate(Long billHeadId, Long compKey) throws Exception {
        BillingHead billingHead = billingHeadService.findByIdFetchBillingDetails(billHeadId);
        /**
         * To be used and improvised in future for bulk template fetching
         */
//        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
//            storageService.downloadToOutputStream(os, "saas", reportTemplate.getTemplateURI() + "/" +
//            reportTemplate.getTemplateName());
//            try (InputStream is = new ByteArrayInputStream(os.toByteArray())) {
//                template = IOUtils.toString(is, String.valueOf(StandardCharsets.UTF_8));
//            }
//        }
//        deleteBlob(appProfile, billingHead.getInvoice().getId()
//                + " - " + userService.findById(billingHead.getUserAccountId()).getFirstName().trim()
//                + " " + userService.findById(billingHead.getUserAccountId()).getLastName().trim()
//                + ", " + billingHead.getBillingMonthYear() + AppConstants.MIME_TYPE_PDF,
//                compKey, AppConstants.INVOICE_REPORT_PATH);
        //Generate PDF through Jasper
        try {
           return generateTemplateReport(billingHead);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());

        }
        return null;
    }
}
