package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.payment.billing.PaymentDetailsView;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PaymentDetailsViewRepositoryImpl implements PaymentDetailsViewRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    public List<PaymentDetailsView> generateList(List<Object[]> list) {
        List<PaymentDetailsView> views = new ArrayList<>();
        String str = "2000-01-01 00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        AtomicReference<LocalDateTime> dateTimeString = new AtomicReference<>(LocalDateTime.parse(str, formatter));
        list.forEach(objects -> {
            if (objects[17] != null) {
                dateTimeString.set(((Timestamp) objects[17]).toLocalDateTime());
            }
            LocalDateTime transTime = dateTimeString.get();
            views.add(PaymentDetailsView.builder()
                    .accountId((BigInteger) objects[0])
                    .firstName((String) objects[1])
                    .lastName((String) objects[2])
                    .subscriptionId((BigInteger) objects[3])
                    .premiseNo((String) objects[4])
                    .gardenSrc((String) objects[5])
                    .gardenName((String) objects[6])
                    .subscriptionType((String) objects[7])
                    .subscriptionRate_Matrix_Id((BigInteger) objects[8])
                    .billHeadId((BigInteger) objects[9])
                    .billedAmount((Double) objects[10])
                    .pendingAmount((Double) objects[11])
                    .billingMonthYear((String) objects[12])
                    .billStatus((String) objects[13])
                    .invoiceId((BigInteger) objects[14])
                    .paymentId((BigInteger) objects[15])
                    .totalPaidAmount((Double) objects[16])
                    .transactionDate(transTime)
                    .source((String) objects[18])
                    .instrumentNum((String) objects[19])
                    .issuer((String) objects[20])
                    .bill_credit((Double) objects[21])
                    .fullName((String) objects[1]+" "+(String) objects[2])
                    .build());
        });
        if (views.isEmpty()) {
            return null;
        }
        return views;
    }

    @Override
    public List<PaymentDetailsView> getByAccount(List<String> accountId, List<String> billingMonthYear,
                                                 List<String> billStatus, List<String> source) {
        Query query = em.createNativeQuery("SELECT * FROM payment_details_view where account_id in :accountId and " +
                "billing_month_year in :billingMonthYear and bill_status in :billStatus and source in :source");
        query.setParameter("accountId", accountId);
        query.setParameter("billingMonthYear", billingMonthYear);
        query.setParameter("billStatus", billStatus);
        query.setParameter("source", source);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<PaymentDetailsView> getBySubscriptionType(List<String> subscriptionType,
                                                          List<String> billingMonthYear, List<String> billStatus,
                                                          List<String> source) {
        Query query = em.createNativeQuery("SELECT * FROM payment_details_view where subscription_type in " +
                ":subscriptionType and billing_month_year in :billingMonthYear and bill_status in :billStatus and " +
                "source in :source");
        query.setParameter("subscriptionType", subscriptionType);
        query.setParameter("billingMonthYear", billingMonthYear);
        query.setParameter("billStatus", billStatus);
        query.setParameter("source", source);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<PaymentDetailsView> getBySubscriptionId(List<String> subscriptionId, List<String> billingMonthYear,
                                                        List<String> billStatus, List<String> source) {
        Query query = em.createNativeQuery("SELECT * FROM payment_details_view where subscription_id in " +
                ":subscriptionId and billing_month_year in :billingMonthYear and bill_status in :billStatus and " +
                "source in :source");
        query.setParameter("subscriptionId", subscriptionId);
        query.setParameter("billingMonthYear", billingMonthYear);
        query.setParameter("billStatus", billStatus);
        query.setParameter("source", source);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<PaymentDetailsView> getByGardenSRC(List<String> gardenSRC, List<String> billingMonthYear,
                                                   List<String> billStatus, List<String> source) {
        Query query = em.createNativeQuery("SELECT * FROM payment_details_view where garden_src in :gardenSRC and " +
                "billing_month_year in :billingMonthYear and bill_status in :billStatus and source in :source");
        query.setParameter("gardenSRC", gardenSRC);
        query.setParameter("billingMonthYear", billingMonthYear);
        query.setParameter("billStatus", billStatus);
        query.setParameter("source", source);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<PaymentDetailsView> getByPremiseNumber(List<String> premiseNumber, List<String> billingMonthYear,
                                                       List<String> billStatus, List<String> source) {
        Query query = em.createNativeQuery("SELECT * FROM payment_details_view where premise_no in :premiseNumber and" +
                " billing_month_year in :billingMonthYear and bill_status in :billStatus and source in :source");
        query.setParameter("premiseNumber", premiseNumber);
        query.setParameter("billingMonthYear", billingMonthYear);
        query.setParameter("billStatus", billStatus);
        query.setParameter("source", source);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<PaymentDetailsView> getByInvoiceId(List<String> invoiceId, List<String> billingMonthYear,
                                                   List<String> billStatus, List<String> source) {
        Query query = em.createNativeQuery("SELECT * FROM payment_details_view where invoice_id in :invoiceId and " +
                "billing_month_year in :billingMonthYear and bill_status in :billStatus and source in :source");
        query.setParameter("invoiceId", invoiceId);
        query.setParameter("billingMonthYear", billingMonthYear);
        query.setParameter("billStatus", billStatus);
        query.setParameter("source", source);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<PaymentDetailsView> getAll() {
        Query query = em.createNativeQuery("SELECT * FROM payment_details_view");
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }
}
