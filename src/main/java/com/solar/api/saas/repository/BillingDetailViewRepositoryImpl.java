package com.solar.api.saas.repository;

import com.solar.api.saas.model.chart.views.BillingByCodeView;
import com.solar.api.saas.model.chart.views.BillingDetailView;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillingDetailViewRepositoryImpl implements BillingDetailViewRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<BillingByCodeView> billingByCodeAndDate(Long accountId, Long subscriptionId, String rateCode,
                                                        String startDate, String endDate) {
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery("SELECT billing_code, round(sum(value), 2) as value, billing_month_year, " +
                "month FROM billing_detail_v" +
                " WHERE account_id = :accountId" +
                (subscriptionId != null ? " AND subscription_id = :subscriptionId" : "") +
                " AND billing_month_year BETWEEN :startDate AND :endDate" +
                " AND billing_code = :rateCode" +
                " AND bill_status in ('INVOICED','PAID')" +
                " GROUP BY billing_month_year, month, account_id" +
                " ORDER BY billing_month_year, billing_code desc");
        query.setParameter("accountId", accountId);
        if (subscriptionId != null) {
            query.setParameter("subscriptionId", subscriptionId);
        }
        query.setParameter("rateCode", rateCode);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        List<Object[]> list = query.getResultList();
        List<BillingByCodeView> views = new ArrayList<>();
        list.forEach(objects -> {
            views.add(
                    BillingByCodeView.builder()
                            .billingCode((String) objects[0])
                            .value((Double) objects[1])
                            .billingMonthYear((Date) objects[2])
                            .month((String) objects[3])
                            .build());
        });
        return views;
    }

    @Override
    public Double lifeTimeSum(String billingCode, Long accountId, Long subscriptionId) {
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery("SELECT sum(value)as lifeTimeSum FROM billing_detail_v WHERE bill_status NOT IN ('SCHEDULED', 'GENERATED') AND billing_code =:billingCode AND account_id=:accountId " +
                (subscriptionId != null ? " AND subscription_id = :subscriptionId " : ""));

        query.setParameter("billingCode", billingCode);
        query.setParameter("accountId", accountId);
        if (subscriptionId != null) {
            query.setParameter("subscriptionId", subscriptionId);
        }
        if (!query.getResultList().isEmpty()) {
            return (Double) query.getResultList().get(0);
        }
        return null;
    }

    @Override
    public List<BillingDetailView> getCumulativeSavings(Long accountId, Long subscriptionId, String startDate,
                                                        String endDate) {
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery(
                "Select value, billing_month_year, subscription_id, account_id, month,\n" +
                        ",SUM(value) over (partition by account_id  order by billing_month_year, subscription_id )" +
                        "FROM billing_detail_v" +
                        "where billing_code = 'PSAV'" +
                        (accountId != null ? " AND account_id = :accountId" : "") +
                        (subscriptionId != null ? " AND subscription_id = :subscriptionId" : "") +
                        " AND billing_month_year BETWEEN :startDate AND :endDate");
        if (accountId != null) {
            query.setParameter("accountId", accountId);
        }
        if (subscriptionId != null) {
            query.setParameter("subscriptionId", subscriptionId);
        }
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List<Object[]> list = query.getResultList();
        List<BillingDetailView> views = new ArrayList<>();
        list.forEach(objects -> {
            views.add(
                    BillingDetailView.builder()
//                            .billingCode((String) objects[0])
//                            .value((Double) objects[1])
//                            .billingMonthYear((Date) objects[2])
//                            .month((String) objects[3])
                            .build());
        });
        return views;
    }
}
