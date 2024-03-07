package com.solar.api.tenant.mapper.tiles.dataexport.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataExportPaymentTile {
    private static final DateTimeFormatter desiredDateFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy");
    private static final DateTimeFormatter desiredPeriodFormat = DateTimeFormatter.ofPattern("MMMM yyyy");
    private String customerName;
    private String period;
    private String source;
    private String subscription;
    private String status;
    private Double invoicedAmount;
    private Double paidAmount;
    private Double remainingAmount;
    private String dueDate;
    private String paymentDate;

    public DataExportPaymentTile(String customerName, String period,
                                 String source, String subscription, String status, Double invoicedAmount,
                                 Double paidAmount, Date dueDate, Date paymentDate) {
        this.customerName = customerName;
        if (period != null) {
            DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("MM-yyyy");
            YearMonth yearMonth = YearMonth.parse(period, dbFormat);
            this.period = yearMonth.format(desiredPeriodFormat);
        }

        this.source = source;
        this.subscription = subscription;
        this.status = status;
        this.invoicedAmount = invoicedAmount;
        this.paidAmount = (paidAmount !=null ? paidAmount :0d);
        this.remainingAmount = (invoicedAmount != null ? invoicedAmount : 0) - (paidAmount != null ? paidAmount : 0);
        if (dueDate != null) {
            LocalDate localDate = dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            this.dueDate = localDate.format(desiredDateFormat);
            ;
        }
        if (paymentDate != null) {
            LocalDate localDate = paymentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            this.paymentDate = localDate.format(desiredDateFormat);
            ;
        }


    }
}
