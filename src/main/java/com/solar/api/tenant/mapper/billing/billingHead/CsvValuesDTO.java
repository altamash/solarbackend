package com.solar.api.tenant.mapper.billing.billingHead;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CsvValuesDTO {

    private String paymentType;
    private String debtorNumber;
    private String premiseNumber;
    private String subscriberAllocationHistorySubscriberName;
    private String monthlyProductionAllocationinkWh;
    private String tariffRate;
    private String billCredit;
    private String gardenID;
    private String namePlateCapacitykWDC;
    private String calendarMonth;

    @Override
    public boolean equals(Object o) {
        CsvValuesDTO that = (CsvValuesDTO) o;
        return paymentType.equals(that.paymentType) &&
                premiseNumber.equals(that.premiseNumber) &&
                gardenID.equals(that.gardenID) &&
                calendarMonth.equals(that.calendarMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentType, debtorNumber, premiseNumber, subscriberAllocationHistorySubscriberName,
                monthlyProductionAllocationinkWh, tariffRate, billCredit, gardenID, namePlateCapacitykWDC,
                calendarMonth);
    }
}
