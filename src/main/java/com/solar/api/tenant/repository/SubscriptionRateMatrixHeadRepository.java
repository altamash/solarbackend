package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionRateMatrixHeadRepository extends JpaRepository<SubscriptionRateMatrixHead, Long> {

    SubscriptionRateMatrixHead findSubscriptionRateMatrixHeadBySubscriptionTemplate(String subscriptionTemplate);

    SubscriptionRateMatrixHead findBySubscriptionTemplate(String subscriptionTemplate);

    List<SubscriptionRateMatrixHead> findBySubscriptionCodeAndActive(String subscriptionCode, Boolean active);

    List<SubscriptionRateMatrixHead> findBySubscriptionCodeInAndActive(List<String> subscriptionCodes, Boolean active);

    @Query("SELECT rmh FROM SubscriptionRateMatrixHead rmh WHERE rmh.id in :ids")
    List<SubscriptionRateMatrixHead> findByIdsIn(@Param("ids") List<Long> ids);

    @Query("SELECT rmh FROM SubscriptionRateMatrixHead rmh" +
            " LEFT JOIN FETCH rmh.subscriptionRateMatrixDetails" +
//            " LEFT JOIN FETCH rmh.customerSubscriptionMappings" +
//            " LEFT JOIN FETCH rmh.subscriptionType" +
//            " LEFT JOIN FETCH rmh.customerSubscriptions" +
            " WHERE rmh.id = :subscriptionRateMatrixHeadId")
    SubscriptionRateMatrixHead findByIdFetchDetails(Long subscriptionRateMatrixHeadId);

    @Query("SELECT distinct srmh FROM SubscriptionRateMatrixHead srmh LEFT JOIN FETCH srmh.subscriptionRateMatrixDetails")
    List<SubscriptionRateMatrixHead> findAllFetchDetails();

}
