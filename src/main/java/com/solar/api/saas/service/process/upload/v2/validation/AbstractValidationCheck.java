package com.solar.api.saas.service.process.upload.v2.validation;

import com.google.common.base.Joiner;
import com.solar.api.Constants;
import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.model.contract.EEntityType;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.user.userType.ECustomerDetailStates;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.solar.api.saas.service.process.upload.v2.validation.ValidationUtils.addParseError;

public abstract class AbstractValidationCheck {

    @Autowired
    private UserService userService;
    @Autowired
    private PortalAttributeOverrideService portalAttributeOverrideService;

    @Autowired
    private EntityService entityService;

    public abstract String[] getMandatoryFields();

    public abstract int checkRequiredFieldsInHeader(List<ValidationDTO> checks, Set<?> fileHeader, String... fields);

    public abstract int checkRequiredFieldsInHeader(List<ValidationDTO> checks, String... fields);

    public int checkMandatoryFieldsNotEmpty(List<ValidationDTO> checks, List<Map> mappings, String... fields) {
        List<String> emptyFieldsList = new ArrayList<>();
        List<String> mandatoryFields = List.of(fields);
        for (int i = 0; i < mappings.size(); i++) {
            List<String> keys = new ArrayList((List<String>) mappings.get(i).entrySet().stream()
                    .map(m -> ((Map.Entry) m).getKey()).collect(Collectors.toList()));

            List<String> mandatory = new ArrayList();
            mandatory.addAll(mandatoryFields);
            mandatory.removeAll(keys);

            List<String> emptyFields = (List<String>) mappings.get(i).entrySet().stream()
                    .filter(m -> (mandatoryFields.contains(((String) ((Map.Entry) m).getKey())))
                            && ((Map.Entry) m).getValue() == null || "".equals(((Map.Entry) m).getValue()))
                    .map(m -> ((Map.Entry) m).getKey()).collect(Collectors.toList());
            mandatory.addAll(emptyFields);
            if (!mandatory.isEmpty()) {
                addParseError(checks, i, mappings.get(i),
                        Joiner.on(", ").join(mandatory) + (mandatory.size() == 1 ? " is" : " are") + " mandatory");
                emptyFieldsList.addAll(mandatory);
            }
        }
        return emptyFieldsList.size();
    }

    int checkDateFormatIsValid(List<ValidationDTO> checks, List<Map<?, ?>> mappings) {
        int count = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String calendarMonth = (String) mapping.get("Calendar Month");
            if (calendarMonth != null && !calendarMonth.isEmpty()) {
                try {
                    formatter.parse(calendarMonth);
                } catch (DateTimeParseException e) {
                    addParseError(checks, i, mappings.get(i), "Invalid date format for Calendar Month. Expected MM-yyyy format, but got " + calendarMonth);
                    count++;
                }
            }
        }
        return count;
    }

    // Parse custom fields with fieldsFormat as field|format
    public int checkCustomFieldFormats(List<ValidationDTO> checks, List<Map> mappings, String... fieldsFormat) {
        int count = 0;
        for (String ff : Arrays.asList(fieldsFormat)) {
            for (int i = 0; i < mappings.size(); i++) {
                Map<?, ?> mapping = mappings.get(i);
                String[] fieldAndFormat = ff.split("\\|");
                String field = fieldAndFormat[0];
                String fieldValue = (String) mapping.get(field);
                switch (fieldAndFormat[1]) {
                    case "NUMBER":
                        if (fieldValue != null && !fieldValue.isEmpty() && !com.solar.api.helper.ValidationUtils.isNumeric(fieldValue)) {
                            addParseError(checks, i, mappings.get(i), field + " must be numeric");
                            count++;
                        }
                        break;
                    case "DATE":
                        if (fieldValue != null && !fieldValue.isEmpty() && !com.solar.api.helper.ValidationUtils.isValidDate(fieldValue,
                                Utility.SYSTEM_DATE_FORMAT)) {
                            addParseError(checks, i, mappings.get(i),
                                    field + " must be a date in format " + Utility.SYSTEM_DATE_FORMAT);
                            count++;
                        }
                }
            }
        }
        return count;
    }

    public int checkMultipleOccurrences(List<ValidationDTO> checks, List<Map> mappings, String field) {
        int count = 0;
        if (mappings.stream().filter(m -> m.get(field) != null).findAny().isPresent()) {
            List<?> fieldValuesInFile =
                    mappings.stream().map(m -> m.get(field)).collect(Collectors.toList());
            if (!fieldValuesInFile.isEmpty()) {
                for (int i = 0; i < mappings.size(); i++) {
                    String fieldValue = (String) mappings.get(i).get(field);
                    if (fieldValue != null && !fieldValue.isEmpty()) {
                        int frequency = Collections.frequency(fieldValuesInFile, mappings.get(i).get(field));
                        if (frequency > 1) {
                            addParseError(checks, i, mappings.get(i),
                                    "Multiple occurrences of " + field + " " + fieldValue + " found in file");
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    int checkValuesFromPortalAttribute(List<ValidationDTO> checks, List<Map<?, ?>> mappings, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(f -> {
            for (int i = 0; i < mappings.size(); i++) {
                Map<?, ?> mapping = mappings.get(i);
                String[] fieldParts = f.split("\\|");
                String fieldValue = (String) mapping.get(fieldParts[0]);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    List<PortalAttributeValueTenantDTO> portalAttributeValues = portalAttributeOverrideService.findByPortalAttributeName(fieldParts[1]);
                    if (!portalAttributeValues.stream().filter(attrib -> attrib.getAttributeValue().equals(fieldValue)).findFirst().isPresent()) {
                        addParseError(checks, i, mappings.get(i), fieldParts[0] + " must be selected from " + portalAttributeValues.stream().map(attrib -> attrib.getAttributeValue()).collect(Collectors.toList()));
                        count.getAndIncrement();
                    }
                }
            }
        });
        return count.get();
    }

    int checkAllowedValues(List<ValidationDTO> checks, List<Map<?, ?>> mappings, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(f -> {
            for (int i = 0; i < mappings.size(); i++) {
                Map<?, ?> mapping = mappings.get(i);
                String[] fieldParts = f.split("\\|");
                String fieldValue = (String) mapping.get(fieldParts[0]);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    String[] allowedValues = fieldParts[1].split(",");
                    List<String> allowedValuesList = Arrays.stream(allowedValues).map(v -> v.trim()).collect(Collectors.toList());
                    if (!allowedValuesList.contains(fieldValue)) {
                        addParseError(checks, i, mappings.get(i), fieldParts[0] + " must be selected from " + allowedValuesList);
                        count.getAndIncrement();
                    }
                }
            }
        });
        return count.get();
    }

    public int checkIsValidPhone(List<ValidationDTO> checks, List<Map> mappings, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(f -> {

            for (int i = 0; i < mappings.size(); i++) {
                Map<?, ?> mapping = mappings.get(i);
                String fieldValue = (String) mapping.get(f);
                if (fieldValue != null && !fieldValue.isEmpty() && !com.solar.api.helper.ValidationUtils.isValidPhoneNumber(fieldValue)) {
                    addParseError(checks, i, mappings.get(i), f + " must be valid phone Number");
                    count.getAndIncrement();
                }
            }
        });
        return count.get();
    }

    public int checkIsValidCountryCode(List<ValidationDTO> checks, List<Map> mappings, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(f -> {

            for (int i = 0; i < mappings.size(); i++) {
                Map<?, ?> mapping = mappings.get(i);
                String fieldValue = (String) mapping.get(f);
                if (fieldValue != null && !fieldValue.isEmpty() && !com.solar.api.helper.ValidationUtils.isValidCountryCode(fieldValue)) {
                    addParseError(checks, i, mappings.get(i), f + " must be valid country Code");
                    count.getAndIncrement();
                }
            }
        });
        return count.get();
    }

    public int checkIsValidEmailField(List<ValidationDTO> checks, List<Map> mappings, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(f -> {

            for (int i = 0; i < mappings.size(); i++) {
                Map<?, ?> mapping = mappings.get(i);
                String fieldValue = (String) mapping.get(f);
                        if (fieldValue != null && !fieldValue.isEmpty() && !com.solar.api.helper.ValidationUtils.isValidEmail(fieldValue)) {
                            addParseError(checks, i, mappings.get(i), f + " must be valid email");
                            count.getAndIncrement();
                }
            }
        });
        return count.get();
    }

    int checkIsValidLeadType(List<ValidationDTO> checks, List<Map<?, ?>> mappings, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(f -> {
            for (int i = 0; i < mappings.size(); i++) {
                Map<?, ?> mapping = mappings.get(i);
                String fieldValue = (String) mapping.get(f);
                switch (f) {
                    case "lead_type":
                        Optional<String> leadType = null;
                        List<PortalAttributeValueTenantDTO> portalAttrValuesList = portalAttributeOverrideService.findByPortalAttributeName(Constants.CUSTOMER_ACQ.LEAD_TYPE);
                        leadType = portalAttrValuesList.stream().filter(portalAttr -> portalAttr.getAttributeValue().equalsIgnoreCase(fieldValue)).map(PortalAttributeValueTenantDTO::getAttributeValue).findFirst();
                        if (fieldValue != null && !fieldValue.isEmpty() && !leadType.isPresent()) {
                            addParseError(checks, i, mappings.get(i),
                                    f + " must be a valid lead type");
                            count.getAndIncrement();
                        }
                }
            }
        });
        return count.get();
    }

    int checkYearIsValid(List<ValidationDTO> checks, List<Map<?, ?>> mappings) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String year = (String) mapping.get("years");
            if (year != null && !year.isEmpty()) {
                try {
                    Year.parse(year);
                } catch (DateTimeParseException e) {
                    addParseError(checks, i, mappings.get(i), "Year not in correct format " + year);
                    count++;
                }
            }
        }
        return count;
    }

    int checkMonthYearIsValid(List<ValidationDTO> checks, List<Map<?, ?>> mappings) {
        int count = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-uuuu")
                .withResolverStyle(ResolverStyle.STRICT);
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String monthYear = (String) mapping.get("months");
            if (monthYear != null && !monthYear.isEmpty()) {
                try {
                    YearMonth.parse(monthYear, formatter);
                } catch (DateTimeParseException e) {
                    addParseError(checks, i, mappings.get(i), "Month-Year not in correct format " + monthYear);
                    count++;
                }
            }
        }
        return count;
    }

    int checkDayMonthYearIsValid(List<ValidationDTO> checks, List<Map<?, ?>> mappings) {
        int count = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-uuuu")
                .withResolverStyle(ResolverStyle.STRICT);
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String dayMonthYear = (String) mapping.get("days");
            if (dayMonthYear != null && !dayMonthYear.isEmpty()) {
                try {
                    LocalDate.parse(dayMonthYear, formatter);
                } catch (DateTimeParseException e) {
                    addParseError(checks, i, mappings.get(i), "Day-Month-Year not in correct format " + dayMonthYear);
                    count++;
                }
            }
        }
        return count;
    }

    int checkYearQuarterIsValid(List<ValidationDTO> checks, List<Map<?, ?>> mappings) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String yearQuarter = (String) mapping.get("years");
            if (yearQuarter != null && !yearQuarter.isEmpty()) {
                String[] parts = yearQuarter.split("-Q");
                if (parts.length == 2) {
                    try {
                        Year.parse(parts[0]);
                        int quarter = Integer.parseInt(parts[1]);
                        if (quarter < 1 || quarter > 4) {
                            throw new DateTimeParseException("Invalid quarter", yearQuarter, 0);
                        }
                    } catch (DateTimeParseException e) {
                        addParseError(checks, i, mappings.get(i), "Year-Quarter not in correct format " + yearQuarter);
                        count++;
                    }
                } else {
                    addParseError(checks, i, mappings.get(i), "Year-Quarter not in correct format " + yearQuarter);
                    count++;
                }
            }
        }
        return count;
    }

    int checkProjectedFieldIsValid(List<ValidationDTO> checks, List<Map<?, ?>> mappings) {
        int count = 0;
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?"); // the regex pattern to check if it is a number

        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String projected = (String) mapping.get("projected");
            if (projected != null && !projected.isEmpty()) {
                if (!pattern.matcher(projected).matches()) { // check if the projected value is a number
                    addParseError(checks, i, mappings.get(i), "Projected field is not numeric " + projected);
                    count++;
                }
            }
        }
        return count;
    }

    public int checkMultipleDBOccurrences(List<ValidationDTO> checks, List<Map> mappings, String field) {
        int count = 0;
        if (mappings.stream().filter(m -> m.get(field) != null).findAny().isPresent()) {
            List<?> fieldValuesInFile =
                    mappings.stream().map(m -> m.get(field)).collect(Collectors.toList());
            if (!fieldValuesInFile.isEmpty()) {
                for (int i = 0; i < mappings.size(); i++) {
                    String fieldValue = (String) mappings.get(i).get(field);
                    if (fieldValue != null && !fieldValue.isEmpty()) {
                        List<Entity> emailAddrExists = entityService.findByEmailAddressAndEntityTypeIn(fieldValue, ECustomerDetailStates.CUSTOMER.getName() ); // aaa  bbb ccc
                        if (emailAddrExists.size() == 0) {
                        }
                        else {
                            addParseError(checks, i, mappings.get(i),
                                    "Email Already exists in database;" + field + " " + fieldValue + " found in file");
                        }
                    }
                }
            }
        }
        return count;
    }
}
