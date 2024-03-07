package com.solar.api.saas.service.process.calculation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRatesDerived;
import com.solar.api.tenant.repository.CustomerSubscriptionMappingRepository;
import com.solar.api.tenant.repository.SubscriptionRatesDerivedRepository;
import com.solar.api.tenant.service.MatrixValueCalculation;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.solar.api.saas.service.process.calculation.ERateMatrixValuePlaceholder.DYNAMIC;

@Component
public class RateFunctions {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private CustomerSubscriptionMappingRepository mappingRepository;
    @Autowired
    private SubscriptionRatesDerivedRepository subscriptionRatesDerivedRepository;
    @Autowired
    private MatrixValueCalculation valueCalculation;

    // ACTVDATE(SSDT)
    LocalDate firstDayOfActivationMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    // OPMH
    int currentOperatingMonth(LocalDate date) {
        LocalDate now = LocalDate.now();
        return Months.monthsBetween(firstDayOfActivationMonth(date), now).getMonths() + 1;
    }

    // starting from date of operation (year changes in December). Used in billing function
    public Integer currentCalendarYear(LocalDate date, String monthYear) {
//        String gardenStartDate = date.toString(DateTimeFormat.forPattern("yyyy-MM-dd"));
        return parseMonthYear(monthYear).getYear() - date.getYear() + 1;
//        return Years.yearsBetween(date, parseMonthYear(monthYear)).getYears() + 1;
    }

    // starting from date of operation (a full year of twelve months)
    public int operatingYear(LocalDate date) {
        LocalDate now = LocalDate.now();
        return Years.yearsBetween(date, now).getYears() + 1;
    }

    // NOW()
    public LocalDate now() {
        return systemDate();
    }

    public LocalDate systemDate() {
        return LocalDate.now();
    }

    public String getMonthYear(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM-yyyy");
        return date.toString(formatter);
    }

    public LocalDate parseMonthYear(String str) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM-yyyy");
        return formatter.parseLocalDate(str);
    }

    public LocalDate parseDateFormat(String format, String dateString) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        return formatter.parseLocalDate(dateString);
    }

    public int monthsDifference(String date) {
        // Get months between these dates.
        int years = 0;
        // Using LocalDate object.
        LocalDate date1 = LocalDate.parse(date);
        LocalDate date2 = LocalDate.now();
        years = Years.yearsBetween(date1, date2).getYears();
        return years;
    }

    public String parseMonthYearFormat(String dateString) {
        // Format for input
        DateTimeFormatter inputFormat = DateTimeFormat.forPattern("MM-yyyy");
        // Parsing the date
        DateTime dateParsed = inputFormat.parseDateTime(dateString);
        // Format for output
        DateTimeFormatter outputFormat = DateTimeFormat.forPattern("yyyy-MM");
        // Printing the date
        return outputFormat.print(dateParsed);
    }

    public String getSSDT(CustomerSubscription subscription) {
        CustomerSubscriptionMapping mappingSSDT =
                mappingRepository.findByRateCodeAndSubscription("SSDT", subscription);
        if (mappingSSDT == null) {
            throw new NotFoundException(CustomerSubscriptionMapping.class, "rateCode", "SSDT");
        }
        return mappingSSDT.getValue();
    }

    public Object functionAnalyzer(String functionAlias, Map<String, Object> valuesHashMap, Object... args) {
        if ("NOW".equals(functionAlias)) {
            return systemDate();
        } else if ("CURRENT_CALENDAR_YEAR".equals(functionAlias)) {
            String value = null;
            if ((valuesHashMap.get(((String) args[0]).split(",")[0])).toString().contains("T")) {
                value = (String) (valuesHashMap.get(((String) args[0]).split(",")[0])).toString().split("T")[0];
            } else {
                value = (String) (valuesHashMap.get(((String) args[0]).split(",")[0]));
            }
            Integer x = currentCalendarYear(
                    parseDateFormat("yyyy-MM-dd", value),
                    (String) (valuesHashMap.get(((String) args[0]).split(",")[1]))
            );
            return x;
        } else if ("OPERATING_YEAR".equals(functionAlias)) {
            return operatingYear(parseDateFormat("yyyy-MM-dd", (String) args[0]));
        } else if ("CURRENT_OPERATING_MONTH".equals(functionAlias)) {
            return currentOperatingMonth(parseDateFormat("yyyy-MM-dd", (String) args[0]));
        } else if ("ACTVDATE".equals(functionAlias)) {
            return firstDayOfActivationMonth(parseDateFormat("yyyy-MM-dd", (String) args[0]));
        } else if ("RATECODEADD".equals(functionAlias)) {
            return Arrays.asList(((String) args[0]).split(",")).stream().mapToDouble(m -> {
                Object val = valuesHashMap.get(m);
                if (val instanceof String) {
                    return Double.parseDouble((String) val);
                }
                return (double) val;
            }).sum();
        } else if ("SLAB".equals(functionAlias)) {
            Double measureParam = Double.parseDouble(((String) args[0]).trim());
            Double lowerLimit = Double.parseDouble(((String) args[1]).trim());
            Double upperLimit = Double.parseDouble(((String) args[2]).trim());
            return measureParam != null && measureParam >= lowerLimit && measureParam < upperLimit;
        } else if ("EXP_COMPUTE".equals(functionAlias)) {
            String addr = (valuesHashMap.get(((String) args[0]).split(",")[0])).toString();
            String opyr = (valuesHashMap.get(((String) args[0]).split(",")[1])).toString();
            Double varOpyr = (opyr != null && !opyr.trim().isEmpty()) ? Double.parseDouble(opyr) : null;
            Double varAddr = (addr != null && !addr.trim().isEmpty()) ? Double.parseDouble(addr) : null;
            return (varOpyr != null && varAddr != null )? Math.pow(varAddr, varOpyr) : 1;
        }
        return null;
    }

    public Object derivedValueAnalyzer(CustomerSubscriptionMapping mapping, Map<String, Object> valuesHashMap,
                                       Long jobId, Object... groups) {
        String calcGroup = (String) groups[1];
        String[] conditionParts = new String[0];
        try {
            SubscriptionRatesDerived derived = conditionExprEvaluator(mapping, calcGroup, valuesHashMap);
            if (derived != null) {
                return derived.getValue();
            } else {
                LOGGER.warn("SubscriptionRatesDerived not found with calcGroup  " + calcGroup);
            }
        } catch (Exception e) {
            LOGGER.error(calcGroup
                    + "\n" + conditionParts[0] + "=" + valuesHashMap.get(conditionParts[0])
                    + "\n" + e.getMessage(), e);

            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("job_id", jobId);
            messageJson.put("expression", conditionParts[0] + "=" + valuesHashMap.get(conditionParts[0]));
            LOGGER.error(messageJson.toPrettyString(), e);
        }
        return null;
    }

    private SubscriptionRatesDerived conditionExprEvaluator(CustomerSubscriptionMapping mapping,
                                                            String calcGroup,
                                                            Map<String, Object> valuesHashMap) {
        List<SubscriptionRatesDerived> subscriptionRatesDerivedRecords =
                subscriptionRatesDerivedRepository.findBySubscriptionRateMatrixIdAndSubscriptionCodeAndCalcGroup(
                        mapping.getSubscriptionRateMatrixHead().getId(),
                        mapping.getSubscriptionRateMatrixHead().getSubscriptionCode(),
                        calcGroup);
        if (subscriptionRatesDerivedRecords.isEmpty()) {
            subscriptionRatesDerivedRecords =
                    subscriptionRatesDerivedRepository.findBySubscriptionCodeAndCalcGroup(
                            mapping.getSubscriptionRateMatrixHead().getSubscriptionCode(),
                            calcGroup);
        }
        List<SubscriptionRatesDerived> derivedDynamic = subscriptionRatesDerivedRecords.stream()
                .filter(derived -> (ERateMatrixValuePlaceholder.isDynamic(derived.getConditionExpr()))).collect(Collectors.toList());
        if (!derivedDynamic.isEmpty()) {
            return derivedDynamic.stream().filter(derived -> {
                String[] groups = DYNAMIC.getGroups(derived.getConditionExpr());
                String methodName = groups[1].substring(0, groups[1].indexOf("("));
                String[] params = ERateMatrixValuePlaceholder.PARENTHESES_CONTENT
                        .getGroups(groups[1].substring(groups[1].indexOf("(")))[1].split(",");
                String value = valuesHashMap.get(params[0]) instanceof Double ?
                        String.valueOf(valuesHashMap.get(params[0])) : (String) valuesHashMap.get(params[0]);
                if (value == null) {
                    return false;
                }
                params[0] = value;
                return (boolean) functionAnalyzer(methodName, valuesHashMap, params);
            }).findFirst().orElse(null);
        }
        if (!subscriptionRatesDerivedRecords.isEmpty()) {
            if (!StringUtils.isBlank(subscriptionRatesDerivedRecords.get(0).getConditionExpr())) {
                String[] conditionParts = subscriptionRatesDerivedRecords.get(0).getConditionExpr().split("=");
                return subscriptionRatesDerivedRecords
                        .stream()
                        .filter(record -> record.getConditionExpr().equals(conditionParts[0] + "=" + valuesHashMap.get(conditionParts[0])))
                        .findAny().orElse(null);
            } else {
                return subscriptionRatesDerivedRecords
                        .stream()
                        .findAny().orElse(null);
            }
        } else {
            LOGGER.warn("SubscriptionRatesDerived not found with calcGroup  " + calcGroup);
        }
        return null;
    }

    public Object derivedValueAnalyzer(Map<String, Object> valuesHashMap, Long jobId, Object... groups) {
        String calcGroup = (String) groups[1];
        String[] conditionParts = new String[0];
        try {
            SubscriptionRatesDerived derived = conditionExprEvaluator((String) groups[1], valuesHashMap);
            if (derived != null) {
                return derived.getValue();
            } else {
                LOGGER.warn("SubscriptionRatesDerived not found with calcGroup  " + calcGroup);
            }
        } catch (Exception e) {
            LOGGER.error(calcGroup
                    + "\n" + conditionParts[0] + "=" + valuesHashMap.get(conditionParts[0])
                    + "\n" + e.getMessage(), e);

            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("job_id", jobId);
            messageJson.put("expression", conditionParts[0] + "=" + valuesHashMap.get(conditionParts[0]));
            LOGGER.error(messageJson.toPrettyString(), e);
        }
        return null;
    }

    private SubscriptionRatesDerived conditionExprEvaluator(String calcGroup, Map<String, Object> valuesHashMap) {
        List<SubscriptionRatesDerived> subscriptionRatesDerivedRecords =
                subscriptionRatesDerivedRepository.findByCalcGroup(calcGroup);
        List<SubscriptionRatesDerived> derivedDynamic = subscriptionRatesDerivedRecords.stream()
                .filter(derived -> (ERateMatrixValuePlaceholder.isDynamic(derived.getConditionExpr()))).collect(Collectors.toList());
        if (!derivedDynamic.isEmpty()) {
            return derivedDynamic.stream().filter(derived -> {
                String[] groups = DYNAMIC.getGroups(derived.getConditionExpr());
                String methodName = groups[1].substring(0, groups[1].indexOf("("));
                String[] params = ERateMatrixValuePlaceholder.PARENTHESES_CONTENT
                        .getGroups(groups[1].substring(groups[1].indexOf("(")))[1].split(",");
                String value = valuesHashMap.get(params[0]) instanceof Double ?
                        String.valueOf(valuesHashMap.get(params[0])) : (String) valuesHashMap.get(params[0]);
                if (value == null) {
                    return false;
                }
                params[0] = value;
                return (boolean) functionAnalyzer(methodName, valuesHashMap, params);
            }).findFirst().orElse(null);
        }
        if (!subscriptionRatesDerivedRecords.isEmpty()) {
            if (!StringUtils.isBlank(subscriptionRatesDerivedRecords.get(0).getConditionExpr())) {
                String[] conditionParts = subscriptionRatesDerivedRecords.get(0).getConditionExpr().split("=");
                return subscriptionRatesDerivedRecords
                        .stream()
                        .filter(record -> record.getConditionExpr().equals(conditionParts[0] + "=" + valuesHashMap.get(conditionParts[0])))
                        .findAny().orElse(null);
            } else {
                return subscriptionRatesDerivedRecords
                        .stream()
                        .findAny().orElse(null);
            }
        } else {
            LOGGER.warn("SubscriptionRatesDerived not found with calcGroup  " + calcGroup);
        }
        return null;
    }

    public Object arithmeticExprAnalyzer(Long jobId, Object... args) {
        String[] groups = (String[]) args[0];
        Map<String, Object> valuesHashMap = (Map<String, Object>) args[1];
        Object result = null;
        String firstOperand = null;
        String secondOperand = null;
        Double firstOperandValue = null;
        String operator = null;
        Double secondOperandValue = null;
        try {
            firstOperand = groups[0];
            operator = groups[1];
            secondOperand = groups[2];
            firstOperandValue = getOperandValue(firstOperand, valuesHashMap);
            secondOperandValue = getOperandValue(secondOperand, valuesHashMap);
            if (firstOperandValue != null && secondOperandValue != null) {
                result = getSimpleArthValue(firstOperandValue, operator, secondOperandValue);
            }
            for (int i = 3; i < groups.length - 1; i += 2) {
                Double operand = getOperandValue(groups[i + 1], valuesHashMap);
                result = getSimpleArthValue((Double) result, groups[i], operand);
            }
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("job_id", jobId);
            messageJson.put(firstOperand, firstOperandValue);
            messageJson.put("operator", operator);
            messageJson.put(secondOperand, secondOperandValue);
            LOGGER.error(messageJson.toPrettyString(), e);
        }
        return result;
    }

    private Object getSimpleArthValue(Double firstOperandValue, String operator, Double secondOperandValue) {
        if (firstOperandValue != null && secondOperandValue != null) {
            if ("+".equals(operator)) {
                return firstOperandValue + secondOperandValue;
            } else if ("-".equals(operator)) {
                return firstOperandValue - secondOperandValue;
            } else if ("*".equals(operator)) {
                return firstOperandValue * secondOperandValue;
            } else if ("/".equals(operator)) {
                return firstOperandValue / secondOperandValue;
            }
        }
        return null;
    }

    private Double getOperandValue(String operand, Map<String, Object> valuesHashMap) {
        String[] powerOperandParts = operand.split("\\^");
        if (powerOperandParts.length > 1) {
            String exponent = powerOperandParts[1];
            Integer exponentValue = null;
            Double base = null;
            if (StringUtils.isNumeric(exponent)) {
                exponentValue = Integer.valueOf(exponent);
            } else {
                exponentValue = Integer.valueOf((String) valuesHashMap.get(exponent));
            }
            base = (Double) valuesHashMap.get(powerOperandParts[0]);
            return Math.pow(base, exponentValue);
        }
        if (StringUtils.isNumeric(operand)) {
            return Double.valueOf(operand);
        } else {
            if (valuesHashMap.get(operand).getClass() == String.class) {
                return Double.valueOf((String) valuesHashMap.get(operand));
            } else if (valuesHashMap.get(operand).getClass() == LocalDate.class) {
                return (double) ((LocalDate) valuesHashMap.get(operand)).toDate().getTime();
            } else {
                return (Double) valuesHashMap.get(operand);
            }
        }
    }

}
