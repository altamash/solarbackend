package com.solar.api.tenant.mapper.payment.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.user.UserDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentInfoDTO {

    private Long id;
    private String paymentSrcAlias;
    private UserDTO portalAccount;
    private Long portalAccountId;
    private Long acctId;
    private Integer sequenceNumber;
    private String paymentSource;
    private String accountTitle;
    private String accountNo;
    private String routingNo;
    private String accountType;
    private String bankName;
    private Boolean primaryIndicator;
    private Boolean ecApproved;
//    private String cvvNo;
//    private String cardNo;
    private String cardProvider;
    private String cardType;
    private Boolean isPrimary;
}
