package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CustomerSubscriptionMappingRepositoryImpl implements CustomerSubscriptionMappingRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SubscriptionRateMatrixHeadRepository subscriptionRateMatrixHeadRepository;

    /*SELECT rate_code, value, subscription_rate_matrix_head_id FROM customer_subscription_mapping where
    customer_subscription_id=4
    union
    SELECT rate_code, default_value as value, subscription_rate_matrix_id as subscription_rate_matrix_head_id FROM
    subscription_rate_matrix_detail
    WHERE subscription_rate_matrix_id=5 and vary_by_customer=0;*/
    @Override
    public List<CustomerSubscriptionMapping> getMappingsForCalculationOrderedBySequence(CustomerSubscription subscription, Long subscriptionRateMatrixHeadId) {
        String queryString =
                "SELECT mapping.rate_code, mapping.value, detail.sequence_number as sequence" +
                        " FROM customer_subscription_mapping mapping, subscription_rate_matrix_detail detail" +
                        " WHERE" +
                        " mapping.customer_subscription_id = :subscriptionId" +
                        " AND detail.level = 1" +
                        " AND detail.rate_code = mapping.rate_code" +
                        " AND detail.subscription_rate_matrix_id = :subscriptionRateMatrixHeadId " +
                        "UNION " +
                        "SELECT rate_code, default_value as value, sequence_number as sequence" +
                        " FROM subscription_rate_matrix_detail" +
                        " WHERE" +
                        " subscription_rate_matrix_id = :subscriptionRateMatrixHeadId" +
                        " AND level = 1" +
                        " AND vary_by_customer = 0 " +
                        "ORDER BY sequence";
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery(queryString);
        query.setParameter("subscriptionId", subscription.getId());
        query.setParameter("subscriptionRateMatrixHeadId", subscriptionRateMatrixHeadId);
        List<Object[]> list = query.getResultList();
        List<CustomerSubscriptionMapping> mappings = new ArrayList<>();
        SubscriptionRateMatrixHead matrixHead =
                subscriptionRateMatrixHeadRepository.findById(subscriptionRateMatrixHeadId).orElse(null);
        list.forEach(objects -> {
            mappings.add(
                    CustomerSubscriptionMapping.builder()
                            .rateCode((String) objects[0])
                            .value((String) objects[1])
                            .subscriptionRateMatrixHead(matrixHead)
                            .build());
        });
        return mappings;
    }

    @Override
    public List<CustomerSubscriptionMapping> getMappingsWithStaticValues(CustomerSubscription subscription,
                                                                         Long subscriptionRateMatrixHeadId) {
        String queryString =
                "SELECT mapping.rate_code, mapping.value, detail.sequence_number" +
                        " FROM customer_subscription_mapping mapping, subscription_rate_matrix_detail detail" +
                        " WHERE" +
                        " mapping.customer_subscription_id = :subscriptionId" +
                        " AND detail.level = 0" +
                        " AND detail.rate_code = mapping.rate_code" +
                        " AND detail.subscription_rate_matrix_id = :subscriptionRateMatrixHeadId " +
                        "UNION " +
                        "SELECT rate_code, default_value as value, sequence_number" +
                        " FROM subscription_rate_matrix_detail" +
                        " WHERE" +
                        " subscription_rate_matrix_id = :subscriptionRateMatrixHeadId" +
                        " AND level = 0" +
                        " AND vary_by_customer = 0 ";
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery(queryString);
        query.setParameter("subscriptionId", subscription.getId());
        query.setParameter("subscriptionRateMatrixHeadId", subscriptionRateMatrixHeadId);
        List<Object[]> list = query.getResultList();
        List<CustomerSubscriptionMapping> mappings = new ArrayList<>();
        SubscriptionRateMatrixHead matrixHead =
                subscriptionRateMatrixHeadRepository.findById(subscriptionRateMatrixHeadId).orElse(null);
        list.forEach(objects -> {
            mappings.add(
                    CustomerSubscriptionMapping.builder()
                            .rateCode((String) objects[0])
                            .value((String) objects[1])
                            .subscriptionRateMatrixHead(matrixHead)
                            .build());
        });
        return mappings;
    }

    @Override
    public List<CustomerSubscriptionMapping> findCustomerSubscriptionMappingIncludingVaryByCustomerZero(Long customerSubscriptionId, Long subscriptionRateMatrixId) {
        String queryString = "select id, subscription_rate_matrix_id, rate_code, value, " +
                "measure_definition_id, measure, visible, uom from subscription_matrix_fin_v " +
                "where subscription_rate_matrix_id = :subscriptionRateMatrixId and (customer_subscription_id = " +
                ":customerSubscriptionId or customer_subscription_id is null)";
//        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query query = em.createNativeQuery(queryString);
        query.setParameter("subscriptionRateMatrixId", subscriptionRateMatrixId);
        query.setParameter("customerSubscriptionId", customerSubscriptionId);
        List<Object[]> list = query.getResultList();

        List<CustomerSubscriptionMapping> mappings = new ArrayList<>();
        list.forEach(result -> {
            Object[] values = (Object[]) result;
            mappings.add(
                    CustomerSubscriptionMapping.builder()
                            .id(values[0] != null ? new Long(((BigInteger) values[0]).longValue()) : null)
                            .subscriptionRateMatrixId(values[1] != null ?
                                    new Long(((BigInteger) values[1]).longValue()) : null)
                            .rateCode((String) values[2])
                            .value((String) values[3])
                            .measureDefinition(MeasureDefinitionTenantDTO.builder()
                                    .id(values[4] != null ? new Long(((BigInteger) values[4]).longValue()) : null)
                                    .measure((String) values[5])
                                    .visible(values[6] == null ? null : (Boolean) values[6])
                                    .uom((String) values[7])
                                    .build())
                            .build());
        });

        return mappings;
    }
}
