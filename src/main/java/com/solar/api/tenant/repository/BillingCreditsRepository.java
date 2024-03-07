package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.billingCredits.BillingCreditResult;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billingCredits.BillingCredits;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BillingCreditsRepository extends JpaRepository<BillingCredits, Long> {

    List<BillingCredits> findByCreditCodeVal(String premiseNo);

    List<BillingCredits> findByCreditCodeValAndGardenIdAndCalendarMonthOrderByCreatedAtDesc(String premiseNumber,
                                                                                            String gardenId,
                                                                                            String calendarMonth);

    @Query("select mpa from BillingCredits bc " +
            "where creditCodeVal=:creditCodeVal and gardenId=:gardenId and calendarMonth=:calendarMonth " +
            "group by mpa")
    Double getMpa(String creditCodeVal, String gardenId, String calendarMonth);

    @Query("select sum(creditValue) from BillingCredits bc " +
            "where creditCodeVal=:creditCodeVal and gardenId=:gardenId and calendarMonth=:calendarMonth " +
            "group by creditCodeVal, gardenId, calendarMonth, mpa")
    public Double getCreditValueSum(String creditCodeVal, String gardenId, String calendarMonth);

    @Query("SELECT bc FROM BillingCredits bc " +
            "WHERE creditCodeVal IN (SELECT value FROM CustomerSubscriptionMapping WHERE rateCode= 'PN')")
    List<BillingCredits> getAllWithContract();

    @Query("SELECT bc FROM BillingCredits bc " +
            "WHERE creditCodeVal NOT IN (SELECT value FROM CustomerSubscriptionMapping WHERE rateCode= 'PN')")
    List<BillingCredits> getAllWithoutContract();

    List<BillingCredits> findAllByCalendarMonth(String calendarMonth);

    List<BillingCredits> findAllByCalendarMonthIsBefore(String calendarMonth);

    BillingCredits findByCreditCodeValAndGardenIdAndCalendarMonthAndLineSeqNo(String premiseNumber, String gardenId,
                                                                              String calendarMonth,
                                                                              Integer lineSequence2);

    @Query("select new com.solar.api.tenant.mapper.billingCredits.BillingCreditResult(sum(creditValue) as " +
            "actual_bill_credit, mpa) " +
            "from BillingCredits " +
            "where creditCodeVal=:premiseNumber and gardenId=:gardenId and calendarMonth=:calendarMonth group by mpa")
    BillingCreditResult getABCREAndMPA(String premiseNumber, String gardenId, String calendarMonth);

    @Query(value = "SELECT * FROM billing_credits WHERE credit_code_val LIKE %:creditCodeVal% AND garden_id LIKE %:gardenId% AND calendar_month LIKE %:calendarMonth%", nativeQuery = true)
    public Page<BillingCredits> getAll(String creditCodeVal, String gardenId, String calendarMonth, Pageable pageable);

    Optional<BillingCredits> findByGardenIdAndCalendarMonthAndCreditCodeVal(String gardenId, String calendarMonth, String creditCodeVal);

    @Query("select credit from BillingCredits credit where credit.creditCodeVal =:premiseNo and credit.calendarMonth=:month and credit.gardenId=:gardenId")
    BillingCredits findByPremiseNoAndMonthAndGardenSrc(@Param("premiseNo") String premiseNo, @Param("month") String month, @Param("gardenId") String gardenId);

    @Query("select credit from BillingCredits credit where credit.creditCodeVal in (:premiseNos) and credit.calendarMonth=:month and credit.gardenId in (:gardenIds)")
    List<BillingCredits> findByPremiseNoInAndMonthInAndGardenSrcIn(@Param("premiseNos") List<String> premiseNos, @Param("month") String month, @Param("gardenIds") List<String> gardenIds);
    @Query("select credit from BillingCredits credit where credit.subscriptionCode in (:subscriptionIds) and credit.calendarMonth in (:period)")
    List<BillingCredits> findBillingCreditsBySubsIdAndPeriod(@Param("subscriptionIds") List<String> subscriptionIds, @Param("period") List<String> periods);
    @Query("select new com.solar.api.tenant.mapper.billingCredits.BillingCreditResult(sum(bc.creditValue), bc.mpa) " +
            "from BillingCredits bc " +
            "left join CustomerSubscription cs on cs.extSubsId= bc.subscriptionCode " +
            "where cs.id=:subscriptionId and bc.calendarMonth=:calendarMonth group by bc.mpa")
    BillingCreditResult getABCREAndMPA(Long subscriptionId, String calendarMonth);
}
