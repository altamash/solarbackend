package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.subscription.CustomerSubscriptionsListView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerSubscriptionsListRepository extends JpaRepository<CustomerSubscriptionsListView, String>,
        CustomerSubscriptionsListRepositoryCustom {
}
