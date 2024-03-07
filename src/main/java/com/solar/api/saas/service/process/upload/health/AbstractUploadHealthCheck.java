package com.solar.api.saas.service.process.upload.health;

import com.google.common.base.Joiner;
import com.solar.api.Constants;
import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.helper.Utility;
import com.solar.api.helper.ValidationUtils;
import com.solar.api.saas.service.process.upload.EUploadAction;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractUploadHealthCheck {

    @Autowired
    private UserService userService;
    @Autowired
    private PortalAttributeOverrideService portalAttributeOverrideService;

    @Autowired
    private EntityService entityService;

    abstract void addParseError(List<HealthCheck> checks, Integer line, Map<?, ?> mapping, String issue);

    abstract int checkRequiredFieldsInHeader(List<HealthCheck> checks, Set<?> fileHeader, String... fields);

    HealthCheckResult healthCheckResult(List<Map<?, ?>> mappings, List<HealthCheck> checks) {
        Set<Integer> checksRows = checks.stream().map(HealthCheck::getLine).collect(Collectors.toSet());
        List<Integer> checksRowsOriginal = checksRows.stream().map(r -> r - 2).collect(Collectors.toList());
        List<Integer> correctRowIds = IntStream.range(0, mappings.size())
                .boxed()
                .filter(i -> !checksRowsOriginal.contains(i))
                .collect(Collectors.toList());
        return HealthCheckResult.builder()
                .totalRows(mappings.size())
                .totalCorrectRows(correctRowIds.size())
                .correctRowIds(Joiner.on(", ").join(correctRowIds))
                .healthChecks(checks)
                .build();
    }

    int checkMandatoryFieldsNotEmpty(List<HealthCheck> checks, List<Map<?, ?>> mappings, String... fields) {
        List<String> emptyFieldsList = new ArrayList<>();
        for (int i = 0; i < mappings.size(); i++) {
            List<String> emptyFields = mappings.get(i).entrySet().stream()
                    .filter(m -> ((String) m.getValue()).isEmpty() && Arrays.asList(fields).contains(((String) m.getKey())))
                    .map(m -> (String) m.getKey()).collect(Collectors.toList());
            if (!emptyFields.isEmpty()) {
                addParseError(checks, i, mappings.get(i),
                        Joiner.on(", ").join(emptyFields) + (emptyFields.size() == 1 ? " is" : " are") + " mandatory");
                emptyFieldsList.addAll(emptyFields);
            }
        }
        return emptyFieldsList.size();
    }

    int checkActions(List<HealthCheck> checks, List<Map<?, ?>> mappings, String primaryKeyField) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String primaryKey = (String) mapping.get(primaryKeyField);
            String action = (String) mapping.get("action");
            if (action != null && action.equals(EUploadAction.INSERT.getAction()) && primaryKey != null && !primaryKey.isEmpty()) {
                addParseError(checks, i, mappings.get(i),
                        primaryKeyField + " must be empty for action " + EUploadAction.INSERT);
                count++;
            } else if (action != null && action.equals(EUploadAction.UPDATE.getAction()) && primaryKey != null && primaryKey.isEmpty()) {
                addParseError(checks, i, mappings.get(i),
                        primaryKeyField + " must be present for action " + EUploadAction.UPDATE);
                count++;
            } else if (action != null && !action.isEmpty() && !EUploadAction.getActions().contains(action)) {
                addParseError(checks, i, mappings.get(i), "action must be selected from " + EUploadAction.getActions());
                count++;
            }
        }
        return count;
    }

    int checkUserIsValid(List<HealthCheck> checks, List<Map<?, ?>> mappings) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String acctId = (String) mapping.get("acct_id");
            if (acctId != null && !acctId.isEmpty()) {
                User user = userService.findByIdNoThrow(Long.parseLong(acctId));
                if (user == null) {
                    addParseError(checks, i, mappings.get(i), "Customer account not found with acct_id " + acctId);
                    count++;
                }
            }
        }
        return count;
    }
    int checkDateFormatIsValid(List<HealthCheck> checks, List<Map<?, ?>> mappings) {
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
    int checkCustomFieldFormats(List<HealthCheck> checks, List<Map<?, ?>> mappings, String... fieldsFormat) {
        int count = 0;
        for (String ff : Arrays.asList(fieldsFormat)) {
            for (int i = 0; i < mappings.size(); i++) {
                Map<?, ?> mapping = mappings.get(i);
                String[] fieldAndFormat = ff.split("\\|");
                String field = fieldAndFormat[0];
                String fieldValue = (String) mapping.get(field);
                switch (fieldAndFormat[1]) {
                    case "NUMBER":
                        if (fieldValue != null && !fieldValue.isEmpty() && !ValidationUtils.isNumeric(fieldValue)) {
                            addParseError(checks, i, mappings.get(i), field + " must be numeric");
                            count++;
                        }
                        break;
                    case "DATE":
                        if (fieldValue != null && !fieldValue.isEmpty() && !ValidationUtils.isValidDate(fieldValue,
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

    int checkMultipleOccurrences(List<HealthCheck> checks, List<Map<?, ?>> mappings, String field) {
        int count = 0;
        if (mappings.stream().filter(m -> m.get(field) != null).findAny().isPresent()) {
            List<?> fieldValuesInFile =
                    mappings.stream().filter(m -> m.get("action").equals(EUploadAction.INSERT.getAction())).map(m -> m.get(field)).collect(Collectors.toList());
            if (!fieldValuesInFile.isEmpty()) {
                for (int i = 0; i < mappings.size(); i++) {
                    if (!mappings.get(i).get("action").equals(EUploadAction.INSERT.getAction())) {
                        continue;
                    }
                    String fieldValue = (String) mappings.get(i).get(field);
                    if (fieldValue != null && !fieldValue.isEmpty()) {
                        int frequency = Collections.frequency(fieldValuesInFile, mappings.get(i).get(field));
                        if (frequency > 1) {
                            addParseError(checks, i, mappings.get(i),
                                    "Multiple occurrences of " + field + " " + fieldValue + " for action " + EUploadAction.INSERT.getAction() + " found in file");
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    int checkValuesFromPortalAttribute(List<HealthCheck> checks, List<Map<?, ?>> mappings, String... fields) {
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

    int checkAllowedValues(List<HealthCheck> checks, List<Map<?, ?>> mappings, String... fields) {
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

    int checkIsValidCountryCodeAndPhoneAndEmail(List<HealthCheck> checks, List<Map<?, ?>> mappings, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(f -> {

            for (int i = 0; i < mappings.size(); i++) {
                Map<?, ?> mapping = mappings.get(i);
                String fieldValue = (String) mapping.get(f);
                switch (f) {
                    case "phone_number":
                        if (fieldValue != null && !fieldValue.isEmpty() && !ValidationUtils.isValidPhoneNumber(fieldValue)) {
                            addParseError(checks, i, mappings.get(i), f + " must be valid phone Number");
                            count.getAndIncrement();
                        }
                        break;
                    case "country_code":
                        if (fieldValue != null && !fieldValue.isEmpty() && !ValidationUtils.isValidCountryCode(fieldValue)) {
                            addParseError(checks, i, mappings.get(i), f + " must be valid country Code");
                            count.getAndIncrement();
                        }
                        break;
                    case "email":
                        if (fieldValue != null && !fieldValue.isEmpty() && !ValidationUtils.isValidEmail(fieldValue)) {
                            addParseError(checks, i, mappings.get(i),
                                    f + " must be a valid email");
                            count.getAndIncrement();
                        } else if (fieldValue != null && !fieldValue.isEmpty() && (entityService.findByEmailAddressAndEntityType(fieldValue, "Customer") != null)) {
                            addParseError(checks, i, mappings.get(i),
                                    f + " already used");
                            count.getAndIncrement();
                        }
                }
            }
        });
        return count.get();
    }

    int checkIsValidLeadType(List<HealthCheck> checks, List<Map<?, ?>> mappings, String... fields) {
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

    int checkYearIsValid(List<HealthCheck> checks, List<Map<?, ?>> mappings) {
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

    int checkMonthYearIsValid(List<HealthCheck> checks, List<Map<?, ?>> mappings) {
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

    int checkDayMonthYearIsValid(List<HealthCheck> checks, List<Map<?, ?>> mappings) {
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

    int checkYearQuarterIsValid(List<HealthCheck> checks, List<Map<?, ?>> mappings) {
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

    int checkProjectedFieldIsValid(List<HealthCheck> checks, List<Map<?, ?>> mappings) {
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
}
