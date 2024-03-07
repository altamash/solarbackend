package com.solar.api.tenant.repository;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class BannerRepositoryCustomImpl implements BannerRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public void deleteByCompanyPreferenceId(Long companyPreferenceId) {
        Query query = em.createNativeQuery("delete from banner where company_preference_id = :companyPreferenceId");
        query.setParameter("companyPreferenceId", companyPreferenceId);
        query.executeUpdate();
    }
}
