package com.solar.api.tenant.mapper.billing.billingHead;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class PaymentTransactionHeadDetailMasterDTO {

    Double totalOutstandingAmount;
    List<PaymentTransactionHeadDetailDTO> PaymentTransactionHeadDetailDTO;

}
