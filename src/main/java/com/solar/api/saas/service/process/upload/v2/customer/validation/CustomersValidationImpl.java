package com.solar.api.saas.service.process.upload.v2.customer.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.solar.api.saas.service.process.upload.v2.validation.IntegrityCheck;
import com.solar.api.saas.service.process.upload.v2.validation.ValidationCheck;
import com.solar.api.saas.service.process.upload.v2.validation.ValidationDTO;
import com.solar.api.tenant.model.CustomerStageTable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CustomersValidationImpl implements CustomersValidation, ValidationCheck, IntegrityCheck {

    private static final String[] MANDATORY_FIELDS_CUSTOMER_INDIVIDUAL = {"First Name",
            "Last Name", "Email", "Phone Number","Utility Provider", "Location Name",
            "Location Type", "Address 1", "Address 2", "City", "State", "Country", "Zip Code",
            "Account Holder Name", "Premise Number", "Reference Number / ID", "Average Monthly Bill", "Payment Method",
            "Account Title", "Bank", "Account Type", "Soft Credit Check", "Soft Credit Reference", "Soft Credit Source",
            "How did you hear about us?", "Promo Code"};
    private static final String[] MANDATORY_FIELDS_CUSTOMER_COMMERCIAL = {"First Name",
            "Last Name", "Email", "Phone Number","Utility Provider", "Location Name",
             "Location Type", "Address 1", "Address 2", "City", "State", "Country", "Zip Code",
            "Account Holder Name", "Premise Number", "Reference Number / ID", "Average Monthly Bill", "Payment Method",
            "Account Title", "Bank", "Account Type", "Soft Credit Check", "Soft Credit Reference", "Soft Credit Source",
            "How did you hear about us?", "Promo Code", "Legal Business Name", "Contact Person Designation",
            "Designation"};
    private static final String[] MANDATORY_FIELDS_NON_CUSTOMER_COMMERCIAL = {"Legal Business Name",
            "Contact Person Designation", "Designation"};
    private static final String[] MANDATORY_FIELDS_FOR_LEAD_PROSOECT_INDIVIDUAL = {"First Name",
            "Last Name", "Email", "Phone Number"};
    private static final String[] MANDATORY_FIELDS_FOR_LEAD_PROSOECT_COMMERCIAL = {"Legal Business Name",
            "Email"};
    private final CustomersValidationCheck validation;
    private final CustomersIntegrityCheck integrity;

    public CustomersValidationImpl(CustomersValidationCheck validation, CustomersIntegrityCheck integrity) {
        this.validation = validation;
        this.integrity = integrity;
    }

    @Override
    public CustomerValidationResult validate(List<?> stagedCustomers, String uploadType, String customerType) {
        List<ValidationDTO> checks = new ArrayList<>();
        List<CustomerStageTable> staged = (List<CustomerStageTable>) stagedCustomers;
        List<Map> mappings = getCustomerMappings((List<CustomerStageTable>) stagedCustomers);
        validateRows(mappings, checks, uploadType, customerType);
        validateIntegrity(mappings, checks);
        checks.sort(Comparator.comparing(ValidationDTO::getLine));

        CustomerValidationResult validationResult = new CustomerValidationResult();
        validationResult.setValidationResult(getValidationSummary(mappings, checks));
        validationResult.setCorrectStagedIds(Joiner.on(", ").join(IntStream.range(0, mappings.size())
               .boxed()
               .filter(i -> validationResult.getCorrectRowIdsList().contains(i))
               .map(m -> staged.get(m.intValue()).getId())
               .collect(Collectors.toList())));
        validationResult.setCorrectRowIdsList(null);
        return validationResult;
    }

    private List<Map> getCustomerMappings(List<CustomerStageTable> stagedCustomers) {
        ObjectMapper mapper = new ObjectMapper();
        return stagedCustomers.stream()
                .map(m -> {
                    try {
                        return mapper.readValue(m.getCsvJson(), Map.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public void validateRows(List<Map> mappings, List<ValidationDTO> checks, String uploadType, String customerType) {
        //uploadType = Lead,customer, prospect  //customerType = individual, commercial
//        validation.checkMandatoryFieldsNotEmpty(checks, mappings, validation.getMandatoryFields()); // union of all mandatory
        validation.checkMandatoryFieldsNotEmpty(
                checks,
                mappings,
                // Determine mandatory fields based on conditions
                ("CUSTOMER".equalsIgnoreCase(uploadType) && "INDIVIDUAL".equalsIgnoreCase(customerType)) ?
                        MANDATORY_FIELDS_CUSTOMER_INDIVIDUAL :
                        ("CUSTOMER".equalsIgnoreCase(uploadType) && "COMMERCIAL".equalsIgnoreCase(customerType)) ?
                                MANDATORY_FIELDS_CUSTOMER_COMMERCIAL :
                                ("LEAD".equalsIgnoreCase(uploadType) && "INDIVIDUAL".equalsIgnoreCase(customerType)) ?
                                        MANDATORY_FIELDS_FOR_LEAD_PROSOECT_INDIVIDUAL :
                                        ("LEAD".equalsIgnoreCase(uploadType) && "COMMERCIAL".equalsIgnoreCase(customerType)) ?
                                                MANDATORY_FIELDS_FOR_LEAD_PROSOECT_COMMERCIAL :
                                                ("PROSPECT".equalsIgnoreCase(uploadType) && "INDIVIDUAL".equalsIgnoreCase(customerType)) ?
                                                        MANDATORY_FIELDS_FOR_LEAD_PROSOECT_INDIVIDUAL :
                                                        ("PROSPECT".equalsIgnoreCase(uploadType) && "COMMERCIAL".equalsIgnoreCase(customerType)) ?
                                                                MANDATORY_FIELDS_FOR_LEAD_PROSOECT_COMMERCIAL :
                                                                ("COMMERCIAL".equalsIgnoreCase(customerType)) ?
                                                                        MANDATORY_FIELDS_NON_CUSTOMER_COMMERCIAL :
                                                                        new String[0]
        ); // additional minus union
        validation.checkIsValidPhone(checks, mappings, "Phone Number");
        validation.checkIsValidEmailField(checks, mappings, "Email");
//        validation.checkIsValidCountryCode(checks, mappings, "country_code");
        validation.checkMultipleOccurrences(checks, mappings, "Email");
        validation.checkMultipleDBOccurrences(checks, mappings, "Email");
        validation.checkCustomFieldFormats(checks, mappings);
    }

    @Override
    public void validateIntegrity(List<Map> mappings, List<ValidationDTO> checks) {
        integrity.checkDuplication(checks, mappings, "user_name");
    }
}
