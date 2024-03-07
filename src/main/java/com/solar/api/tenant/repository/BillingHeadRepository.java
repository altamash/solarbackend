package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.billing.billingHead.*;
import com.solar.api.tenant.mapper.billing.calculation.CalTrackerGraphTemplate;
import com.solar.api.tenant.mapper.billing.calculation.CalculationTrackerTemplate;
import com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile;
import com.solar.api.tenant.mapper.tiles.calculationTracker.CalculationTrackerTile;
import com.solar.api.tenant.mapper.tiles.dataexport.payment.DataExportPaymentTile;
import com.solar.api.tenant.mapper.tiles.paymentManagement.CustomerPaymentDashboardTile;
import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface BillingHeadRepository extends JpaRepository<BillingHead, Long> {

    List<BillingHead> findByBillStatus(String status);

    List<BillingHead> findByUserAccount(User userAccount);

    @Query("select bh from BillingHead bh where bh.billStatus != 'CONV' and bh.subscriptionId = :subscriptionId")
    List<BillingHead> findBySubscriptionId(Long subscriptionId);

    @Query("select DISTINCT bh from BillingHead bh LEFT JOIN FETCH bh.billingDetails where bh.billStatus != 'CONV'" +
            " and bh.subscriptionId = :subscriptionId order by bh.id asc")
    List<BillingHead> findBySubscriptionIdFetchBillingDetails(Long subscriptionId);

    @Query(" select new com.solar.api.tenant.mapper.tiles.calculationTracker.CalculationTrackerTile" +
            "(bh.id ,bh.billingMonthYear ,bh.amount,bh.billStatus,bh.dueDate," +
            "concat(u.firstName,concat(' ',u.lastName)), cud.customerType,e.contactPersonEmail,e.contactPersonPhone" +
            ",ed.uri,cs.extSubsId," +
            "cds.errorMessage,bh.billSkip,bh.userAccountId,cds.attemptCount," +
            "edsd.refType,CONCAT(pl.add1, ', ', pl.add2, ', ', pl.add3,', ', pl.zipCode),edsd.subscriptionName,edsd.mpJson,cs.gardenSrc ) " +
            " from BillingHead bh " +
            " inner join User u on u.acctId = bh.userAccountId " +
            " inner join UserLevelPrivilege prv on prv.user.acctId = u.acctId " +
            " inner join Entity e on e.id =  prv.entity.id " +
            " left join CustomerDetail cud on cud.entityId =  e.id " +
            " left join EntityDetail ed on ed.entity.id = e.id " +
            " left join CustomerSubscription cs on cs.id = bh.subscriptionId" +
            " left join CalculationDetails cds on cds.sourceId = bh.id" +
            " left join ExtDataStageDefinition edsd on edsd.subsId = cs.extSubsId " +
            " left join PhysicalLocation pl on pl.id = edsd.siteLocationId  " +
            " where edsd.subsStatus = 'ACTIVE' and bh.billingMonthYear in (:period) and cs.userAccount.acctId is not null and cs.startDate is not null ")
    List<CalculationTrackerTile> findCalculationTrackerTileBySubsId(@Param("period") List<String> period);

    // TODO: need to remove
    @Query(value = " select bh.id as id , bh.billing_month_year as period , " +
            "bh.amount as amount, bh.bill_status as status, bh.due_date as dueDate," +
            "cd.error_message as error, u.acct_id as accountId, bh.cust_prod_id as customerProdId, cd.attempt_count as reAttemptCount," +
            "concat(u.first_name,concat(' ',u.last_name)) as customerName, cud.customer_type as customerType," +
            "e.contact_person_email as customerEmail, e.contact_person_phone as customerPhone,ed.uri as profileUrl,bh.bill_skip as billSkip " +
            "from billing_head bh " +
            " left join user u on u.acct_id = bh.user_account_id " +
            " left join user_level_privilege prv " +
            " on prv.account_id = u.acct_id  " +
            " left join entity e" +
            " on e.id =  prv.entity_id " +
            " left join entity_detail ed " +
            " on ed.entity_id = e.id " +
            " left join customer_detail cud " +
            " on cud.entity_id = e.id " +
            " left join calculation_details cd " +
            " on cd.source_id = bh.id " +
            "where bh.cust_prod_id in (:subscriptionIds) ",
            nativeQuery = true)
    List<CalculationTrackerTemplate> findBySubscriptionIdList(@Param("subscriptionIds") List<String> subscriptionIds);

    //    @Query(value = " SELECT new com.solar.api.tenant.model.billing.billingHead (bh.id, bh.amount, bh.bill_status, bh.billing_month_year, bh.subscription_id, bh.due_date )" +
    @Query(value = "SELECT bh.* FROM billing_head bh WHERE bh.billing_month_year IN :monthYears AND bh.subscription_id = :subscriptionId " +
            "ORDER BY STR_TO_DATE(CONCAT('01-', bh.billing_month_year),'%d-%m-%Y') DESC", nativeQuery = true)
    List<BillingHead> findLastTwelveMonths(List<String> monthYears, Long subscriptionId);

    BillingHead findBySubscriptionIdAndBillingMonthYear(Long subscriptionId, String billingMonthYear);

    BillingHead findByCustProdIdAndBillingMonthYear(String custProdId, String billingMonthYear);

    /**
     * Lazy Loading Alternate
     *
     * @param id
     * @return
     */
    @Query("select bh from BillingHead bh LEFT JOIN FETCH bh.billingDetails where bh.id = :id")
    BillingHead findByIdFetchBillingDetails(Long id);

    @Query("SELECT bh.id from BillingHead bh WHERE subscriptionId=:subscriptionId and id>=:id order by id")
    List<Object> findBySubscriptionIdAndId(@Param("subscriptionId") Long subscriptionId, @Param("id") Long id);

    @Query("SELECT bh from BillingHead bh, CustomerSubscription cc WHERE bh.subscriptionId=cc.id and cc" +
            ".subscriptionStatus=:subscriptionStatus")
    List<BillingHead> findBySubscriptionStatus(@Param("subscriptionStatus") String subscriptionStatus);

    // select SUM(amount) from billing_head where bill_status="GENERATED";
    @Query("SELECT SUM(amount) from BillingHead bh WHERE billStatus=:billStatus")
    Double getReceivableAggregate(@Param("billStatus") String billStatus);

    // SELECT id, bill_status FROM ec1001.billing_head where id= (select max(bh.id) from ec1001.billing_head bh where
    // bh.subscription_id=8 and bh.id<78)
    @Query("SELECT billHead FROM BillingHead billHead" +
            " WHERE billHead.id = (select MAX(bh.id) FROM BillingHead bh WHERE bh.subscriptionId = :subscriptionId " +
            "AND bh.id < :billHeadId)")
    BillingHead findLastBillHead(@Param("subscriptionId") Long subscriptionId, @Param("billHeadId") Long billHeadId);

    @Query("SELECT billHead FROM BillingHead billHead" +
            " WHERE billHead.id = (select MAX(bh.id) FROM BillingHead bh WHERE bh.custProdId = :subscriptionId " +
            "AND bh.id < :billHeadId)")
    BillingHead findLastBillHeadV1(@Param("subscriptionId") String subscriptionId, @Param("billHeadId") Long billHeadId);

    // SELECT * FROM ec1001.billing_head where subscription in (select id from customer_subscription where
    // subscription_rate_matrix_id=41);
    @Query("SELECT billHead FROM BillingHead billHead" +
            " where billHead.billStatus=:billStatus" +
            " and billHead.billingMonthYear=:billingMonthYear" +
            " and billHead.subscriptionId in (select cs.id from CustomerSubscription cs where cs" +
            ".subscriptionRateMatrixId=:rateMatrixId)")
    List<BillingHead> findByMonthYearAndRateMatrixId(@Param("billingMonthYear") String billingMonthYear, @Param(
            "rateMatrixId") Long rateMatrixId, @Param("billStatus") String billStatus);

    @Query("SELECT billHead FROM BillingHead billHead" +
            " where billHead.billStatus in (:billStatus)" +
            " and billHead.billingMonthYear=:billingMonthYear" +
            " and billHead.subscriptionId in (select cs.id from CustomerSubscription cs where cs" +
            ".subscriptionRateMatrixId in (:rateMatrixIds))")
    List<BillingHead> findByMonthYearAndRateMatrixIds(@Param("billingMonthYear") String billingMonthYear, @Param(
            "rateMatrixIds") List<Long> rateMatrixIds, @Param("billStatus") List<String> billStatus);

    // SELECT * FROM ec1001.billing_head where bill_status='GENERATED' and billing_month_year='01-2020' and
    // subscription in (select id from customer_subscription where subscription_type='CSGR');
    @Query("SELECT billHead FROM BillingHead billHead" +
            " where billHead.billStatus in (:billStatus)" +
            " and billHead.billingMonthYear=:billingMonthYear" +
            " and billHead.subscriptionId in (select cs.id from CustomerSubscription cs where cs" +
            ".subscriptionType=:subscriptionType)")
    List<BillingHead> findByMonthYearAndSubscriptionType(@Param("billingMonthYear") String billingMonthYear, @Param(
            "subscriptionType") String subscriptionType, @Param("billStatus") List<String> billStatus);

    // select * from billing_head where invoice_id in (SELECT id FROM ec1001.billing_invoice where id not in (select
    // invoice_ref_id from payment_transaction_head))
    @Query("select bh from BillingHead bh where bh.invoice in (SELECT inv FROM BillingInvoice inv WHERE inv not in " +
            "(select invoice from PaymentTransactionHead))")
    List<BillingHead> findAllWithoutPaymentTransactionHead();

    BillingHead findByInvoice(BillingInvoice invoice);

    @Query("SELECT bh FROM BillingHead bh where bh.billStatus='INVOICED' and bh.subscriptionId=:subscriptionId")
    List<BillingHead> invoiceBySubscription(@Param("subscriptionId") Long subscriptionId);

    /*select invoice_id as "invoice_ref_id", "BILLING" as "payment_code", amount as amt from billing_head where
    bill_status='INVOICED' and billing_month_year='01-2020' and subscription_id in
            (select id from customer_subscription where subscription_status='ACTIVE' and subscription_type='CSGF')*/
    @Query("select new com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionPreprocess((select concat(u" +
            ".firstName, ' ', u.lastName) from User u where u.acctId=bh.userAccountId) as customer_name," +
            "bh.invoice.id, 'BILLING', bh.amount) from BillingHead bh" +
            " where bh.billingMonthYear=:billingMonthYear and bh.billStatus='INVOICED' and bh.subscriptionId in " +
            "(select id from CustomerSubscription where subscriptionStatus='ACTIVE' and " +
            "subscriptionType=:subscriptionType)")
    List<PaymentTransactionPreprocess> getPreprocessTransactionWithSubscriptionType(@Param("subscriptionType") String subscriptionType, @Param("billingMonthYear") String billingMonthYear);

    @Query("select new com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionPreprocess((select concat(u" +
            ".firstName, ' ', u.lastName) from User u where u.acctId=bh.userAccountId) as customer_name, " +
            "bh.invoice.id, 'BILLING', bh.amount) from BillingHead bh" +
            " where bh.billingMonthYear=:billingMonthYear and bh.billStatus='INVOICED' and bh.subscriptionId in " +
            "(select id from CustomerSubscription where subscriptionStatus='ACTIVE' and " +
            "subscriptionRateMatrixId=:subscriptionRateMatrixId)")
    List<PaymentTransactionPreprocess> getPreprocessTransactionWithSubscriptionRateMatrixId(@Param(
            "subscriptionRateMatrixId") Long subscriptionRateMatrixId, @Param("billingMonthYear") String billingMonthYear);

    @Query("Select bh from BillingHead bh where bh.id=(Select max(id) from BillingHead where " +
            "subscriptionId=:subscriptionId ) ")
    BillingHead getBillHeadMonth(@Param("subscriptionId") Long subscriptionId);

    @Query("    select (select MAX(b.id) FROM BillingHead b " +
            "    WHERE b.subscriptionId=:subscriptionId  AND b.id > MAX(bh.id) and b.billStatus not in (:billStatus))" +
            " as nextBhId " +
            "    from BillingHead bh " +
            "    where bh.subscriptionId=:subscriptionId and bh.billStatus in (:billStatus)")
    Object checkFutureBillExists(@Param("subscriptionId") Long subscriptionId,
                                 @Param("billStatus") List<String> billStatus);

    @Transactional
    @Modifying
    @Query("update BillingHead bh set bh.billStatus=:billStatus, bh.updatedAt=:updatedAt " +
            " where bh.id in (:billHeadIds)")
    void updateBillHeadForTermination(@Param("billStatus") String billStatus, @Param("billHeadIds") List<Long> billHeadIds, @Param("updatedAt") LocalDateTime updatedAt);

    @Query("select new com.solar.api.tenant.mapper.billing.billingHead.ACHFileDTO(h.id ," +
            " s.id, info.accountNumber, abs(round(h.amount - pHead.net,2)), h.invoice.id, " +
            " DATE_FORMAT(current_date()+1,'%Y-%m-%d') ," +
            " mPN.value, " +
            " concat(replace(u.firstName,',',''), ' ', replace(u.lastName,',','')) as name," +
            " case when (h.amount - pHead.net) < 0 then 'CR' else 'DR' end as paymentInformation," +
            " info.routingNumber, " +
            "  case when info.accountType like 'Checking%' and (h.amount - pHead.net) < 0 then '22'" +
            "       when info.accountType like 'Checking%' and ((h.amount - pHead.net) > 0 OR (h.amount - pHead.net) = 0) then '27'" +
            "       when info.accountType like 'Saving%' and (h.amount - pHead.net) < 0 then '32'" +
            "       when info.accountType like 'Saving%' and ((h.amount - pHead.net) > 0 OR (h.amount - pHead.net) = 0) then '37'" +
            "                end as transactionCode , pHead.paymentId) " +
            " from BillingHead h " +
            " INNER JOIN User u ON h.userAccountId = u.acctId " +
            " INNER JOIN CustomerSubscription s ON h.subscriptionId = s.id" +
            " INNER JOIN CustomerSubscriptionMapping mPN ON s.id = mPN.subscription.id " +
            " INNER JOIN CustomerSubscriptionMapping mPSRC ON s.id = mPSRC.subscription.id " +
            " INNER JOIN PaymentInfo info ON mPSRC.value = info.paymentSrcAlias " +
            " INNER join PaymentTransactionHead pHead " +
            " on h.invoice.id = pHead.invoice.id " +

            " WHERE h.billStatus = 'INVOICED' " +
            " and h.billingMonthYear =:billingMonthYear " +
            " and u.ccd =:ccd " +
            " and s.subscriptionRateMatrixId in (:subscriptionRateMatrixIdsCSV)" +
            " and mPSRC.rateCode = 'PSRC' " +
            " and mPN.rateCode = 'PN' " +
            " and info.paymentSource = 'ACH' " +
            " and info.portalAccount.acctId = s.userAccount.acctId ")
    List<ACHFileDTO> generateACHCSV(@Param("subscriptionRateMatrixIdsCSV") List<Long> subscriptionRateMatrixIdsCSV,
                                    @Param("ccd") Boolean ccd,
                                    @Param("billingMonthYear") String billingMonthYear); // only payment type ach


    //unpaid invoices
    @Query("select new com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionHeadDetailDTO(" +
            "bh.id as head_id ,concat(u.firstName, ' ', u.lastName) as customer_name ,bh.amount as invoice_amount,bh.subscriptionId as subscription_id ," +
            "bh.invoice.id as invoice_id, (bh.amount - pth.net)  as outstanding_amount, st.subscriptionName as subscription_name, csm.value as premise, '' as account_number, '' as payment_mode) " +
            " from BillingHead bh " +
            " INNER JOIN User u ON bh.userAccountId = u.acctId" +
            " INNER JOIN CustomerSubscription cs ON bh.subscriptionId = cs.id" +
            " INNER JOIN SubscriptionType st ON  cs.subscriptionType = st.code" +
            " INNER JOIN CustomerSubscriptionMapping csm ON cs.id = csm.subscription.id " +
            " INNER join PaymentTransactionHead pth ON bh.invoice.id = pth.invoice.id " +
            " where bh.billingMonthYear=:billingMonthYear and bh.billStatus='INVOICED' and (bh.amount - pth.net)>0 and bh.subscriptionId in " +
            "(select id from CustomerSubscription where subscriptionStatus='ACTIVE' and csm.rateCode = 'PN' and " +
            "subscriptionType=:subscriptionType)")
    List<PaymentTransactionHeadDetailDTO> getPaymentTransactionWithSubscriptionTypeForPremise(@Param("subscriptionType") String subscriptionType, @Param("billingMonthYear") String billingMonthYear);

    //unpaid invoices for account number
    @Query("select new com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionHeadDetailDTO(" +
            "bh.id as head_id ,concat(u.firstName, ' ', u.lastName) as customer_name ,bh.amount as invoice_amount,bh.subscriptionId as subscription_id ," +
            "bh.invoice.id as invoice_id, (bh.amount - pth.net)  as outstanding_amount, st.subscriptionName as subscription_name, '' as premise, info.accountNumber as account_number,upper(info.paymentSource) as payment_mode) " +
            " from BillingHead bh " +
            " INNER JOIN User u ON bh.userAccountId = u.acctId" +
            " INNER JOIN CustomerSubscription cs ON bh.subscriptionId = cs.id" +
            " INNER JOIN SubscriptionType st ON  cs.subscriptionType = st.code" +
            " INNER JOIN CustomerSubscriptionMapping csm ON cs.id = csm.subscription.id " +
            " INNER JOIN PaymentInfo info ON RTRIM(LTRIM(upper(csm.value))) = RTRIM(LTRIM(upper(info.paymentSrcAlias))) " +
            " INNER join PaymentTransactionHead pth ON bh.invoice.id = pth.invoice.id " +
            " where bh.billingMonthYear=:billingMonthYear and bh.billStatus='INVOICED' and (bh.amount - pth.net) > 0 and" +
            " info.portalAccount.acctId = cs.userAccount.acctId and bh.subscriptionId in " +
            "(select id from CustomerSubscription where subscriptionStatus='ACTIVE' and csm.rateCode = 'PSRC' and " +
            "subscriptionType=:subscriptionType)")
    List<PaymentTransactionHeadDetailDTO> getPaymentTransactionWithSubscriptionTypeForAcctNo(@Param("subscriptionType") String subscriptionType, @Param("billingMonthYear") String billingMonthYear);

    //unreconciled invoices
    @Query("select new com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionLineItemsDetailDTO(" +
            "bh.id as head_id ,bh.amount as invoice_amount,bh.subscriptionId as subscription_id ," +
            "bh.invoice.id as invoice_id, (bh.amount - pth.net)  as outstanding_amount, st.subscriptionName as subscription_name, " +
            "cs.subscriptionTemplate as garden, pth.paymentId as payment_id, pth.jobId as job_id," +
            "ptd.referenceId as reference_id, ptd.amt as detail_amount, ptd.issuer as payment_mode, " +
            "ptd.instrumentNum as instrument_id, ptd.tranDate as payment_Date, " +
            "ptd.payDetId as payment_detail_Id, ptd.notes as notes, ptd.batchNo as batch, ptd.issuerReconStatus as status) " +
            // "case when ptd.issuerReconStatus like 'COMPLETED' or ptd.issuerReconStatus like 'REVERSAL' or ptd.issuerReconStatus like 'REVERSED' " +
            // " then 'false' else 'true' end as action)"+
            " from BillingHead bh " +
            " INNER JOIN CustomerSubscription cs ON bh.subscriptionId = cs.id" +
            " INNER JOIN SubscriptionType st ON st.code = cs.subscriptionType" +
            " INNER join PaymentTransactionHead pth ON bh.invoice.id = pth.invoice.id " +
            " INNER join PaymentTransactionDetail ptd ON pth.paymentId = ptd.paymentTransactionHead.paymentId " +
            " where  bh.billingMonthYear=:billingMonthYear and (ptd.status='PAID-UNRECONCILED' or ptd.status='PAID-RECONCILED') and  bh.billStatus='INVOICED' " +
            " and bh.subscriptionId in " +
            "(select id from CustomerSubscription where subscriptionStatus='ACTIVE' and " +
            "subscriptionType=:subscriptionType)")
    List<PaymentTransactionLineItemsDetailDTO> getPaymentUnreconciledHeadDetailWithSubscriptionType(@Param("subscriptionType") String subscriptionType, @Param("billingMonthYear") String billingMonthYear);

    //reversal invoices
    @Query("select new com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionLineItemsDetailDTO(" +
            "bh.id as head_id ,bh.amount as invoice_amount,bh.subscriptionId as subscription_id ," +
            "bh.invoice.id as invoice_id, (bh.amount - pth.net)  as outstanding_amount, st.subscriptionName as subscription_name, " +
            "cs.subscriptionTemplate as garden, pth.paymentId as payment_id, pth.jobId as job_id," +
            "ptd.referenceId as reference_id, ptd.amt as detail_amount, ptd.issuer as payment_mode, " +
            "ptd.instrumentNum as instrumentNum, ptd.tranDate as payment_Date, " +
            "ptd.payDetId as payment_detail_Id, ptd.notes as notes, ptd.batchNo as batch, ptd.issuerReconStatus as status," +
            " ptd.source as source, ptd.sourceId as sourceId)" +
//            ", case when ptd.issuerReconStatus like 'IN-PROGRESS' or ptd.issuerReconStatus like 'PAID'" +
//            " then 'true' else 'false' end as action)"+
            " from BillingHead bh " +
            " INNER join PaymentTransactionHead pth ON bh.invoice.id = pth.invoice.id " +
            " INNER join PaymentTransactionDetail ptd ON pth.paymentId = ptd.paymentTransactionHead.paymentId " +
            " INNER JOIN CustomerSubscription cs ON bh.subscriptionId = cs.id" +
            " INNER JOIN SubscriptionType st ON st.code = cs.subscriptionType" +
            " where  bh.billingMonthYear=:billingMonthYear and (ptd.status='PAID-UNRECONCILED' or ptd.status='PAID-RECONCILED') and  bh.billStatus='INVOICED' " +
            " and bh.subscriptionId in " +
            "(select id from CustomerSubscription where subscriptionStatus='ACTIVE' and " +
            "subscriptionType=:subscriptionType)")
    List<PaymentTransactionLineItemsDetailDTO> getPaymentReversalHeadDetailWithSubscriptionType(@Param("subscriptionType") String subscriptionType, @Param("billingMonthYear") String billingMonthYear);

    //unpaid graph
    @Query("select new com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionGraphDTO(" +
            "(bh.amount - pth.net) as amount, bh.billingMonthYear as billing_month)" +
            " from BillingHead bh " +
            " INNER JOIN CustomerSubscription cs ON bh.subscriptionId = cs.id" +
            " INNER JOIN SubscriptionType st ON  cs.subscriptionType = st.code" +
            " INNER join PaymentTransactionHead pth ON bh.invoice.id = pth.invoice.id " +
            " where SUBSTRING(bh.billingMonthYear,INSTR(bh.billingMonthYear,'-')+1,LENGTH(bh.billingMonthYear)) =:billingYear and bh.billStatus='INVOICED' and (bh.amount - pth.net)>0 and bh.subscriptionId in " +
            "(select id from CustomerSubscription where subscriptionStatus='ACTIVE' and " +
            "subscriptionType=:subscriptionType)")
    List<PaymentTransactionGraphDTO> getPaymentTransactionUnpaid(@Param("subscriptionType") String subscriptionType, @Param("billingYear") String billingMonthYear);


    //unreconciled graph
    @Query("select new com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionGraphDTO(" +
            " (ptd.amt)  as amount,  bh.billingMonthYear as billing_month) " +
            " from BillingHead bh " +
            " INNER JOIN CustomerSubscription cs ON bh.subscriptionId = cs.id" +
            " INNER JOIN SubscriptionType st ON  cs.subscriptionType = st.code" +
            " INNER join PaymentTransactionHead pth ON bh.invoice.id = pth.invoice.id " +
            " INNER JOIN PaymentTransactionDetail ptd ON pth.paymentId = ptd.paymentTransactionHead.paymentId " +
            " where SUBSTRING(bh.billingMonthYear,INSTR(bh.billingMonthYear,'-')+1,LENGTH(bh.billingMonthYear)) =:billingYear and (ptd.status='PAID-UNRECONCILED') and " +
            " (ptd.issuerReconStatus  not like 'REVERSED' and ptd.issuerReconStatus not like 'REVERSAL') and bh.billStatus='INVOICED' " +
            " and bh.subscriptionId in " +
            "(select id from CustomerSubscription where subscriptionStatus='ACTIVE' and " +
            "subscriptionType=:subscriptionType)")
    List<PaymentTransactionGraphDTO> getPaymentTransactionUnreconciled(@Param("subscriptionType") String subscriptionType, @Param("billingYear") String billingYear);

    //reconciled graph
    @Query("select new com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionGraphDTO(" +
            "(ptd.amt)  as amount,  bh.billingMonthYear as billing_month) " +
            " from BillingHead bh " +
            " INNER JOIN CustomerSubscription cs ON bh.subscriptionId = cs.id" +
            " INNER JOIN SubscriptionType st ON  cs.subscriptionType = st.code" +
            " INNER join PaymentTransactionHead pth ON bh.invoice.id = pth.invoice.id " +
            " INNER JOIN PaymentTransactionDetail ptd ON pth.paymentId = ptd.paymentTransactionHead.paymentId " +
            " where SUBSTRING(bh.billingMonthYear,INSTR(bh.billingMonthYear,'-')+1,LENGTH(bh.billingMonthYear)) =:billingYear and (ptd.status='PAID-RECONCILED') and  bh.billStatus='INVOICED' " +
            " and bh.subscriptionId in " +
            "(select id from CustomerSubscription where subscriptionStatus='ACTIVE' and " +
            "subscriptionType=:subscriptionType)")
    List<PaymentTransactionGraphDTO> getPaymentTransactionReconciled(@Param("subscriptionType") String subscriptionType, @Param("billingYear") String billingYear);

    //unreconciled invoices for autoReconcile
    @Query("select new com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionLineItemsDetailDTO(" +
            "bh.id as head_id ,bh.amount as invoice_amount,bh.subscriptionId as subscription_id ," +
            "bh.invoice.id as invoice_id, ptd.amt  as outstanding_amount, st.subscriptionName as subscription_name, " +
            "cs.subscriptionTemplate as garden, pth.paymentId as payment_id, pth.jobId as job_id," +
            "ptd.referenceId as reference_id, ptd.amt as detail_amount, ptd.issuer as payment_mode, " +
            "ptd.instrumentNum as instrument_id, ptd.tranDate as payment_Date, " +
            "ptd.payDetId as payment_detail_Id, ptd.notes as notes, ptd.batchNo as batch, ptd.issuerReconStatus as status, ptd.issuerId as payment_mode_Id) " +
            " from BillingHead bh " +
            " INNER join PaymentTransactionHead pth ON bh.invoice.id = pth.invoice.id " +
            " INNER join PaymentTransactionDetail ptd ON pth.paymentId = ptd.paymentTransactionHead.paymentId " +
            " INNER JOIN CustomerSubscription cs ON bh.subscriptionId = cs.id" +
            " INNER JOIN SubscriptionType st ON st.code = cs.subscriptionType" +
            " where  (ptd.status='PAID-UNRECONCILED') and  bh.billStatus='INVOICED' " +
            " and bh.subscriptionId in " +
            "(select id from CustomerSubscription where subscriptionStatus='ACTIVE')")
    List<PaymentTransactionLineItemsDetailDTO> getPaymentDetailAutoReconcileRecords();

    @Transactional
    @Modifying
    @Query(value = "UPDATE billing_head bh SET bh.payment_locked = :lockInd WHERE bh.id IN (:ids)", nativeQuery = true)
    public void updateBillingHeadPaymentLockByIds(@Param("ids") List<Long> ids, @Param("lockInd") boolean lockInd);

    @Transactional
    @Modifying
    @Query(value = "UPDATE billing_head bh SET bh.reconcile_locked = :lockInd WHERE bh.id IN (:ids)", nativeQuery = true)
    public void updateBillingHeadReconcileLockByIds(@Param("ids") List<Long> ids, @Param("lockInd") boolean lockInd);

    @Transactional
    @Modifying
    @Query(value = "UPDATE billing_head bh SET bh.reverse_locked = :lockInd WHERE bh.id IN (:ids)", nativeQuery = true)
    public void updateBillingHeadReverseLockByIds(@Param("ids") List<Long> ids, @Param("lockInd") boolean lockInd);

    @Query(value = "select bh.id from BillingHead bh where paymentLocked =:lockInd")
    public List<Long> getBillingHeadByPaymentLocked(@Param("lockInd") boolean lockInd);

    @Query(value = "select bh.id from BillingHead bh where reverseLocked =:lockInd")
    public List<Long> getBillingHeadByReverseLocked(@Param("lockInd") boolean lockInd);

    @Query(value = "select bh.id from BillingHead bh where reconcileLocked =:lockInd")
    public List<Long> getBillingHeadByReconcileLocked(@Param("lockInd") boolean lockInd);


    @Query(value = "SELECT DATE_FORMAT(str_to_date(concat('01-',bh.billing_month_year),'%d-%m-%Y'), '%b-%Y') as Period, " +
            "bh.bill_status as billStatus, " +
            "sum(bh.amount) as Amount, " +
            "Count(bh.bill_status) as statusCount " +
            "FROM billing_head bh " +
            "where bh.billing_month_year in (:periods) " +
            "and bh.bill_status in (:status)  " +
            "GROUP BY bh.bill_status,bh.billing_month_year " +
            "order by str_to_date(concat('01-',bh.billing_month_year),'%d-%m-%Y') desc", nativeQuery = true)
    public List<CalTrackerGraphTemplate> getBillStatusGraph(@Param("periods") List<String> periods, @Param("status") List<String> status);

    @Query(value = "SELECT DATE_FORMAT(str_to_date(concat('01-',bh.billing_month_year),'%d-%m-%Y'), '%b-%Y') as Period, " +
            "bh.bill_status as billStatus, " +
            "sum(bh.amount) as Amount, " +
            "Count(bh.bill_status) as statusCount " +
            "FROM billing_head bh " +
            "where bh.billing_month_year in (:periods) and bh.bill_status in (:status) " +
            "GROUP BY bh.bill_status,bh.billing_month_year " +
            "order by str_to_date(concat('01-',bh.billing_month_year),'%d-%m-%Y') desc", nativeQuery = true)
    public List<CalTrackerGraphTemplate> getStatusWiseGraphLM(@Param("periods") List<String> periods, @Param("status") List<String> status);

    @Query(value = "SELECT DATE_FORMAT(str_to_date(concat('01-',bh.billing_month_year),'%d-%m-%Y'), '%b-%Y') as Period, " +
            "bh.bill_status as billStatus, " +
            "sum(bh.amount) as Amount, " +
            "Count(bh.bill_status) as statusCount " +
            "FROM billing_head bh " +
            "where (str_to_date(concat('01-',bh.billing_month_year),'%d-%m-%Y') between  now() - INTERVAL 1 month and  now() ) " +
            "GROUP BY bh.bill_status,bh.billing_month_year " +
            "order by str_to_date(concat('01-',bh.billing_month_year),'%d-%m-%Y') desc", nativeQuery = true)
    public List<CalTrackerGraphTemplate> getStatusWiseGraphCM();
    @Query(" select new com.solar.api.tenant.mapper.tiles.paymentManagement.CustomerPaymentDashboardTile( bh.id , bh.invoice.id ," +
            "DATE_FORMAT(str_to_date(concat('01-',bh.billingMonthYear),'%d-%m-%Y'), '%b-%Y') , " +
            "cs.extSubsId ,bh.billStatus , bh.amount , " +
            "pth.net, concat(u.firstName,concat(' ',u.lastName)) , " +
            "cud.customerType , e.contactPersonEmail ," +
            "e.contactPersonPhone ,ed.uri,ptd.referenceId,scm.referenceId ,bh.dueDate ," +
            "ptd.tranDate,cs.subscriptionTemplate,cs.subscriptionType,u.acctId , ptd.issuer, edsd.refType,edsd.subscriptionName) from BillingHead bh " +
            " left join User u on u.acctId = bh.userAccountId " +
            " left join UserLevelPrivilege prv on prv.user.acctId = u.acctId " +
            " left join Entity e on e.id = prv.entity.id " +
            " left join EntityDetail ed on ed.entity.id = e.id " +
            " left join CustomerDetail cud on cud.entityId = e.id " +
            " left join PaymentTransactionHead pth on pth.invoice.id = bh.invoice.id " +
            " left join PaymentTransactionDetail ptd on ptd.paymentTransactionHead.paymentId = pth.paymentId " +
            " left join StripeCustomerMapping scm on scm.accountId = u.acctId " +
            " left join CustomerSubscription cs on cs.id = bh.subscriptionId " +
            " left join ExtDataStageDefinition edsd on edsd.subsId = cs.extSubsId " +
            "where bh.billStatus in (:billingStatus) and bh.billingMonthYear in (:period) and " +
            "edsd.subsStatus = 'ACTIVE' and cs.userAccount.acctId is not null and cs.startDate is not null")
    List<CustomerPaymentDashboardTile> getCustomerPaymentDashboardTile(@Param("billingStatus") List<String> billingStatus,
                                                                       @Param("period") List<String> period);

    @Query(" select new com.solar.api.tenant.mapper.tiles.paymentManagement.CustomerPaymentDashboardTile(bh.id , bh.invoice.id ," +
            "DATE_FORMAT(str_to_date(concat('01-',bh.billingMonthYear),'%d-%m-%Y'), '%b-%Y') , " +
            "cs.extSubsId ,bh.billStatus , bh.amount , " +
            "pth.net, concat(u.firstName,concat(' ',u.lastName)) , " +
            "cud.customerType , e.contactPersonEmail ," +
            "e.contactPersonPhone ,ed.uri,ptd.referenceId,scm.referenceId,bh.dueDate ," +
            "ptd.tranDate,cs.subscriptionTemplate,cs.subscriptionType,u.acctId, ptd.issuer, edsd.refType,edsd.subscriptionName) from BillingHead bh " +
            " left join User u on u.acctId = bh.userAccountId " +
            "left join UserLevelPrivilege prv on prv.user.acctId = u.acctId " +
            "left join Entity e  on e.id =  prv.entity.id " +
            " left join EntityDetail ed on ed.entity.id = e.id  " +
            " left join CustomerDetail cud on cud.entityId = e.id " +
            " left join PaymentTransactionHead pth on pth.invoice.id = bh.invoice.id " +
            " left join  PaymentTransactionDetail ptd  on  ptd.paymentTransactionHead.paymentId = pth.paymentId " +
            " left join StripeCustomerMapping scm on scm.accountId = u.acctId " +
            " left join CustomerSubscription cs on cs.id = bh.subscriptionId " +
            " left join ExtDataStageDefinition edsd on edsd.subsId = cs.extSubsId " +
            "where bh.billStatus in (:billingStatus) and bh.billingMonthYear in (:period) and u.userName =:userName and " +
            "edsd.subsStatus = 'ACTIVE' and cs.userAccount.acctId is not null and cs.startDate is not null ")
    List<CustomerPaymentDashboardTile> getCustomerPaymentDashboardTile(@Param("billingStatus") List<String> billingStatus,
                                                                       @Param("period") List<String> period,
                                                                       @Param("userName") String username);

    @Query("select bh from BillingHead bh where bh.billingMonthYear in (:period) and bh.billStatus='PENDING'")
    List<BillingHead> getPendingBillsByBillingPeriod(@Param("period") List<String> period);

    @Query(" select new com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile(count(bh.billStatus) , bh.billStatus) " +
            " from BillingHead bh " +
            " left join User u on u.acctId = bh.userAccountId " +
            " left join PaymentTransactionHead pth on pth.invoice.id = bh.invoice.id " +
            " left join  PaymentTransactionDetail ptd  on  ptd.paymentTransactionHead.paymentId = pth.paymentId " +
            " left join StripeCustomerMapping scm on scm.accountId = u.acctId " +
            " left join CustomerSubscription cs on cs.id = bh.subscriptionId " +
            "where bh.billStatus in (:billingStatus)  and " +
            "bh.billingMonthYear in (:periods) " +
            "GROUP BY bh.billStatus ")
    List<AdminBillingDashboardTile> getBillingByStatus(@Param("billingStatus") List<String> billingStatus,
                                                       @Param("periods") List<String> periods);

    @Query(" select new com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile(count(cud.customerType) ,cud.customerType, bh.billStatus) " +
            "from BillingHead bh " +
            "inner join User u on u.acctId = bh.userAccountId " +
            "inner join UserLevelPrivilege prv on prv.user.acctId = u.acctId " +
            "inner join Entity e  on e.id =  prv.entity.id " +
            "inner join EntityDetail ed on ed.entity.id = e.id  " +
            "inner join CustomerDetail cud on cud.entityId = e.id " +
            "left join PaymentTransactionHead pth on pth.invoice.id = bh.invoice.id " +
            "left join  PaymentTransactionDetail ptd  on  ptd.paymentTransactionHead.paymentId = pth.paymentId " +
            "where bh.billStatus in (:billingStatus)  and " +
            "bh.billingMonthYear in (:periods) " +
            "GROUP BY cud.customerType, bh.billStatus ")
    List<AdminBillingDashboardTile> getCustomerTypeDataGroupByBillingStatus(@Param("billingStatus") List<String> billingStatus,
                                                                            @Param("periods") List<String> periods);


    @Query("select new com.solar.api.tenant.mapper.billing.tiles.AdminBillingDashboardTile( bh.billStatus, bh.billingMonthYear, count(bh.billStatus)) " +
            " from BillingHead bh " +
            " left join User u on u.acctId = bh.userAccountId " +
            " left join PaymentTransactionHead pth on pth.invoice.id = bh.invoice.id " +
            " left join  PaymentTransactionDetail ptd  on  ptd.paymentTransactionHead.paymentId = pth.paymentId " +
            " left join StripeCustomerMapping scm on scm.accountId = u.acctId " +
            " left join CustomerSubscription cs on cs.id = bh.subscriptionId " +
            "where bh.billStatus in (:billingStatus)  and " +
            "bh.billingMonthYear in (:periods) " +
            "GROUP BY  bh.billStatus,bh.billingMonthYear")
    List<AdminBillingDashboardTile> getBillingStatusComparisonData(@Param("periods") List<String> periods,
                                                                   @Param("billingStatus") List<String> billingStatus);

    @Query(" select new com.solar.api.tenant.mapper.tiles.calculationTracker.CalculationTrackerTile" +
            "(bh.id ,bh.billingMonthYear ,bh.amount,bh.billStatus,bh.dueDate," +
            "concat(u.firstName,concat(' ',u.lastName)), cud.customerType,e.contactPersonEmail,e.contactPersonPhone" +
            ",ed.uri,cs.extSubsId," +
            "cds.errorMessage,bh.billSkip,bh.userAccountId,cds.attemptCount,concat('',:sourceName) ) " +
            " from BillingHead bh " +
            " left join User u on u.acctId = bh.userAccountId " +
            " left join Account ac on ac.user.acctId = u.acctId" +
            " left join UserLevelPrivilege prv on prv.user.acctId = u.acctId " +
            " left join Entity e on e.id =  prv.entity.id " +
            " left join CustomerDetail cud on cud.entityId =  e.id " +
            " left join EntityDetail ed on ed.entity.id = e.id " +
            " left join CustomerSubscription cs on cs.id = bh.subscriptionId" +
            " left join CalculationDetails cds on cds.sourceId = bh.id" +
            " where cs.extSubsId in (:subscriptionIds) and bh.billingMonthYear in (:period) and bh.billStatus in(:status) ")
    List<CalculationTrackerTile> findCalculationTrackerTileBySubsIdAndStatus(@Param("subscriptionIds") List<String> subscriptionId,
                                                                             @Param("period") List<String> period,
                                                                             @Param("sourceName") String sourceName,
                                                                             @Param("status") List<String> status);

    @Query("select bh from BillingHead bh " +
            "Inner join CustomerSubscription cs " +
            "on cs.id = bh.subscriptionId " +
            "where cs.userAccount.acctId in (:accountId)")
    List<BillingHead> getBillingInfo(@Param("accountId") List<Long> accountId);


    @Query(" select new com.solar.api.tenant.mapper.tiles.dataexport.payment.DataExportPaymentTile(prv.entity.entityName,bh.billingMonthYear , edsd.refType ," +
            "cs.extSubsId , bh.billStatus ,bh.amount, pth.net,bh.dueDate,ptd.tranDate) from BillingHead bh " +
            "inner join CustomerSubscription cs on cs.id = bh.subscriptionId " +
            "inner join UserLevelPrivilege prv on prv.user.acctId = cs.userAccount.acctId " +
            "inner join CustomerDetail cud on cud.entityId = prv.entity.id " +
            "left join PaymentTransactionHead pth on pth.invoice.id = bh.invoice.id " +
            "left join  PaymentTransactionDetail ptd  on  ptd.paymentTransactionHead.paymentId = pth.paymentId " +
            "inner join ExtDataStageDefinition  edsd on edsd.subsId = cs.extSubsId " +
            "inner join CalculationDetails cd on cd.sourceId = bh.id " +
            "where cud.customerType in (:custType) and prv.user.acctId in (:acctIds) " +
            "and bh.billingMonthYear in (:period) and bh.id in (:billIds) " +
            "and bh.billStatus in (:status) and edsd.refId in (:source) and cd.errorMessage in (:error) ")
    Page<DataExportPaymentTile> getPaymentExportTile(@Param("custType") List<String> customerTypes,
                                                     @Param("acctIds") List<Long> accountIds,
                                                     @Param("period") List<String> period,
                                                     @Param("billIds") List<Long> billIds,
                                                     @Param("status") List<String> status,
                                                     @Param("source") List<String> source,
                                                     @Param("error") List<String> error,
                                                     Pageable pageable);

    @Query("select bh from BillingHead bh where bh.subscriptionId in (:subscriptionIds) and bh.billingMonthYear in (:period)")
    List<BillingHead> findBillingProjectionBySubsIdAndPeriod(@Param("subscriptionIds") List<Long> subscriptionIds, @Param("period") List<String> periods);


}
