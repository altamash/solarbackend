package com.solar.api.tenant.mapper.tiles.calculationTracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.billing.calculation.CalculationTrackerBillingDetailDTO;
import com.solar.api.tenant.mapper.billing.calculation.CustomerDetailDTO;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculationTrackerTile {

    private Long billHeadId;
    private String period;
    private String sourceName;
    private Double amount;
    private String status;
    private Date dueDate;
    private CustomerDetailDTO customerDetailDTO;
    private String subsId;
    private String subsName;
    private String address;
    private String error;
    private String errorDesc;
    private Boolean invalidationAllowed;
    private Boolean billSkip;
    private Long accountId;
    private List<CalculationTrackerBillingDetailDTO> billingDetailList;
    private Integer reAttemptCount;
    private String premiseNo;
    private String gardenSrc;
    private Double creditValue;
    private String creditType;
    private Long creditTypeId;

    public CalculationTrackerTile(Long billHeadId, String period, Double amount,
                                  String status, Date dueDate, String customerName, String customerType, String customerEmail,
                                  String customerPhone, String profileUrl, String subsId, String errorDesc,
                                  Boolean billSkip, Long accountId, Integer reAttemptCount, String sourceName) {
        this.billHeadId = billHeadId;
        this.period = period;
        this.amount = amount;
        this.status = status;
        if (dueDate != null) {
            this.dueDate = dueDate;
        }
        this.customerDetailDTO = CustomerDetailDTO.builder().customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .customerType(customerType)
                .profileUrl(profileUrl).build();
        this.subsId = subsId;
        this.errorDesc = errorDesc;
        this.billSkip = billSkip;
        this.accountId = accountId;
        this.reAttemptCount = reAttemptCount;
        this.sourceName = sourceName;
    }
    public CalculationTrackerTile(Long billHeadId, String period, Double amount,
                                  String status, Date dueDate, String customerName, String customerType, String customerEmail,
                                  String customerPhone, String profileUrl, String subsId, String errorDesc,
                                  Boolean billSkip, Long accountId, Integer reAttemptCount, String sourceName,String siteLocation,
                                  String subsName,String premiseNo,String gardenSrc) {
        this.billHeadId = billHeadId;
        this.period = period;
        this.amount = amount;
        this.status = status;
        if (dueDate != null) {
            this.dueDate = dueDate;
        }
        this.customerDetailDTO = CustomerDetailDTO.builder().customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .customerType(customerType)
                .profileUrl(profileUrl).build();
        this.subsId = subsId;
        this.errorDesc = errorDesc;
        this.billSkip = billSkip;
        this.accountId = accountId;
        this.reAttemptCount = reAttemptCount;
        this.sourceName = sourceName;
        this.address = siteLocation;
        this.subsName=subsName;
        this.premiseNo= Utility.getMeasureAsJson(premiseNo, Constants.RATE_CODES.S_PN);
        this.gardenSrc=gardenSrc;
    }
}
