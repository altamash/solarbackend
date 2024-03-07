package com.solar.api.tenant.model.dataexport.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDataDTO {

    private Long entityId;
    private Long accntId;
    private String name;
    private String image;
    private String period;
    private String periodName;
    private String error;
    private String source;
    private String sourceId;

    public PaymentDataDTO(Long entityId, Long accntId, String name, String image) {
        this.entityId = entityId;
        this.accntId = accntId;
        this.name = name;
        this.image = image;
    }

    public PaymentDataDTO(String error, String source,String sourceId) {
        this.error = error;
        this.source = source;
        this.sourceId = sourceId;
    }

    public PaymentDataDTO(String period) {
        this.periodName = getFormattedDate(period);
        this.period= period;
    }
    private String getFormattedDate(String period) {
        try {
            String[] parts = period.split("-");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            return Month.of(month).name() + " " + year;
        } catch (DateTimeParseException e) {
          return period;
        }
    }
        }
