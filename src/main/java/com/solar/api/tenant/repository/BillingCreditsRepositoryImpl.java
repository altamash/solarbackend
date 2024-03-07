package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billingCredits.BillingCredits;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class BillingCreditsRepositoryImpl implements BillingCreditsRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    public List<BillingCredits> generateList(List<Object[]> list) {
        List<BillingCredits> billingCreditsList = new ArrayList<>();

        list.forEach(objects -> {
            billingCreditsList.add(BillingCredits.builder()
                    .calendarMonth((String) objects[1])
                    .creditCodeType((String) objects[3])
                    .creditCodeVal((String) objects[4])
                    .creditValue((Double) objects[6])
                    .gardenId((String) objects[7])
                    .mpa((Double) objects[11])
                    .tariffRate((Double) objects[13])
                    .build());
        });

        return billingCreditsList;
    }


    @Transactional
    @Override
    public void DumpBillingCredits() {
        Query query = em.createNativeQuery("TRUNCATE TABLE billing_credits");
        query.executeUpdate();
    }

    @Override
    public List<BillingCredits> getCreditCodeVal(List<String> creditCodeVal) {
        Query query = em.createNativeQuery("SELECT * FROM ec1001.billing_credits where credit_code_val in :creditCodeVal");
        query.setParameter("creditCodeVal", creditCodeVal);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<BillingCredits> getByGardenId(List<String> gardenId) {
        Query query = em.createNativeQuery("SELECT * FROM ec1001.billing_credits where garden_id in :gardenId");
        query.setParameter("gardenId", gardenId);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }

    @Override
    public List<BillingCredits> getByCalendarMonth(List<String> calendarMonth) {
        Query query = em.createNativeQuery("SELECT * FROM ec1001.billing_credits where calendar_month in :calendarMonth");
        query.setParameter("calendarMonth", calendarMonth);
        List<Object[]> list = query.getResultList();
        return generateList(list);
    }
}
