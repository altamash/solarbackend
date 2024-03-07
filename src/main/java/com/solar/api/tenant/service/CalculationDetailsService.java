package com.solar.api.tenant.service;

import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.dataexport.payment.PaymentDataDTO;

import java.util.List;
import java.util.Map;

public interface CalculationDetailsService {

    CalculationDetails addOrUpdate(CalculationDetails calculationDetails);

    CalculationDetails findById(Long id);

    CalculationDetails findBySourceId(Long id);

    List<CalculationDetails> findAll();

    void delete(Long id);

    void deleteAll();

    Map getInvoiceTemplate(Long billHeadId);

    Map getStatusWiseGraph(Map response, List<String> periodList);

    Map getBillingStatusGraph(Map response, List<String> periodList);

    Map getStatusWisePieLM(Map response, List<String> periodList);

    Map getStatusWisePieCM();

    Map getStatusWiseGraphAmountCM();

    Map getCompAnalysisGraph(Map response, List<String> periodList);

    Map getBillingByGardenTable();

    List<CalculationDetails> findAllBySourceIds(List<Long> sourceIds);

    List<CalculationDetails> findAllByStatus(String status);
    List<CalculationDetails> saveAll(List<CalculationDetails> calculationDetailsList);
    List<PaymentDataDTO> findSourceAndError(List<Long> accountId);
    List<CalculationDetails> findAllByStatusAndPeriods(String status, List<String> periods);
}
