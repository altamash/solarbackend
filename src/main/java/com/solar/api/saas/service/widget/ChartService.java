package com.solar.api.saas.service.widget;

import com.solar.api.saas.mapper.widget.chart.ChartResponse;
import com.solar.api.saas.model.widget.chart.ChartDetail;
import com.solar.api.saas.model.widget.chart.ChartHead;

import java.util.List;

public interface ChartService {

    // ChartHead ////////////////////////////////////////////////
    ChartHead addOrUpdate(ChartHead chartHead);

    ChartHead findById(Long id);

    List<ChartHead> findAll();

    void delete(Long id);

    void deleteAll();

    // ChartDetail ////////////////////////////////////////////////
    ChartDetail addOrUpdateChartDetail(ChartDetail chartDetail);

    ChartDetail findChartDetailById(Long id);

    List<ChartDetail> findAllChartDetails();

    void deleteChartDetail(Long id);

    void deleteAllChartDetails();

    // Chart API ////////////////////////////////////////////////
    ChartResponse getChartData(String widgetCode, Long accountId, Long subscriptionId);
}
