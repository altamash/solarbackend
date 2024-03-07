package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.tenant.mapper.billing.calculation.BillByGardenTableTemplate;
import com.solar.api.tenant.mapper.billing.calculation.BillingByGardenTableDTO;
import com.solar.api.tenant.mapper.billing.calculation.CalTrackerGraphDTO;
import com.solar.api.tenant.mapper.billing.calculation.CalTrackerGraphTemplate;
import com.solar.api.tenant.mapper.billing.paymentManagement.PaymentManagementGraphDTO;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.dataexport.payment.PaymentDataDTO;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.repository.CalculationDetailsRepository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


import static com.solar.api.tenant.mapper.billing.calculation.CalculationDetailsMapper.toUpdatedCalculationDetails;

@Service
public class CalculationDetailsServiceImpl implements CalculationDetailsService {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private CalculationDetailsRepository calculationDetailsRepository;
    @Autowired
    private BillingHeadRepository billingHeadRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DataExchange  dataExchange;

    @Override
    public CalculationDetails addOrUpdate(CalculationDetails calculationDetails) {
        if (calculationDetails.getId() != null) {
            CalculationDetails calculationDetailsData = findById(calculationDetails.getId());
            if (calculationDetailsData == null) {
                throw new NotFoundException(CalculationDetails.class, calculationDetails.getId());
            }
            return calculationDetailsRepository.save(toUpdatedCalculationDetails(calculationDetailsData,
                    calculationDetails));
        }
        return calculationDetailsRepository.save(calculationDetails);
    }

    @Override
    public CalculationDetails findById(Long id) {
        return calculationDetailsRepository.findById(id).orElseThrow(() -> new NotFoundException(CalculationDetails.class, id));
    }

    @Override
    public CalculationDetails findBySourceId(Long sourceId) {
        return calculationDetailsRepository.findBySourceId(sourceId).orElseThrow(() -> new NotFoundException(CalculationDetails.class, sourceId));
    }

    @Override
    public List<CalculationDetails> findAll() {
        return calculationDetailsRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        calculationDetailsRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        calculationDetailsRepository.deleteAll();
    }

    /**
     * Description: Method to get Invoice Details from Calculation Detail Table
     *
     * @param billHeadId
     * @return
     */
    @Override
    public Map getInvoiceTemplate(Long billHeadId) {
        User currentUser = userService.getLoggedInUser();
        Map response = new HashMap();
        HashMap<String, String> valueMap = new HashMap<>();
        try {
            CalculationDetails calculationDetails = findBySourceId(billHeadId);
            String html = calculationDetails.getPrevInvHtmlView();
            if (currentUser.getUserType().getId() != 1) {
                html = html.replace("<td colspan=\"2\" style=\"font-size:16px; color:#000000;width:700px; background:#eeeeee;height:30px; font-weight:bold;\"> Pay Invoice </td>", "");
                html = html.replace("<a href=\"#\" style=\"margin-top: 10px;display: inline-block; width: 200px;line-height: 40px;text-decoration: none; color: #fff; height: 40px; background: #e65627; font-weight: 600; text-align:center;\">Pay Now</a>", "");
            }
            valueMap.put("status", calculationDetails.getState());
            valueMap.put("html", cleanHTML(html));
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Details found successfully", valueMap);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "No Details Found", null);
        }

        return response;
    }

    /**
     * Description: Method to return data for status wise chart with count for last 12 months
     *
     * @return
     */
    @Override
    public Map getStatusWiseGraph(Map response, List<String> periodList) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM-yyyy");
        List<String> listStatus = Arrays.asList(EBillStatus.PENDING.getStatus(), EBillStatus.INVOICED.getStatus());

        List<CalTrackerGraphDTO> result = new ArrayList<>();
        List<Integer> pendingCount = new ArrayList<>();
        List<Integer> invoicedCount = new ArrayList<>();
        TreeMap<String, Integer> invoiceMap = generateNewTreeMap(formatter);
        TreeMap<String, Integer> pendingMap = generateNewTreeMap(formatter);
        try {
            List<CalTrackerGraphTemplate> data = billingHeadRepository.getBillStatusGraph(periodList, listStatus);
            List<String> allMonths = data.stream().map(x -> x.getPeriod()).collect(Collectors.toList());
            List<String> uniqueMonths = allMonths.stream().distinct().collect(Collectors.toList());
            List<CalTrackerGraphTemplate> invoicedList = data.stream().filter(x -> x.getBillStatus().equalsIgnoreCase(EBillStatus.INVOICED.getStatus())).collect(Collectors.toList());
            List<CalTrackerGraphTemplate> pendingList = data.stream().filter(x -> x.getBillStatus().equalsIgnoreCase(EBillStatus.PENDING.getStatus())).collect(Collectors.toList());
            invoiceMap = fillChartMap(uniqueMonths, invoicedList, invoiceMap);
            pendingMap = fillChartMap(uniqueMonths, pendingList, pendingMap);

            Collection<Integer> invoiceValues = invoiceMap.values();
            Collection<Integer> pendingValues = pendingMap.values();
            invoicedCount = new ArrayList<>(invoiceValues);
            pendingCount = new ArrayList<>(pendingValues);

            result.add(CalTrackerGraphDTO.builder().period(String.join(",", pendingMap.keySet())).build());
            result.add(CalTrackerGraphDTO.builder().status(EBillStatus.INVOICED.getStatus()).count(invoicedCount).build());
            result.add(CalTrackerGraphDTO.builder().status(EBillStatus.PENDING.getStatus()).count(pendingCount).build());
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Data found successfully", result);
        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }
        return response;
    }

    /**
     * Description: Method to return data for billing status chart with count for last 12 months
     *
     * @return
     */
    @Override
    public Map getBillingStatusGraph(Map response, List<String> periodList) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM-yyyy");
        List<String> listStatus = Arrays.asList(EBillStatus.SKIPPED.getStatus(), EBillStatus.INVOICED.getStatus());
        List<CalTrackerGraphDTO> result = new ArrayList<>();
        List<Integer> invoicedCount = new ArrayList<>();
        List<Integer> skippedCount = new ArrayList<>();
        TreeMap<String, Integer> invoiceMap = generateNewTreeMap(formatter);
        TreeMap<String, Integer> skippedMap = generateNewTreeMap(formatter);
        try {
            List<CalTrackerGraphTemplate> data = billingHeadRepository.getBillStatusGraph(periodList, listStatus);
            List<String> allMonths = data.stream().map(x -> x.getPeriod()).collect(Collectors.toList());
            List<String> uniqueMonths = allMonths.stream().distinct().collect(Collectors.toList());
            List<CalTrackerGraphTemplate> invoicedList = data.stream().filter(x -> x.getBillStatus().equalsIgnoreCase(EBillStatus.INVOICED.getStatus())).collect(Collectors.toList());
            List<CalTrackerGraphTemplate> skippedList = data.stream().filter(x -> x.getBillStatus().equalsIgnoreCase(EBillStatus.SKIPPED.getStatus())).collect(Collectors.toList());
            invoiceMap = fillChartMap(uniqueMonths, invoicedList, invoiceMap);
            skippedMap = fillChartMap(uniqueMonths, skippedList, skippedMap);

            Collection<Integer> invoiceValues = invoiceMap.values();
            Collection<Integer> skippedValues = skippedMap.values();
            skippedCount = new ArrayList<>(skippedValues);
            invoicedCount = new ArrayList<>(invoiceValues);

            result.add(CalTrackerGraphDTO.builder().period(String.join(",", skippedMap.keySet())).build());
            result.add(CalTrackerGraphDTO.builder().status(EBillStatus.SKIPPED.getStatus()).count(skippedCount).build());
            result.add(CalTrackerGraphDTO.builder().status(EBillStatus.INVOICED.getStatus()).count(invoicedCount).build());
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Data found successfully", result);
        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }
        return response;
    }

    /**
     * Description: Method to return data for status wise pie chart with count for last month
     *
     * @return
     */
    @Override
    public Map getStatusWisePieLM(Map response, List<String> periodList) {
        List<CalTrackerGraphDTO> result = new ArrayList<>();
        List<String> queryStatusList = Arrays.asList(EBillStatus.PENDING.getStatus(), EBillStatus.CALCULATED.getStatus(), EBillStatus.INVOICED.getStatus(), EBillStatus.SKIPPED.getStatus(), EBillStatus.PUBLISHED.getStatus());
        try {
            List<CalTrackerGraphTemplate> data = billingHeadRepository.getStatusWiseGraphLM(periodList, queryStatusList);
            List<String> uniqueMonths = data.stream().map(x -> x.getPeriod()).distinct().collect(Collectors.toList());
            Map<String, Integer> statusCounts = data.stream()
                    .collect(Collectors.groupingBy(CalTrackerGraphTemplate::getBillStatus,
                            Collectors.summingInt(CalTrackerGraphTemplate::getStatusCount)));
            Collection<Integer> invoiceValues = statusCounts.values();
            List<Integer> countList = new ArrayList<>(invoiceValues);
            result.add(CalTrackerGraphDTO.builder().period(String.join(",", uniqueMonths)).build());
            result.add(CalTrackerGraphDTO.builder().status(String.join(",", statusCounts.keySet())).count(countList).build());
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Data found successfully", result);
        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }
        return response;
    }

    /**
     * Description: Method to return data for status wise pie chart with count for current month
     *
     * @return
     */
    @Override
    public Map getStatusWisePieCM() {
        Map response = new HashMap();

        List<CalTrackerGraphDTO> result = new ArrayList<>();
        try {
            List<CalTrackerGraphTemplate> data = billingHeadRepository.getStatusWiseGraphCM();
            List<String> allMonths = data.stream().map(x -> x.getPeriod()).collect(Collectors.toList());
            List<String> uniqueMonths = allMonths.stream().distinct().collect(Collectors.toList());

            List<String> statusList = data.stream().map(x -> x.getBillStatus()).collect(Collectors.toList());
            List<Integer> countList = data.stream().map(x -> x.getStatusCount()).collect(Collectors.toList());

            result.add(CalTrackerGraphDTO.builder().period(String.join(",", uniqueMonths)).build());
            result.add(CalTrackerGraphDTO.builder().status(String.join(",", statusList)).count(countList).build());
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Data found successfully", result);
        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }
        return response;
    }

    /**
     * Description: Method to return data for status wise graph with amount for current month
     *
     * @return
     */
    @Override
    public Map getStatusWiseGraphAmountCM() {
        Map response = new HashMap();

        List<CalTrackerGraphDTO> result = new ArrayList<>();
        try {
            List<CalTrackerGraphTemplate> data = billingHeadRepository.getStatusWiseGraphCM();
            List<String> allMonths = data.stream().map(x -> x.getPeriod()).collect(Collectors.toList());
            List<String> uniqueMonths = allMonths.stream().distinct().collect(Collectors.toList());

            List<String> statusList = data.stream().map(x -> x.getBillStatus()).collect(Collectors.toList());
            List<Double> amountList = data.stream().map(x -> x.getAmount() != null ? x.getAmount() : 0).collect(Collectors.toList());
            Double total = amountList.stream().reduce(0d, Double::sum);
            result.add(CalTrackerGraphDTO.builder().period(String.join(",", uniqueMonths)).build());
            result.add(CalTrackerGraphDTO.builder().status(String.join(",", statusList)).amount(amountList).build());
            result.add(CalTrackerGraphDTO.builder().status("Total").amount(Collections.singletonList(total)).build());
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Data found successfully", result);
        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }
        return response;
    }

    /**
     * Description: Method to return data for billing status chart with count for last 12 months
     *
     * @return
     */
    @Override
    public Map getCompAnalysisGraph(Map response, List<String> periodList) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM-yyyy");
        List<String> listStatus = Arrays.asList(EBillStatus.CALCULATED.getStatus(), EBillStatus.INVOICED.getStatus(), EBillStatus.SKIPPED.getStatus());

        List<CalTrackerGraphDTO> result = new ArrayList<>();
        List<Double> calculatedAmount = new ArrayList<>();
        List<Double> invoicedAmount = new ArrayList<>();
        List<Double> skippedAmount = new ArrayList<>();
        List<Double> totalAmount = new ArrayList<>();
        TreeMap<String, Double> calculatedMap = generateNewTreeMap(formatter);
        TreeMap<String, Double> invoiceMap = generateNewTreeMap(formatter);
        TreeMap<String, Double> skippedMap = generateNewTreeMap(formatter);
        TreeMap<String, Double> totalMap = generateNewTreeMap(formatter);
        try {
            List<CalTrackerGraphTemplate> data = billingHeadRepository.getBillStatusGraph(periodList, listStatus);
            List<String> allMonths = data.stream().map(x -> x.getPeriod()).collect(Collectors.toList());
            List<String> uniqueMonths = allMonths.stream().distinct().collect(Collectors.toList());

            List<CalTrackerGraphTemplate> calculatedList = data.stream().filter(x -> x.getBillStatus().equalsIgnoreCase(EBillStatus.CALCULATED.getStatus())).collect(Collectors.toList());
            List<CalTrackerGraphTemplate> invoicedList = data.stream().filter(x -> x.getBillStatus().equalsIgnoreCase(EBillStatus.INVOICED.getStatus())).collect(Collectors.toList());
            List<CalTrackerGraphTemplate> skippedList = data.stream().filter(x -> x.getBillStatus().equalsIgnoreCase(EBillStatus.SKIPPED.getStatus())).collect(Collectors.toList());

            invoiceMap = fillCountChartMap(uniqueMonths, invoicedList, invoiceMap);
            skippedMap = fillCountChartMap(uniqueMonths, skippedList, skippedMap);
            calculatedMap = fillCountChartMap(uniqueMonths, calculatedList, calculatedMap);
            totalMap = fillTotalMap(uniqueMonths, invoicedList, totalMap);
            totalMap = fillTotalMap(uniqueMonths, skippedList, totalMap);
            totalMap = fillTotalMap(uniqueMonths, calculatedList, totalMap);

            Collection<Double> invoiceValues = invoiceMap.values();
            Collection<Double> skippedValues = skippedMap.values();
            Collection<Double> calculatedValues = calculatedMap.values();
            Collection<Double> totalValues = totalMap.values();

            calculatedAmount = new ArrayList<>(calculatedValues);
            invoicedAmount = new ArrayList<>(invoiceValues);
            skippedAmount = new ArrayList<>(skippedValues);
            totalAmount = new ArrayList<>(totalValues);


            result.add(CalTrackerGraphDTO.builder().period(String.join(",", skippedMap.keySet())).build());
            result.add(CalTrackerGraphDTO.builder().status(EBillStatus.INVOICED.getStatus()).amount(invoicedAmount).build());
            result.add(CalTrackerGraphDTO.builder().status(EBillStatus.CALCULATED.getStatus()).amount(calculatedAmount).build());
            result.add(CalTrackerGraphDTO.builder().status(EBillStatus.SKIPPED.getStatus()).amount(skippedAmount).build());
            result.add(CalTrackerGraphDTO.builder().status("Total").amount(totalAmount).build());
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Data found successfully", result);
        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }
        return response;
    }

    @Override
    public Map getBillingByGardenTable() {
        Map response = new HashMap();
        List<BillingByGardenTableDTO> result = new ArrayList<>();
        List<BillByGardenTableTemplate> billByGardenTableTemplate = calculationDetailsRepository.getBillByGardenTable();
        billByGardenTableTemplate.stream().forEach(
                template -> {
                    try {
                        Map<String, String> subsMeasures = new ObjectMapper().readValue(template.getBillJson(), Map.class);
                        String srcNo = subsMeasures.get("SCSGN") != null ? subsMeasures.get("SCSGN").toString() : "";
                        Double gardenSize = subsMeasures.get("S_GS") != null ? Double.parseDouble(subsMeasures.get("S_GS").toString()) : 0d;
                        result.add(BillingByGardenTableDTO.builder()._id(template.getVariantId())
                                .variantAlias(template.getVariantAlias())
                                .subscriptionCount(template.getSubsCount()).srcNo(srcNo).gardenSize(gardenSize).build());

                    } catch (Exception e) {

                    }
                }
        );
        if (result.size() > 0) {
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Data found successfully", result);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }


        return response;
    }

    private TreeMap generateNewTreeMap(SimpleDateFormat formatter) {
        return new TreeMap<>(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                String s1 = (String) o1;
                String s2 = (String) o2;
                try {
                    return formatter.parse(s1).compareTo(formatter.parse(s2));
                } catch (ParseException e) {
                    throw new RuntimeException("Bad date format");
                }
            }
        });
    }

    private TreeMap fillChartMap(List<String> uniqueMonths, List<CalTrackerGraphTemplate> dataList, TreeMap<String, Integer> dataMap) {
        uniqueMonths.forEach(month -> {
            if (dataList.size() > 0) {
                dataList.forEach(chartData -> {
                    if (month.equalsIgnoreCase(chartData.getPeriod())) {
                        dataMap.put(chartData.getPeriod(), chartData.getStatusCount());
                    } else {
                        if (!dataMap.containsKey(month)) {
                            dataMap.put(month, 0);
                        }
                    }
                });
            } else {
                dataMap.put(month, 0);
            }
        });
        return dataMap;
    }

    private TreeMap fillCountChartMap(List<String> uniqueMonths, List<CalTrackerGraphTemplate> dataList, TreeMap<String, Double> dataMap) {
        uniqueMonths.forEach(month -> {
            if (dataList.size() > 0) {
                dataList.forEach(data -> {
                    if (month.equalsIgnoreCase(data.getPeriod())) {
                        if (dataMap.containsKey(data.getPeriod())) {
                            dataMap.put(data.getPeriod(), (data.getAmount() != null ? data.getAmount() : 0) + dataMap.get(data.getPeriod()));
                        } else {
                            dataMap.put(month, data.getAmount() != null ? data.getAmount() : 0);
                        }
                    } else {
                        if (!dataMap.containsKey(month)) {
                            dataMap.put(month, 0d);
                        }
                    }
                });
            } else {
                dataMap.put(month, 0d);
            }
        });
        return dataMap;
    }

    private TreeMap fillTotalMap(List<String> uniqueMonths, List<CalTrackerGraphTemplate> dataList, TreeMap<String, Double> dataMap) {
        uniqueMonths.forEach(month -> {
            if (dataList.size() > 0) {
                dataList.forEach(data -> {
                    if (month.equalsIgnoreCase(data.getPeriod()) && dataMap.containsKey(data.getPeriod())) {
                        dataMap.put(data.getPeriod(), (data.getAmount() != null ? data.getAmount() : 0) + dataMap.get(data.getPeriod()));
                    } else {
                        dataMap.put(month, data.getAmount() != null ? data.getAmount() : 0);
                    }
                });
            } else {
                dataMap.put(month, 0d);
            }
        });
        return dataMap;
    }

    @Override
    public List<CalculationDetails> findAllBySourceIds(List<Long> sourceIds) {
        return calculationDetailsRepository.findAllBySourceIds(sourceIds);
    }

    @Override
    public List<CalculationDetails> findAllByStatus(String status) {
        return calculationDetailsRepository.findAllByStatus(status);
    }

    @Override
    public List<CalculationDetails> saveAll(List<CalculationDetails> calculationDetailsList) {
        return calculationDetailsRepository.saveAll(calculationDetailsList);
    }

    @Override
    public List<PaymentDataDTO> findSourceAndError(List<Long> accountId) {
        return calculationDetailsRepository.findSourceAndError(accountId);
    }

    private  String cleanHTML(String htmlInput) {

        String encodedString = String.valueOf(dataExchange.washHtml(htmlInput));
        Document doc = Jsoup.parse(encodedString);

        // Select the desired section after <h3>Washed!</h3>
        Element washedSection = doc.selectFirst("h3:contains(Washed!) + div.glueo-simpletoolextensions-simpletool-result");

        if (washedSection == null) {
            return ""; // not found
        }

        // Get the content inside the textarea
        Element textarea = washedSection.selectFirst("textarea");
        String textContent = "";
        if (textarea != null) {
            textContent = textarea.text(); // Get the content inside the textarea
        }

        String result =  textContent;

        return result.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#xD;", "")
                .replace("&#xA;", "")
                .replace("\r","")
                .replace("\n","");

    }
    @Override
    public List<CalculationDetails> findAllByStatusAndPeriods(String status, List<String> periods) {
        return calculationDetailsRepository.findAllByStatusAndPeriods(status,periods);
    }
}
