package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.subscription.subscriptionType.SubscriptionTypeDTO;
import com.solar.api.tenant.model.subscription.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionTypeRepository extends JpaRepository<SubscriptionType, Long> {

    SubscriptionType findBySubscriptionName(String subscriptionName);

    SubscriptionType findByCode(String code);

    List<SubscriptionType> findByCodeIn(List<String> codes);

    @Query("select new com.solar.api.tenant.mapper.subscription.subscriptionType.SubscriptionTypeDTO(st.primaryGroup as primaryGroup, st.code as code) from SubscriptionType st")
    List<SubscriptionTypeDTO> findAllSubscriptionTypesWithPrimaryGroup();

    }
