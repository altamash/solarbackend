package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billingCredits.BillingCredits;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillingCreditsRepositoryCustom {

    void DumpBillingCredits();

    List<BillingCredits> getCreditCodeVal(@Param("creditCodeVal") List<String> creditCodeVal);

    List<BillingCredits> getByGardenId(@Param("gardenId") List<String> gardenId);

    List<BillingCredits> getByCalendarMonth(@Param("calendarMonth") List<String> calendarMonth);
}
