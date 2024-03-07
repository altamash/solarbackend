package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.payment.info.PaymentInfoWrapper;

public interface PaymentInfoRepositoryCustom {

    PaymentInfoWrapper getPaymentInfoByGardenId(String gardenId,
                                                String month,
                                                String paymentSource,
                                                String billStatus);
}
