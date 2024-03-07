package com.solar.api.tenant.mapper.user;

import com.solar.api.tenant.mapper.payment.info.PaymentInfoDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionDTO;
import com.solar.api.tenant.mapper.user.address.AddressDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RegistrationData {

    private UserDTO user;
    private List<AddressDTO> addresses;
    private List<CustomerSubscriptionDTO> customerSubscriptions;
    private List<PaymentInfoDTO> paymentInfos;
}
