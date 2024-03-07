package com.solar.api.tenant.mapper.ca;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.contract.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaSoftCreditCheckDTO {
    private Long id;
    private Entity customerNo;
    private Long sequenceNo;
    private String source;
    private String referenceNo;
    private String creditStatus;
    private LocalDateTime dateTime;
    private Long checkedBy;
    private  Boolean isCheckedLater;
    private LocalDateTime expiryDate;


}
