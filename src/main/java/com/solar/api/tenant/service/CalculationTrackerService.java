package com.solar.api.tenant.service;

import com.mchange.util.AlreadyExistsException;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.billing.calculation.CalculationTracker;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CalculationTrackerService {

    CalculationTracker addOrUpdate(CalculationTracker calculationTracker) throws AlreadyExistsException;

    CalculationTracker findById(Long id);

    List<CalculationTracker> findAll();

    void delete(Long id);

    void deleteAll();

    CalculationDetails updateBillingLog(Long billHeadId, String state);

    CalculationDetails updateBillingLogCalculation(Long billHeadId, String state);

    CalculationDetails updateBillingLogInvoice(Long billHeadId, String state, Long invoiceId, Date invoiceDate, Date dueDate);

    CalculationDetails updateBillingLogError(Long billHeadId, String error);

    Map getCalculationTrackerList(Map response,String groupBy, List<String> period);

    Map getBillingPeriodList();

    Map getCalculationTrackerListByUserId(Map response, List<String> periodList, Long userId);
    BaseResponse addManualCredits(String credits);
    }
