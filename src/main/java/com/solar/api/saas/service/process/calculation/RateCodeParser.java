package com.solar.api.saas.service.process.calculation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.service.integration.mongo.response.subscription.MeasureType;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.integration.mongo.response.subscription.Variant;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDetailMapper;
import com.solar.api.tenant.mapper.billingCredits.BillingCreditResult;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.RateCodesTemp;
import com.solar.api.tenant.repository.BillingCreditsRepository;
import com.solar.api.tenant.service.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class RateCodeParser {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    protected RateFunctions rateFunctions;
    @Autowired
    private MatrixValueCalculation valueCalculation;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private BillingDetailService billingDetailService;
    @Autowired
    private BillingCreditsRepository billingCreditsRepository;
    @Autowired
    private RateCodesTempService rateCodesTempService;
    @Autowired
    private ExtDataStageDefinitionBillingService extDataStageDefinitionBillingService;

    public Map<String, Object> generateHashMap(CustomerSubscription subscription, Long subscriptionMatrixHeadId,
                                               BillingHead billingHead, Long jobId) {
        Map<String, Object> valuesHashMap = new HashMap<>();
        List<CustomerSubscriptionMapping> staticMappings =
                subscriptionService.getMappingsWithStaticValues(subscription, subscriptionMatrixHeadId);
        List<CustomerSubscriptionMapping> calculationMappings =
                subscriptionService.getMappingsForCalculationOrderedBySequence(subscription, subscriptionMatrixHeadId);
        LOGGER.info("\tStarted storing values in HashMap");
        LOGGER.info("\t\tStatic values:");
        staticMappings.stream().forEach(mapping -> {
            valuesHashMap.put(mapping.getRateCode(), mapping.getValue());
            LOGGER.info("\t\t\t" + mapping.getRateCode() + " = " + mapping.getValue());
        });
        // Add parsed dynamic values in map
        parseAndUpdateValues(calculationMappings, valuesHashMap, subscription.getId(), jobId);
        LOGGER.info("\tCompleted  storing values in HashMap");
        return valuesHashMap;
    }

    public Map<String, Object> storeRateCodes(CustomerSubscription subscription, Long subscriptionMatrixHeadId,
                                              BillingHead billingHead, Long jobId) {
        Map<String, Object> valuesHashMap = new HashMap<>();
        List<CustomerSubscriptionMapping> staticMappings =
                subscriptionService.getMappingsWithStaticValues(subscription, subscriptionMatrixHeadId);
        List<CustomerSubscriptionMapping> calculationMappings =
                subscriptionService.getMappingsForCalculationOrderedBySequence(subscription, subscriptionMatrixHeadId);
        LOGGER.info("\tStarted storing values in HashMap");
        LOGGER.info("\t\tStatic values:");
        RateCodesTemp rateCodesTemp = new RateCodesTemp();
        rateCodesTemp.setAccountTd(subscription.getUserAccount().getAcctId());
        rateCodesTemp.setSubscriptionId(subscription.getId());
        staticMappings.stream().forEach(mapping -> {
            if (mapping.getRateCode().equals("SN")) {
                rateCodesTemp.setSN(mapping.getValue());
            }
            if (mapping.getRateCode().equals("CN")) {
                rateCodesTemp.setCN(mapping.getValue());
            }
            if (mapping.getRateCode().equals("VCN")) {
                rateCodesTemp.setVCN(mapping.getValue());
            }
            if (mapping.getRateCode().equals("CCLAS")) {
                rateCodesTemp.setCCLAS(mapping.getValue());
            }
            if (mapping.getRateCode().equals("PN")) {
                rateCodesTemp.setPN(mapping.getValue());
            }
            if (mapping.getRateCode().equals("SADD")) {
                rateCodesTemp.setSADD(mapping.getValue());
            }
            if (mapping.getRateCode().equals("MP")) {
                rateCodesTemp.setMP(mapping.getValue());
            }
            if (mapping.getRateCode().equals("KWDC")) {
                rateCodesTemp.setKWDC(mapping.getValue());
            }
            if (mapping.getRateCode().equals("SSDT")) {
                rateCodesTemp.setSSDT(mapping.getValue());
            }
            if (mapping.getRateCode().equals("PSRC")) {
                rateCodesTemp.setPSRC(mapping.getValue());
            }
            if (mapping.getRateCode().equals("PCOMP")) {
                rateCodesTemp.setPCOMP(mapping.getValue());
            }
            if (mapping.getRateCode().equals("DSCP")) {
                rateCodesTemp.setDSCP(mapping.getValue());
            }
            if (mapping.getRateCode().equals("TENR")) {
                rateCodesTemp.setTENR(mapping.getValue());
            }
            if (mapping.getRateCode().equals("ROLL")) {
                rateCodesTemp.setROLL(mapping.getValue());
            }
            if (mapping.getRateCode().equals("CSGSDT")) {
                rateCodesTemp.setCSGSDT(mapping.getValue());
            }
            if (mapping.getRateCode().equals("DEP")) {
                rateCodesTemp.setDEP(mapping.getValue());
            }
            if (mapping.getRateCode().equals("GLCR")) {
                rateCodesTemp.setGLCR(mapping.getValue());
            }
            if (mapping.getRateCode().equals("GLDR")) {
                rateCodesTemp.setGLDR(mapping.getValue());
            }
            if (mapping.getRateCode().equals("GNSIZE")) {
                rateCodesTemp.setGNSIZE(mapping.getValue());
            }
            if (mapping.getRateCode().equals("GOWN")) {
                rateCodesTemp.setGOWN(mapping.getValue());
            }
            if (mapping.getRateCode().equals("SCSG")) {
                rateCodesTemp.setSCSG(mapping.getValue());
            }
            if (mapping.getRateCode().equals("SCSGN")) {
                rateCodesTemp.setSCSGN(mapping.getValue());
            }
            if (mapping.getRateCode().equals("SPGM")) {
                rateCodesTemp.setSPGM(mapping.getValue());
            }
            if (mapping.getRateCode().equals("UTCOMP")) {
                rateCodesTemp.setUTCOMP(mapping.getValue());
            }
            if (mapping.getRateCode().equals("PRJN")) {
                rateCodesTemp.setPRJN(mapping.getValue());
            }
            if (mapping.getRateCode().equals("DSC")) {
                rateCodesTemp.setDSC(mapping.getValue());
            }
            if (mapping.getRateCode().equals("SSYR")) {
                rateCodesTemp.setSSYR(mapping.getValue());
            }
            if (mapping.getRateCode().equals("VSDT")) {
                rateCodesTemp.setVSDT(mapping.getValue());
            }
            if (mapping.getRateCode().equals("VYR")) {
                rateCodesTemp.setVYR(mapping.getValue());
            }
            if (mapping.getRateCode().equals("SRTE")) {
                rateCodesTemp.setSRTE(mapping.getValue());
            }
            if (mapping.getRateCode().equals("FDAM")) {
                rateCodesTemp.setFDAM(mapping.getValue());
            }
            if (mapping.getRateCode().equals("OPYR")) {
                rateCodesTemp.setOPYR(mapping.getValue());
            }
            if (mapping.getRateCode().equals("TNR1")) {
                rateCodesTemp.setTNR1(mapping.getValue());
            }
            if (mapping.getRateCode().equals("DSCM")) {
                rateCodesTemp.setDSCM(mapping.getValue());
            }
            if (mapping.getRateCode().equals("ROLLDT")) {
                rateCodesTemp.setROLLDT(mapping.getValue());
            }
            if (mapping.getRateCode().equals("YLD")) {
                rateCodesTemp.setYLD(mapping.getValue());
            }
            valuesHashMap.put(mapping.getRateCode(), mapping.getValue());
            LOGGER.info("\t\t\t" + mapping.getRateCode() + " = " + mapping.getValue());
        });
        // Add parsed dynamic values in map
        parseAndUpdateValues(calculationMappings, valuesHashMap, subscription.getId(), jobId);
        rateCodesTempService.save(rateCodesTemp);
        LOGGER.info("\tCompleted  storing values in HashMap");
        return valuesHashMap;
    }

    protected void parseAndUpdateValues(List<CustomerSubscriptionMapping> calculationMappings,
                                        Map<String, Object> valuesHashMap, Long subscriptionId, Long jobId) {
        LOGGER.info("\t\tDynamic values:");
        calculationMappings.stream()
                .forEach(mapping -> {
                    try {
                        String[] groups = valueCalculation.getCalculatedValue(mapping.getValue());
                        if (groups.length > 1) {
                            String defaultValue = groups[1];
                            Object value = null;
                            if (valueCalculation.getValueType(defaultValue) == ERateMatrixValuePlaceholder.FUNCTION) {
                                String[] functionGroups = valueCalculation.getCalculatedValue(defaultValue);
                                String methodName = functionGroups[0].substring(0, functionGroups[0].indexOf("("));
                                Object paramValue = valuesHashMap.get(functionGroups[1]);
                                Object param = paramValue == null ? functionGroups[1] : paramValue;
                                LOGGER.info("\t\t\tmethodName = " + methodName);
                                LOGGER.info("\t\t\tparamValue = " + paramValue);
                                LOGGER.info("\t\t\tparam = " + param);
                                value = rateFunctions.functionAnalyzer(methodName, valuesHashMap, param);
                                LOGGER.info("\t\t\tvalue = " + value);
                                LOGGER.info("\t\t\t-----------------------------------");
                                valuesHashMap.put(mapping.getRateCode(), value);
                            } else if (valueCalculation.getValueType(defaultValue) == ERateMatrixValuePlaceholder.ARITHMETIC_EXPRESSION_ELEMENTS) {
                                String[] arithmeticGroups = valueCalculation.getCalculatedValue(defaultValue);
                                LOGGER.info("\t\t\tarithmeticGroups = " + Arrays.asList(arithmeticGroups));
                                LOGGER.info("\t\t\tmapping.getRateCode() = " + mapping.getRateCode());
                                LOGGER.info("\t\t\tmapping.getValue() = " + mapping.getValue());
                                value = rateFunctions.arithmeticExprAnalyzer(jobId, arithmeticGroups, valuesHashMap);
                                valuesHashMap.put(mapping.getRateCode(), value);
                                LOGGER.info("\t\t\tvalue = " + value);
                                LOGGER.info("\t\t\t-----------------------------------");
                            } else if (valueCalculation.getValueType(mapping.getValue()) == ERateMatrixValuePlaceholder.DERIVED) {
                                String[] derivedGgroups = valueCalculation.getCalculatedValue(mapping.getValue());
                                LOGGER.info("\t\t\tderivedGgroups = " + Arrays.asList(derivedGgroups));
                                value = rateFunctions.derivedValueAnalyzer(mapping, valuesHashMap, jobId,
                                        derivedGgroups);
                                valuesHashMap.put(mapping.getRateCode(), value);
                                LOGGER.info("\t\t\tvalue = " + value);
                                LOGGER.info("\t\t\t-----------------------------------");
                            }
                            LOGGER.info("\t\t\t" + mapping.getRateCode() + " = " + value + ", Placeholder = " + mapping.getValue());
                        } else {
                            valuesHashMap.put(mapping.getRateCode(), mapping.getValue());
                            LOGGER.info("\t\t\t" + mapping.getRateCode() + " = " + mapping.getValue());
                        }
                    } catch (Exception e) {
                        ObjectNode messageJson = new ObjectMapper().createObjectNode();
                        messageJson.put("job_id", jobId);
                        messageJson.put("subscription_id", subscriptionId);
                        messageJson.put("rate_code", mapping.getRateCode());
                        messageJson.put("value", mapping.getValue());
                        messageJson.put("message", e.getMessage());
                        LOGGER.error(messageJson.toPrettyString(), e);
                    }
                });
    }

    //TODO Copy this
    protected void parseAndUpdateValues(SubscriptionMapping subscriptionMapping, Map<String, Object> valuesHashMap, String subscriptionId,
                                        Long jobId) {
        LOGGER.info("\t\tDynamic values:");
        Subscription subscription = subscriptionMapping.getSubscription();
        Variant variant = subscriptionMapping.getVariant();
        List<MeasureType> measureTypes = new ArrayList<>();
        measureTypes.addAll(variant.getMeasures().getByProduct().stream().filter(m -> m.getLevel() == 1).collect(Collectors.toList()));
        measureTypes.addAll(subscription.getMeasures().getByCustomer().stream().filter(m -> m.getLevel() == 1).collect(Collectors.toList()));
        measureTypes.sort(Comparator.comparing(a -> a.getSeq()));
        measureTypes.stream()
                .forEach(mapping -> {
                    try {
                        String[] groups = valueCalculation.getCalculatedValue(mapping.getDefaultValue());
                        if (groups.length > 1) {
                            String defaultValue = groups[1];
                            Object value = null;
                            if (valueCalculation.getValueType(defaultValue) == ERateMatrixValuePlaceholder.FUNCTION) {
                                String[] functionGroups = valueCalculation.getCalculatedValue(defaultValue);
                                String methodName = functionGroups[0].substring(0, functionGroups[0].indexOf("("));
                                Object paramValue = valuesHashMap.get(functionGroups[1]);
                                Object param = paramValue == null ? functionGroups[1] : paramValue;
                                LOGGER.info("\t\t\tmethodName = " + methodName);
                                LOGGER.info("\t\t\tparamValue = " + paramValue);
                                LOGGER.info("\t\t\tparam = " + param);
                                value = rateFunctions.functionAnalyzer(methodName, valuesHashMap, param);
                                LOGGER.info("\t\t\tvalue = " + value);
                                LOGGER.info("\t\t\t-----------------------------------");
                                valuesHashMap.put(mapping.getCode(), value);
                            } else if (valueCalculation.getValueType(defaultValue) == ERateMatrixValuePlaceholder.ARITHMETIC_EXPRESSION_ELEMENTS) {
                                String[] arithmeticGroups = valueCalculation.getCalculatedValue(defaultValue);
                                LOGGER.info("\t\t\tarithmeticGroups = " + Arrays.asList(arithmeticGroups));
                                LOGGER.info("\t\t\tmapping.getRateCode() = " + mapping.getCode());
                                LOGGER.info("\t\t\tmapping.getValue() = " + mapping.getDefaultValue());
                                value = rateFunctions.arithmeticExprAnalyzer(jobId, arithmeticGroups, valuesHashMap);
                                valuesHashMap.put(mapping.getCode(), value);
                                LOGGER.info("\t\t\tvalue = " + value);
                                LOGGER.info("\t\t\t-----------------------------------");
                            } else if (valueCalculation.getValueType(mapping.getDefaultValue()) == ERateMatrixValuePlaceholder.DERIVED) {
                                String[] derivedGgroups = valueCalculation.getCalculatedValue(mapping.getDefaultValue());
                                LOGGER.info("\t\t\tderivedGgroups = " + Arrays.asList(derivedGgroups));
                                value = rateFunctions.derivedValueAnalyzer(valuesHashMap, jobId, derivedGgroups);
                                valuesHashMap.put(mapping.getCode(), value);
                                LOGGER.info("\t\t\tvalue = " + value);
                                LOGGER.info("\t\t\t-----------------------------------");
                            }
                            LOGGER.info("\t\t\t" + mapping.getCode() + " = " + value + ", Placeholder = " + mapping.getDefaultValue());
                        } else {
                            valuesHashMap.put(mapping.getCode(), mapping.getDefaultValue());
                            LOGGER.info("\t\t\t" + mapping.getCode() + " = " + mapping.getDefaultValue());
                        }
                    } catch (Exception e) {
                        ObjectNode messageJson = new ObjectMapper().createObjectNode();
                        messageJson.put("job_id", jobId);
                        messageJson.put("subscription_id", subscriptionId);
                        messageJson.put("rate_code", mapping.getCode());
                        messageJson.put("value", mapping.getDefaultValue());
                        messageJson.put("message", e.getMessage());
                        LOGGER.error(messageJson.toPrettyString(), e);
                    }
                });
    }

    protected void parseAndUpdateValues(List<TransStageTemp> transStageTempList, Map<String, Object> valuesHashMap, String subscriptionId,
                                        Long jobId) {
        LOGGER.info("\t\tDynamic values:");

        List<TransStageTemp> transStageTempListLvl1 = transStageTempList.stream().filter(m -> m.getLevel() == 1).collect(Collectors.toList());
        transStageTempListLvl1.sort(Comparator.comparing(a -> a.getSeqNo()));
        transStageTempListLvl1.stream()
                .forEach(mapping -> {
                    try {
                        String[] groups = valueCalculation.getCalculatedValue(mapping.getValue());
                        if (groups.length > 1) {
                            String defaultValue = groups[1];
                            Object value = null;
                            if (valueCalculation.getValueType(defaultValue) == ERateMatrixValuePlaceholder.FUNCTION) {
                                String[] functionGroups = valueCalculation.getCalculatedValue(defaultValue);
                                String methodName = functionGroups[0].substring(0, functionGroups[0].indexOf("("));
                                Object paramValue = valuesHashMap.get(functionGroups[1]);
                                Object param = paramValue == null ? functionGroups[1] : paramValue;
                                LOGGER.info("\t\t\tmethodName = " + methodName);
                                LOGGER.info("\t\t\tparamValue = " + paramValue);
                                LOGGER.info("\t\t\tparam = " + param);
                                value = rateFunctions.functionAnalyzer(methodName, valuesHashMap, param);
                                LOGGER.info("\t\t\tvalue = " + value);
                                LOGGER.info("\t\t\t-----------------------------------");
                                valuesHashMap.put(mapping.getMeasCode(), value);
                            } else if (valueCalculation.getValueType(defaultValue) == ERateMatrixValuePlaceholder.ARITHMETIC_EXPRESSION_ELEMENTS) {
                                String[] arithmeticGroups = valueCalculation.getCalculatedValue(defaultValue);
                                LOGGER.info("\t\t\tarithmeticGroups = " + Arrays.asList(arithmeticGroups));
                                LOGGER.info("\t\t\tmapping.getRateCode() = " + mapping.getMeasCode());
                                LOGGER.info("\t\t\tmapping.getValue() = " + mapping.getValue());
                                value = rateFunctions.arithmeticExprAnalyzer(jobId, arithmeticGroups, valuesHashMap);
                                valuesHashMap.put(mapping.getMeasCode(), value);
                                LOGGER.info("\t\t\tvalue = " + value);
                                LOGGER.info("\t\t\t-----------------------------------");
                            } else if (valueCalculation.getValueType(mapping.getValue()) == ERateMatrixValuePlaceholder.DERIVED) {
                                String[] derivedGgroups = valueCalculation.getCalculatedValue(mapping.getValue());
                                LOGGER.info("\t\t\tderivedGgroups = " + Arrays.asList(derivedGgroups));
                                value = rateFunctions.derivedValueAnalyzer(valuesHashMap, jobId, derivedGgroups);
                                valuesHashMap.put(mapping.getMeasCode(), value);
                                LOGGER.info("\t\t\tvalue = " + value);
                                LOGGER.info("\t\t\t-----------------------------------");
                            }
                            LOGGER.info("\t\t\t" + mapping.getMeasCode() + " = " + value + ", Placeholder = " + mapping.getValue());
                        } else {
                            valuesHashMap.put(mapping.getMeasCode(), mapping.getValue());
                            LOGGER.info("\t\t\t" + mapping.getMeasCode() + " = " + mapping.getValue());
                        }
                    } catch (Exception e) {
                        ObjectNode messageJson = new ObjectMapper().createObjectNode();
                        messageJson.put("job_id", jobId);
                        messageJson.put("subscription_id", subscriptionId);
                        messageJson.put("rate_code", mapping.getMeasCode());
                        messageJson.put("value", mapping.getValue());
                        messageJson.put("message", e.getMessage());
                        LOGGER.error(messageJson.toPrettyString(), e);
                    }
                });
    }

    protected BillingDetail getAbcreOrMpa(Map<String, Object> valuesHashMap, BillingHead billingHead, String code) {
        String convertedMonth = rateFunctions.parseMonthYearFormat(billingHead.getBillingMonthYear());
        int lineSequence = 0;
        BillingCreditResult abcreAndMpa = billingCreditsRepository.getABCREAndMPA((String) valuesHashMap.get("PN"),
                (String) valuesHashMap.get("SCSGN"), convertedMonth);
        if (abcreAndMpa == null) {
            abcreAndMpa = billingCreditsRepository.getABCREAndMPA(billingHead.getSubscriptionId(), convertedMonth);
        }
        BillingDetail mpaOrAbcreDetail = billingDetailService.findByBillingHeadAndBillingCode(billingHead, code);
        Double abcreOrMpa = mpaOrAbcreDetail != null ? mpaOrAbcreDetail.getValue() : 0d;
        LOGGER.info("\t\tAdding MPA and actual bill credit for bill id " + billingHead.getId());
        if (mpaOrAbcreDetail == null || abcreAndMpa != null) {
            if (abcreAndMpa != null) {
                abcreOrMpa = "MPA".equals(code) ? abcreAndMpa.getMPa() : abcreAndMpa.getCreditValueSum();
            }
            if (mpaOrAbcreDetail == null && abcreAndMpa == null) {
                LOGGER.warn("\t\t" + code + " not found for bill id " + billingHead.getId());
            }
            return getBillingDetail(billingHead,
                    null,
                    abcreOrMpa,
                    lineSequence,
                    code,
                    false);
        }
        return null;
    }

    protected BillingDetail getBillingDetail(BillingHead billingHead, String rateCode, double value, int lineSequence
            , String billingCode, boolean addToBillAmount) {
        BillingDetail billingDetailLine = BillingDetail.builder()
                .billingHead(billingHead)
                .rateCode(rateCode)
                .value(value)
                .lineSeqNo(lineSequence)
                .billingCode(billingCode) // from PortalAttribute
                .date(new Date())
                .addToBillAmount(addToBillAmount)
                .build();
        BillingDetail billingDetail = billingDetailService.findByBillingHeadAndBillingCode(billingHead, billingCode);
        if (billingDetail != null) {
            billingDetail = BillingDetailMapper.toUpdatedBillingDetail(billingDetail, billingDetailLine);
            return billingDetail;
        }
        return billingDetailLine;
    }
}
