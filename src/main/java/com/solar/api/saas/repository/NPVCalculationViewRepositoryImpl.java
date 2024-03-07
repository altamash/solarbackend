package com.solar.api.saas.repository;

import com.solar.api.saas.model.chart.views.NPVCalculationView;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class NPVCalculationViewRepositoryImpl implements NPVCalculationViewRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Integer> getSubscriptionCount(Long accountId) {
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery("select distinct subscription_id from npv_cal_v WHERE " +
                "account_id=:accountId and rate_code='KWDC'");

        query.setParameter("accountId", accountId);
        if (!query.getResultList().isEmpty()) {
            List<Integer> subscriptionId = query.getResultList();
            return subscriptionId;
        }
        return null;
    }

    @Override
    public Integer getKWDCCount(Long accountId) {
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery("select count(rate_code) from npv_cal_v WHERE account_id=:accountId and " +
                "rate_code='KWDC'");

        query.setParameter("accountId", accountId);
        if (!query.getResultList().isEmpty()) {
            Integer kwdcCount = (Integer) query.getResultList().get(0);
            return kwdcCount;
        }
        return null;
    }

    @Override
    public Integer getDEPCount(Long accountId) {
        Query query = em.createNativeQuery("select count(rate_code) from npv_cal_v WHERE account_id=:accountId and " +
                "rate_code='DEP'");

        query.setParameter("accountId", accountId);
        if (!query.getResultList().isEmpty()) {
            Integer depCount = (Integer) query.getResultList().get(0);
            return depCount;
        }
        return null;
    }

    @Override
    public Integer getYLDCCount(Long accountId) {
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery("select count(rate_code) from npv_cal_v WHERE account_id=:accountId and " +
                "rate_code='YLD'");

        query.setParameter("accountId", accountId);
        if (!query.getResultList().isEmpty()) {
            Integer yldCount = (Integer) query.getResultList().get(0);
            return yldCount;
        }
        return null;
    }

    @Override
    public List<NPVCalculationView> getRateCodes(Long subscriptionId, Long matrixId) {
        //TODO: Convert to HQL for Audit purposes
//        SELECT * FROM npv_cal_v where subscription_id in (248,0) and matrix_id=5;
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery("SELECT rate_code, value, subscription_id FROM npv_cal_v " +
                "where subscription_id in (:subscriptionId,0) and matrix_id=:matrixId order by rate_code");

        query.setParameter("matrixId", matrixId);
        query.setParameter("subscriptionId", subscriptionId);
        List<Object[]> list = query.getResultList();
        List<NPVCalculationView> views = new ArrayList<>();
        list.forEach(objects -> {
            views.add(
                    NPVCalculationView.builder()
                            .rateCode((String) objects[0])
                            .value((String) objects[1])
                            .subscriptionId((String) objects[2])
                            .build());
        });
        return views;
    }

    @Override
    public NPVCalculationView getDEP(Long accountId, Long subscriptionId) {
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery("SELECT rate_code, value, subscription_id FROM npv_cal_v WHERE " +
                "account_id=:accountId and rate_code='DEP'");

        query.setParameter("accountId", accountId);
//        if (!query.getResultList().isEmpty()) {
//            List<Double> allDEP = query.getResultList();
//            return allDEP;
//        }
        return null;
    }

    @Override
    public NPVCalculationView getYLD(Long accountId, Long subscriptionId) {
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery("SELECT rate_code, value, subscription_id FROM npv_cal_v WHERE " +
                "account_id=:accountId and rate_code='YLD'");

        query.setParameter("accountId", accountId);
//        if (!query.getResultList().isEmpty()) {
//            List<Double> allYLD = query.getResultList();
//            return allYLD;
//        }
        return null;
    }
}

