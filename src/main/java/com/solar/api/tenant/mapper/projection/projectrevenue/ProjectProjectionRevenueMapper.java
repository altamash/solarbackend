package com.solar.api.tenant.mapper.projection.projectrevenue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectProjectionRevenueMapper {

    public static List<ProjectProjectionRevenueDTO> toProjectProjectionRevenueDTO(List<ProjectProjectionRevenue> data) {
        Map<String, Map<String, Double>> dataMap = new HashMap<>();
        List<String> months = data.stream().map(ProjectProjectionRevenue::getBillingMonth).distinct().collect(Collectors.toList());
        data.forEach(record -> {
            String efficiency = record.getEfficiency();
            String billingMonth = record.getBillingMonth();
            Double totalAmount = record.getTotalAmount();

            dataMap
                    .computeIfAbsent(efficiency, k -> new LinkedHashMap<>())
                    .put(billingMonth, totalAmount);
        });

        return dataMap.entrySet().stream()
                .map(entry -> {
                    ProjectProjectionRevenueDTO transformedData = new ProjectProjectionRevenueDTO();
                    transformedData.setEfficiency(entry.getKey());

                    Map<String, Double> amounts = entry.getValue();
                    for (String month : months) {
                        int monthIndex = months.indexOf(month) + 1;
                        String amountKey = "amount" + monthIndex;
                        Double amount = amounts.getOrDefault(month, 0.0);

                        switch (monthIndex) {
                            case 1:
                                transformedData.setAmount1(amount);
                                break;
                            case 2:
                                transformedData.setAmount2(amount);
                                break;
                            case 3:
                                transformedData.setAmount3(amount);
                                break;
                        }
                    }

                    return transformedData;
                })
                .collect(Collectors.toList());
    }
}
