package com.solar.api.tenant.repository;


import com.solar.api.tenant.mapper.billing.billingHead.ACHFileDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class ACHCSVRepositoryCustomImpl implements ACHCSVRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ACHFileDTO> getACHCSV() {
/*
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery();

        Root<BillingHead> billingHeadTable = query.from(BillingHead.class);
        Join<BillingHead, User> userJoin = billingHeadTable.join("billingHeadDAO", JoinType.INNER);
        Join<BillingHead, CustomerSubscription> csJoin = billingHeadTable.join("csDAO", JoinType.INNER);
        Join<CustomerSubscription, CustomerSubscriptionMapping> csmJoin = csJoin.join("csmDAO", JoinType.INNER);
*/



        List<Object[]> results = em.createQuery(
                "SELECT h.id 'Head Id' " +
                        "        FROM BillingHead h " +
                        "        INNER JOIN User u ON " +
                        "        h.userAccountId = u.acctId "+

                        "        INNER JOIN CustomerSubscription s ON " +
                        "        h.subscriptionId = s.id " ).getResultList();

        for (Object[] result : results) {
            System.out.println(result[0] + " " + result[1] + " - " + result[2]);
        }

        return null;
    }
}
