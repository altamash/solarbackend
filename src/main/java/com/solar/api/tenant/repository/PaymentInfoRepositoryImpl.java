package com.solar.api.tenant.repository;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoTemplate;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PaymentInfoRepositoryImpl implements PaymentInfoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private Utility utility;

    @Override
    public PaymentInfoWrapper getPaymentInfoByGardenId(String gardenId, String month, String paymentSource,
                                                       String billStatus) {

        String queryString = "select v.customer_subscription_id, cn.value, v.value as premise_no," +
                " bh.amount as invoice_amount, bh.amount as paid_amount, bh.invoice_id, bh.bill_status," +
                " bh.billing_month_year, bh.id as billing_id, ps.value as payment_alias, pi.payment_source," +
                " pi.account_number, \\'\\' as ach_status" +
                " from subscription_matrix_v v, billing_head bh, customer_subscription cs, " +
                "customer_subscription_mapping ps, payment_info pi, customer_subscription_mapping cn" +
                " where " +
                "v.subscription_rate_matrix_id in (select iq.subscription_rate_matrix_id " +
                "from subscription_rate_matrix_detail iq" +
                " where iq.rate_code='SCSGN'" +
                " and iq.default_value=:gardenId)" +

                " and v.rate_code in ('PN') and (v.value is not null) and v.customer_subscription_id=cs.id" +
                " and bh.subscription_id=cs.id" +
                " and billing_month_year=:month" +
                " and ps.customer_subscription_id=v.customer_subscription_id" +
                " and ps.rate_code='PSRC'" +
                " and pi.portal_account_id=cs.account_id" +
                " and ps.value=pi.payment_src_alias" +
                " and pi.payment_source in :paymentSource" +
                " and cn.rate_code = 'CN'" +
                " and cn.customer_subscription_id=v.customer_subscription_id" +
                " and bh.bill_status in :billStatus";
        Query query = em.createNativeQuery(queryString);
        query.setParameter("gardenId", gardenId);
        query.setParameter("month", month);
        query.setParameter("paymentSource", Arrays.asList(paymentSource.split(",")));
        query.setParameter("billStatus", Arrays.asList(billStatus.split(",")));
        List<Object[]> list = query.getResultList();
        List<PaymentInfoTemplate> views = new ArrayList<>();
        AtomicReference<Double> invoiceAmount = new AtomicReference<>((double) 0);
        AtomicReference<Double> paidAmount = new AtomicReference<>((double) 0);
        int rounding = utility.getCompanyPreference().getRounding();
        list.forEach(objects -> {
            invoiceAmount.updateAndGet(v -> new Double((double) (v + Double.valueOf((Double) objects[3]))));
            paidAmount.updateAndGet(v -> new Double((double) (v + Double.valueOf((Double) objects[4]))));
            views.add(
                    PaymentInfoTemplate.builder()
                            .customerSubscriptionId(new Long(((BigInteger) objects[0]).longValue()))
                            .customerName((String) objects[1])
                            .premiseNo(objects[2] == null ? null : (Long.valueOf((String) objects[2])))
                            .invoiceAmount(objects[3] == null ? null : utility.round((Double) objects[3], rounding))
                            .paidAmount(objects[4] == null ? null : utility.round((Double) objects[4], rounding))
                            .invoiceId(objects[5] == null ? null : new Long(((BigInteger) objects[5]).longValue()))
                            .billStatus((String) objects[6])
                            .billingMonthYear((String) objects[7])
                            .billingId(objects[8] == null ? null : new Long(((BigInteger) objects[8]).longValue()))
                            .paymentAlias((String) objects[9])
                            .paymentSource((String) objects[10])
                            .accountNumber((String) objects[11])
                            .achStatus((String) objects[12])
                            .build());
        });
        return PaymentInfoWrapper.builder()
                .paymentInfoTemplates(views)
                .invoiceAmount(utility.round(invoiceAmount.get(), rounding))
                .paidAmount(utility.round(paidAmount.get(), rounding))
                .build();
    }
}
