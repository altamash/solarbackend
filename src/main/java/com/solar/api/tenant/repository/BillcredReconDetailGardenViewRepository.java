package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.trueup.BillcredReconDetailGardenView;
import com.solar.api.tenant.model.trueup.CsgBillcreReconTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface BillcredReconDetailGardenViewRepository extends JpaRepository<BillcredReconDetailGardenView, Long> {

    /*    @Query(value = "select new com.solar.api.model.process.reconciliation.trueup.CsgBillcreReconTemplate(" +
                "sum(billing_credits) total_credits, sum(billed_amt) total_billed, sum(paid_amt) total_payment," +
                " ((SUM(billing_credits) * 0.9) - sum(paid_amt))," +
                " sum(billing_credits) * 0.9 subscription_cost," +
                " subscription_id, garden_name, garden_id, premise_no, :start, :end)" +
                " from billcred_recon_detail_garden_v" +
                " where garden_id = :gardenId and billing_month_year between :start and :end" +
                " group by subscription_id, garden_id, garden_name, premise_no", nativeQuery = true)*/
    @Query(value = "select new com.solar.api.tenant.model.trueup.CsgBillcreReconTemplate(" +
            "sum(v.billingCredits), sum(v.billedAmt), sum(v.paidAmt)," +
            " ((SUM(v.billingCredits) * 0.9) - sum(v.paidAmt))," +
            " sum(v.billingCredits) * 0.9," +
            " v.subscriptionId, v.gardenName, v.gardenId, v.premiseNo)" +
            " from BillcredReconDetailGardenView v" +
            " where v.gardenId = :gardenId and v.billingMonthYear between :start and :end" +
            " group by v.subscriptionId, v.gardenId, v.gardenName, v.premiseNo")
    List<CsgBillcreReconTemplate> generateByGarden(String gardenId, Date start, Date end);

    @Query(value = "select new com.solar.api.tenant.model.trueup.CsgBillcreReconTemplate(" +
            "sum(v.billingCredits), sum(v.billedAmt), sum(v.paidAmt)," +
            " ((SUM(v.billingCredits) * 0.9) - sum(v.paidAmt))," +
            " sum(v.billingCredits) * 0.9," +
            " v.subscriptionId, v.gardenName, v.gardenId, v.premiseNo)" +
            " from BillcredReconDetailGardenView v" +
            " where v.subscriptionId in (:subscriptionId) and v.billingMonthYear between :start and :end" +
            " group by v.subscriptionId, v.gardenId, v.gardenName, v.premiseNo")
    List<CsgBillcreReconTemplate> generateBySubscriptionId(List<Long> subscriptionId, Date start, Date end);
}
