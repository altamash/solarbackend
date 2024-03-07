package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.pvmonitor.MonitorReading;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class MonitorReadingRepositoryImpl implements MonitorReadingRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public MonitorReading getLastRecord(Long userId) {
        Query q = em.createQuery("select mr from MonitorReading mr where userId = :userId order by id desc", MonitorReading.class);
        q.setParameter("userId", userId);
        q.setMaxResults(1);
        return (MonitorReading) (!q.getResultList().isEmpty() ? q.getResultList().get(0) : null);
    }
}
