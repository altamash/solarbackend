package com.solar.api.tenant.repository;

import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.tenant.mapper.subscription.customerSubscription.SubscriptionCountDTO;
import com.solar.api.tenant.mapper.user.UserCountDTO;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionsListView;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerSubscriptionsListRepositoryCustom {

    List<CustomerSubscriptionsListView> getAll();

    List<CustomerSubscriptionsListView> getByAccount(@Param("accountId") List<Long> accountId);

    List<CustomerSubscriptionsListView> getBySubscriptionType(@Param("subscriptionType") List<String> subscriptionType);

    List<CustomerSubscriptionsListView> getBySubscriptionId(@Param("subscriptionId") List<Long> subscriptionId);

    List<CustomerSubscriptionsListView> getByGardenSRC(@Param("gardenSRC") List<String> gardenSRC);

    List<CustomerSubscriptionsListView> getByPremiseNumber(@Param("premiseNumber") List<String> premiseNumber);

    List<SubscriptionCountDTO> countByCustomer();
}
