package com.solar.api.saas.service.widget;

import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.mapper.widget.chart.ChartMapper;
import com.solar.api.saas.mapper.widget.chart.ChartResponse;
import com.solar.api.saas.mapper.widget.chart.DataSet;
import com.solar.api.saas.model.chart.views.BillingByCodeView;
import com.solar.api.saas.model.widget.chart.ChartDetail;
import com.solar.api.saas.model.widget.chart.ChartHead;
import com.solar.api.saas.repository.BillingDetailViewRepository;
import com.solar.api.saas.repository.ChartDetailRepository;
import com.solar.api.saas.repository.ChartHeadRepository;
import com.solar.api.saas.service.process.calculation.ERateMatrixValuePlaceholder;
import com.solar.api.saas.service.process.calculation.RateFunctions;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.repository.CustomerSubscriptionMappingRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.service.MatrixValueCalculation;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
//@Transactional("masterTransactionManager")
public class ChartServiceImpl implements ChartService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;
    @Autowired
    private CustomerSubscriptionMappingRepository subscriptionMappingRepository;
    @Autowired
    private BillingDetailViewRepository billingDetailViewRepository;
    @Autowired
    private RateFunctions rateFunctions;
    @Autowired
    private ChartHeadRepository chartHeadRepository;
    @Autowired
    private ChartDetailRepository chartDetailRepository;
    @Autowired
    private MatrixValueCalculation valueCalculation;
    @Autowired
    private Utility utility;
    protected Map<String, Object> valuesHashMap = new HashMap<>();

    // ChartHead ////////////////////////////////////////////////
    @Override
    public ChartHead addOrUpdate(ChartHead chartHead) {
        if (chartHead.getChartId() != null) {
            ChartHead chartHeadDb = findById(chartHead.getChartId());
            chartHead = ChartMapper.toUpdatedChartHead(chartHead, chartHeadDb);
        }
        return chartHeadRepository.save(chartHead);
    }

    @Override
    public ChartHead findById(Long id) {
        return chartHeadRepository.findById(id).orElseThrow(() -> new NotFoundException(ChartHead.class, id));
    }

    @Override
    public List<ChartHead> findAll() {
        return chartHeadRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        chartHeadRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        chartHeadRepository.deleteAll();
    }

    // ChartDetail ////////////////////////////////////////////////
    @Override
    public ChartDetail addOrUpdateChartDetail(ChartDetail chartDetail) {
        if (chartDetail.getLabelId() != null) {
            ChartDetail chartDetailDb = findChartDetailById(chartDetail.getLabelId());
            chartDetail = ChartMapper.toUpdatedChartDetail(chartDetail, chartDetailDb);
        }
        return chartDetailRepository.save(chartDetail);
    }

    @Override
    public ChartDetail findChartDetailById(Long id) {
        return chartDetailRepository.findById(id).orElseThrow(() -> new NotFoundException(ChartDetail.class, id));
    }

    @Override
    public List<ChartDetail> findAllChartDetails() {
        return chartDetailRepository.findAll();
    }

    @Override
    public void deleteChartDetail(Long id) {
        chartDetailRepository.deleteById(id);
    }

    @Override
    public void deleteAllChartDetails() {
        chartDetailRepository.deleteAll();
    }

    // Chart API ////////////////////////////////////////////////
    @Override
    public ChartResponse getChartData(String widgetCode, Long accountId, Long subscriptionId) {
        switch (widgetCode) {
            case "ABCREvMBILL":
                return getABCREvMBILL(accountId, subscriptionId);
            case "MPA":
                return getMPA(accountId, subscriptionId);
            case "PSAV":
                return getCumulativeSavings(accountId, subscriptionId);
        }
        return ChartResponse.builder().build();
    }

    // ABCRE VS MBILL AS BAR CHART
    private ChartResponse getABCREvMBILL(Long accountId, Long subscriptionId) {
        String toDate = rateFunctions.parseDateFormat("yyyy-MM-dd", LocalDate.now().toString()).toString();
        LocalDate date2 = rateFunctions.parseDateFormat("yyyy-MM-dd", toDate);
        LocalDate date1 = date2.minusMonths(11);
        String fromDate = rateFunctions.parseDateFormat("yyyy-MM-dd", date1.toString()).toString();
        List<BillingByCodeView> abcreResponse =
                billingDetailViewRepository.billingByCodeAndDate(accountId, subscriptionId, "ABCRE", fromDate, toDate);
        List<BillingByCodeView> mbillResponse =
                billingDetailViewRepository.billingByCodeAndDate(accountId, subscriptionId, "MBILL", fromDate, toDate);
        int lastMonthCount = 12; //by default
        if (abcreResponse.size()!=0 && mbillResponse.size()!=0) {
            int abcreLastMonth = rateFunctions.parseDateFormat("yyyy-MM-dd",abcreResponse.get(abcreResponse.size()-1).getBillingMonthYear().toString()).getMonthOfYear();
            int mbillLastMonth = rateFunctions.parseDateFormat("yyyy-MM-dd",mbillResponse.get(mbillResponse.size()-1).getBillingMonthYear().toString()).getMonthOfYear();

            lastMonthCount = abcreLastMonth;

            if (mbillLastMonth > abcreLastMonth ) {
                lastMonthCount = mbillLastMonth;
            }
        }

        List<String> months = new ArrayList<>();
        for (int i = 0; i < lastMonthCount; i++) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM");
            String month = formatter.print(date1);
            date1 = date1.plusMonths(1);
            System.out.println(month);
            months.add(month);
        }
        List<Double> abcreValues = getValuesInMonths(abcreResponse, months);
        List<Double> mbillValues = getValuesInMonths(mbillResponse, months);
        return ChartResponse.builder()
                .labels(months)
                .dataSets(
                        Arrays.asList(
                                DataSet.builder()
                                        .label(attributeOverrideService.findByAttributeValue("ABCRE").getDescription())
                                        .data(abcreValues)
                                        .borderColor("rgba(217, 170, 8, 0.5)")
                                        .backgroundColor("rgba(217, 170, 8, 0.5)")
                                        .borderWidth(1)
                                        .build(),
                                DataSet.builder()
                                        .label(attributeOverrideService.findByAttributeValue("MBILL").getDescription())
                                        .data(mbillValues)
                                        .borderColor("rgba(31, 34, 51, 0.5)")
                                        .backgroundColor("rgba(31, 34, 51, 0.5)")
                                        .borderWidth(1)
                                        .build()
                        )
                )
                .retUri("http://example.com")
                .type("bar")
                .build();
    }

    /*private String getDataSetLabel(String code) {
        PortalAttributeValueTenantDTO attributeValueTenant = attributeOverrideService.findByAttributeValue(code);
        if (attributeValueTenant != null) {
            return attributeValueTenant.getDescription();
        } else {
            PortalAttributeValue attributeValue = portalAttributeService.findByAttributeValue(code);
            if (attributeValue != null) {
                return attributeValueTenant.getDescription();
            }
        }
        return null;
    }*/

    private List<Double> getValuesInMonths(List<BillingByCodeView> records, List<String> months) {
        List<Double> mbillValues = new ArrayList<>();
        IntStream.range(0, months.size()).forEach(i -> {
            Optional<BillingByCodeView> value =
                    records.stream().filter(response -> response.getMonth().equals(months.get(i))).findFirst();
            if (value.isPresent()) {
                mbillValues.add(value.get().getValue());
            } else {
                mbillValues.add(0.0);
            }
        });
        return mbillValues;
    }

    // MPA AS LINE CHART. For past 12 months.
    private ChartResponse getMPA(Long accountId, Long subscriptionId) {
        String toDate = rateFunctions.parseDateFormat("yyyy-MM-dd", LocalDate.now().toString()).toString();
        LocalDate date2 = rateFunctions.parseDateFormat("yyyy-MM-dd", toDate);
        LocalDate date1 = date2.minusMonths(11);
        String fromDate = rateFunctions.parseDateFormat("yyyy-MM-dd", date1.toString()).toString();
        List<BillingByCodeView> mbillResponse =
                billingDetailViewRepository.billingByCodeAndDate(accountId, subscriptionId, "MPA", fromDate, toDate);

        int lastMonthCount = 12; //by default
        if (mbillResponse.size()!=0) {
            int mpaLastMonth = rateFunctions.parseDateFormat("yyyy-MM-dd",mbillResponse.get(mbillResponse.size()-1).getBillingMonthYear().toString()).getMonthOfYear();
            lastMonthCount = mpaLastMonth;
        }

        List<String> months = new ArrayList<>();
        for (int i = 0; i < lastMonthCount; i++) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM");
            String month = formatter.print(date1);
            date1 = date1.plusMonths(1);
            System.out.println(month);
            months.add(month);
        }
        List<Double> mpaValues = getValuesInMonths(mbillResponse, months);
        return ChartResponse.builder()
                .labels(months)
                .dataSets(
                        Arrays.asList(
                                DataSet.builder()
                                        .label(attributeOverrideService.findByAttributeValue("MPA").getDescription())
                                        .data(mpaValues)
                                        .borderColor("rgba(31,34,51,1)")
                                        .borderWidth(1)
                                        .lineTension(0.2)
                                        .fill(false)
                                        .build()
                        )
                )
                .retUri("http://example.com")
                .type("line")
                .build();
    }

    // CumulativeSavings over future years AS LINE CHART. From January to December.
    private ChartResponse getCumulativeSavings(Long accountId, Long subscriptionId) {
        valuesHashMap.put("MNYR", LocalDate.now().getMonthOfYear() + "-" + LocalDate.now().getYear());
        CustomerSubscription subscription = customerSubscriptionRepository.findById(subscriptionId).orElse(null);
        generateHashMap(subscription, subscription.getSubscriptionRateMatrixId(), null);

        Double csum = 0d;
        List<Double> cumulativeValues = new ArrayList<>();
        List<String> years = new ArrayList<>();
        LocalDate now = LocalDate.now();
        Integer opyr = (Integer) valuesHashMap.get("OPYR");
        int rounding = utility.getCompanyPreference().getRounding();
        for (int i = opyr + 1; i <= Integer.parseInt((String) valuesHashMap.get("TENR")); i++) {
            Double value = Double.parseDouble((String) valuesHashMap.get("KWDC")) *
                    Math.pow(Integer.parseInt((String) valuesHashMap.get("YLD")),
                            Double.parseDouble((String) valuesHashMap.get("DEP"))) *
                    i;
            csum += value;
            LocalDate year = now.plusYears(i - opyr);
            years.add(String.valueOf(year.getYear()));
            cumulativeValues.add(utility.round(csum, rounding));
        }
        return ChartResponse.builder()
                .labels(years)
                .dataSets(
                        Arrays.asList(
                                DataSet.builder()
                                        .label(attributeOverrideService.findByAttributeValue("PSAV").getDescription())
                                        .data(cumulativeValues)
                                        .borderColor("rgba(34,136,0,1)")
                                        .borderWidth(1)
                                        .lineTension(0.2)
                                        .fill(false)
                                        .build()
                        )
                )
                .retUri("http://example.com")
                .type("line")
                .build();
    }

    public void generateHashMap(CustomerSubscription subscription, Long subscriptionMatrixHeadId,
                                BillingHead billingHead) {
        List<CustomerSubscriptionMapping> staticMappings =
                subscriptionMappingRepository.getMappingsWithStaticValues(subscription, subscriptionMatrixHeadId);
        List<CustomerSubscriptionMapping> calculationMappings =
                subscriptionMappingRepository.getMappingsForCalculationOrderedBySequence(subscription, subscriptionMatrixHeadId);
        LOGGER.info("\tStarted storing values in HashMap");
        LOGGER.info("\t\tStatic values:");
        staticMappings.stream()
                .forEach(mapping -> {
                    valuesHashMap.put(mapping.getRateCode(), mapping.getValue());
                    LOGGER.info("\t\t\t" + mapping.getRateCode() + " = " + mapping.getValue());
                });
        // Add parsed dynamic values in map
        parseAndUpdateValues(calculationMappings);
        LOGGER.info("\tCompleted  storing values in HashMap");
    }

    protected void parseAndUpdateValues(List<CustomerSubscriptionMapping> calculationMappings) {
        LOGGER.info("\t\tDynamic values:");
        calculationMappings.stream()
                .forEach(mapping -> {
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
                            value = rateFunctions.arithmeticExprAnalyzer(null, arithmeticGroups, valuesHashMap);
                            valuesHashMap.put(mapping.getRateCode(), value);
                            LOGGER.info("\t\t\tvalue" + value);
                            LOGGER.info("\t\t\t-----------------------------------");
                        } else if (valueCalculation.getValueType(mapping.getValue()) == ERateMatrixValuePlaceholder.DERIVED) {
                            String[] derivedGgroups = valueCalculation.getCalculatedValue(mapping.getValue());
                            LOGGER.info("\t\t\tderivedGgroups = " + Arrays.asList(derivedGgroups));
                            value = rateFunctions.derivedValueAnalyzer(mapping, valuesHashMap, null, derivedGgroups);
                            valuesHashMap.put(mapping.getRateCode(), value);
                            LOGGER.info("\t\t\tvalue = " + value);
                            LOGGER.info("\t\t\t-----------------------------------");
                        }
                        LOGGER.info("\t\t\t" + mapping.getRateCode() + " = " + value + ", Placeholder = " + mapping.getValue());
                    } else {
                        valuesHashMap.put(mapping.getRateCode(), mapping.getValue());
                        LOGGER.info("\t\t\t" + mapping.getRateCode() + " = " + mapping.getValue());
                    }
                });
    }

}
