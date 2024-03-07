package com.solar.api.tenant.mapper.tiles.dashboardwidget.billing;

import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.history.BillingHistoryDataTile;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.history.BillingHistoryWrapperTile;

import java.util.*;

public class BillingWidgetMapper {
    public static BillingSummaryWidgetTile toBillingSummaryWidgetTile(BillingSummaryWidgetTemplate billingSummaryWidgetTemplate) {

        if (billingSummaryWidgetTemplate == null) {
            return null;
        }
        return BillingSummaryWidgetTile.builder()
                .totalPendingAmount(billingSummaryWidgetTemplate.getTotalPendingAmount())
                .totalCalculatedAmount(billingSummaryWidgetTemplate.getTotalCalculatedAmount())
                .totalInvoicedAmount(billingSummaryWidgetTemplate.getTotalInvoicedAmount())
                .totalPaidAmount(billingSummaryWidgetTemplate.getTotalPaidAmount())
                .totalOutstandingAmount(billingSummaryWidgetTemplate.getTotalOutstandingAmount())
                .build();
    }

    public static BillingHistoryWrapperTile toBillingHistoryComparativeWrapperTile(List<BillingSummaryWidgetTemplate> billingSummaryList) {
        if (billingSummaryList == null) {
            return new BillingHistoryWrapperTile();
        }

        Map<String, List<BillingHistoryDataTile>> graphData = new HashMap<>();
        Set<String> timeSet = new TreeSet<>();

        for (BillingSummaryWidgetTemplate template : billingSummaryList) {
            String subsId = template.getSubsId();
            String timeValue = extractTimeValue(template.getDateTime());

            BillingHistoryDataTile dataTile = new BillingHistoryDataTile(template.getTotalInvoicedAmount(),
                    template.getTotalPaidAmount(),
                    template.getDateTime());

            graphData.computeIfAbsent(subsId, k -> new ArrayList<>()).add(dataTile);
            timeSet.add(timeValue);
        }

        List<String> xAxis = new ArrayList<>(timeSet);

        return new BillingHistoryWrapperTile(graphData, xAxis);
    }
    public static BillingHistoryWrapperTile toBillingHistoryCumulativeWrapperTile(List<BillingSummaryWidgetTemplate> billingSummaryList) {
        if (billingSummaryList == null) {
            return new BillingHistoryWrapperTile();
        }

        Map<String, List<BillingHistoryDataTile>> graphData = new HashMap<>();
        Set<String> timeSet = new TreeSet<>();
        String defaultSubsId = "-1";

        for (BillingSummaryWidgetTemplate template : billingSummaryList) {
            String timeValue = extractTimeValue(template.getDateTime());

            BillingHistoryDataTile dataTile = new BillingHistoryDataTile(template.getTotalInvoicedAmount(),
                    template.getTotalPaidAmount(),
                    template.getDateTime());

            graphData.computeIfAbsent(defaultSubsId, k -> new ArrayList<>()).add(dataTile);
            timeSet.add(timeValue);
        }

        List<String> xAxis = new ArrayList<>(timeSet);

        return new BillingHistoryWrapperTile(graphData, xAxis);
    }

    private static String extractTimeValue(String dateTime) {
        if (dateTime.contains("-")) {  // For 'YYYY-MM' format
            return dateTime.substring(5);  // Extracting the month part
        } else if (dateTime.startsWith("Q")) {  // For 'Q1', 'Q2', 'Q3', 'Q4'
            return dateTime.substring(1);  // Extracting the numeric part of the quarter
        } else {
            return dateTime;  // Default return
        }
    }
}
