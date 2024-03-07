package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.subscription.customerSubscription.SubscriptionCountDTO;
import com.solar.api.tenant.mapper.user.UserCountDTO;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionsListView;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CustomerSubscriptionsListRepositoryImpl implements CustomerSubscriptionsListRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    public List<SubscriptionCountDTO> generateCountList(List<Object[]> list) {
        List<SubscriptionCountDTO> views = new ArrayList<>();
        list.forEach(objects -> {
            views.add(SubscriptionCountDTO.builder()
                    .count((BigInteger) objects[1])
                    .gardenName((String) objects[0])
                    .build());
        });
        if (views.isEmpty()) {
            return null;
        }
        return views;
    }

    public List<CustomerSubscriptionsListView> generateList(List<Object[]> list) {
        List<CustomerSubscriptionsListView> views = new ArrayList<>();
        list.forEach(objects -> {
            views.add(CustomerSubscriptionsListView.builder()
                    .accountId((BigInteger) objects[0])
                    .firstName((String) objects[1])
                    .lastName((String) objects[2])
                    .subscriptionId((BigInteger) objects[3])
                    .subscriptionStatus((String) objects[4])
                    .subscriptionRateMatrixId((BigInteger) objects[5])
                    .subscriptionType((String) objects[6])
                    .gardenName((String) objects[7])
                    .gardenSrc((String) objects[8])
                    .premiseNo((String) objects[9])
                    .startDate((String) objects[10])
                    .build());
        });
        if (views.isEmpty()) {
            return null;
        }
        return views;
    }

    @Override
    public List<CustomerSubscriptionsListView> getAll() {
        Query query = em.createNativeQuery("SELECT * FROM cs_subscriber_list_v");
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<CustomerSubscriptionsListView> getByAccount(List<Long> accountId) {
        Query query = em.createNativeQuery("SELECT * FROM cs_subscriber_list_v where account_id in :accountId");
        query.setParameter("accountId", accountId);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override

    public List<CustomerSubscriptionsListView> getBySubscriptionType(List<String> subscriptionType) {
        Query query = em.createNativeQuery("SELECT * FROM cs_subscriber_list_v where subscription_type in " +
                ":subscriptionType");
        query.setParameter("subscriptionType", subscriptionType);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<CustomerSubscriptionsListView> getBySubscriptionId(List<Long> subscriptionId) {
        Query query = em.createNativeQuery("SELECT * FROM cs_subscriber_list_v where subscription_id in " +
                ":subscriptionId");
        query.setParameter("subscriptionId", subscriptionId);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<CustomerSubscriptionsListView> getByGardenSRC(List<String> gardenSRC) {
        Query query = em.createNativeQuery("SELECT * FROM cs_subscriber_list_v where garden_src in :gardenSRC");
        query.setParameter("gardenSRC", gardenSRC);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<CustomerSubscriptionsListView> getByPremiseNumber(List<String> premiseNumber) {
        Query query = em.createNativeQuery("SELECT * FROM cs_subscriber_list_v where premise_no in :premiseNumber");
        query.setParameter("premiseNumber", premiseNumber);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<SubscriptionCountDTO> countByCustomer() {
        Query query = em.createNativeQuery("SELECT distinct(edsd.ref_type) as gardenName, count(cs.account_id) as count " +
                "FROM customer_subscription cs, ext_data_stage_definition edsd where cs.ext_subs_id = edsd.subs_id " +
                "group by edsd.ref_type order by count desc limit 5");
        List<Object[]> list = query.getResultList();
        return generateCountList(list);
    }
}
