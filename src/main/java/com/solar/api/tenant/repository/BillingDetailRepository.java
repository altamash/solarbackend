package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.billing.calculation.CalculationTrackerBillingDetailDTO;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface BillingDetailRepository extends JpaRepository<BillingDetail, Long> {
    BillingDetail findByBillingHeadAndBillingCode(BillingHead billingHead, String billingCode);

    List<BillingDetail> findByBillingHead(BillingHead billingHead);

    @Query("select max(lineSeqNo) from BillingDetail bd where billingHead=:billingHead")
    public Integer getMaxLineSeq(BillingHead billingHead);

    @Query("select max(lineSeqNo) from BillingDetail bd where billingHead=:billingHead")
    BillingDetail findByBillingHeadAndLi(BillingHead billingHead);

    BillingDetail findByBillingCodeAndLineSeqNo(String billingCode, Integer lineSeqNo);

    BillingDetail findByBillingCodeAndLineSeqNoAndBillingHead(String billingCode, Integer lineSeqNo,
                                                              BillingHead billingHead);
    List<BillingDetail> findByBillingHeadIdAndAddToBillAmount(Long headId, Boolean addToBillAmount);
    @Query("select bd from BillingDetail bd where bd.billingHead.id = :headId and bd.addToBillAmount = :addToBillAmount order by bd.lineSeqNo asc")
    List<BillingDetail> findByBillingHeadIdAndAddToBillAmountOrderById(@Param("headId") Long headId, @Param("addToBillAmount") Boolean addToBillAmount);
    @Transactional
    @Modifying
    @Query("update BillingDetail bd set bd.value=0.0 where bd.billingHead.id in (:billHeadIds)")
    void updateBillDetailForTermination(@Param("billHeadIds") List<Long> billHeadIds);

    @Query(" select new com.solar.api.tenant.mapper.billing.calculation.CalculationTrackerBillingDetailDTO(bd.billingCode , bd.value) " +
            "from BillingDetail bd " +
            "where bd.billingHead.id = :billHeadId ")
    List<CalculationTrackerBillingDetailDTO> getBillingDetails(@Param("billHeadId") Long billHeadId);
}
