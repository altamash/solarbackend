package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billingCredits.BillingCredits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BillCreditRepository extends JpaRepository<BillingCredits, Long> {

    //    select sum(credit_value) from billing_credits
//    where credit_code_val='302149247' and garden_id='SRC038294'
//    group by credit_code_val, garden_id, calendar_month;
    @Query("select sum(creditValue) from BillingCredits bc " +
            "where creditCodeVal=:creditCodeVal and gardenId=:gardenId and calendarMonth=:calendarMonth " +
            "group by creditCodeVal, gardenId, calendarMonth")
    Double getBillingCredits(String creditCodeVal, String gardenId, String calendarMonth);

    @Query("select mpa from BillingCredits bc " +
            "where creditCodeVal=:creditCodeVal and gardenId=:gardenId and calendarMonth=:calendarMonth " +
            "group by creditCodeVal, gardenId, calendarMonth, mpa")
    Double getMpa(String creditCodeVal, String gardenId, String calendarMonth);

}
