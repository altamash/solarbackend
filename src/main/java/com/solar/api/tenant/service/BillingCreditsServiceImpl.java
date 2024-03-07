package com.solar.api.tenant.service;

import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.billingCredits.BillingCreditsPMDTO;
import com.solar.api.tenant.mapper.billingCredits.BillingCreditsTempMapper;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.billingCredits.BillingCreditsCsv;
import com.solar.api.tenant.model.billingCredits.BillingCreditsTempStage;
import com.solar.api.tenant.model.billingCredits.SearchParamsBillingCredits;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillingCreditsServiceImpl implements BillingCreditsService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BillingCreditsRepository billingCreditsRepository;
    @Autowired
    private BillingCreditsRepositoryCustom billingCreditsRepositoryCustom;
    @Autowired
    private BillingCreditsCsvRepository billingCreditsCsvRepository;

    @Autowired
    private BillingCreditsTempStageRepository billingCreditsTempStageRepository;
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;
    @Autowired
    private MonitorReadingDailyRepository dailyRepository;

    /**
     * @param billingCredits
     * @return
     */
    @Override
    public List<BillingCredits> addOrUpdateBillingCredits(List<BillingCredits> billingCredits) {
        return billingCreditsRepository.saveAll(billingCredits);
    }

    /**
     * @param billingCredits
     * @return
     */
    @Override
    public List<BillingCredits> save(List<BillingCredits> billingCredits) {
        return billingCreditsRepository.saveAll(billingCredits);
    }

    @Override
    public void mapBillingCredits() {
        List<BillingCreditsCsv> valuesWithS = billingCreditsCsvRepository.findAll();
        List<BillingCredits> billingCreditsList = BillingCreditsTempMapper.toBillingCredits(valuesWithS);
        save(billingCreditsList);
        deleteAllBillingCredits();
    }

    /**
     * Using Native Query
     */
    @Override
    public void deleteAll() {
        try {
            billingCreditsRepositoryCustom.DumpBillingCredits();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * BillingCreditsCsv
     */

    @Override
    public List<BillingCreditsCsv> findBillingCreditsCsvByPaymentType(String paymentType) {
        return billingCreditsCsvRepository.findBillingCreditsCsvByPaymentType(paymentType);
    }

    @Override
    public List<BillingCreditsCsv> saveAll(List<BillingCreditsCsv> billingCreditsCsv) {
        return billingCreditsCsvRepository.saveAll(billingCreditsCsv);
    }

    @Override
    public void deleteAllBillingCredits() {
        billingCreditsCsvRepository.deleteAll();
    }

    @Override
    public Page<BillingCredits> comprehensiveSearch(Pageable pageable, SearchParamsBillingCredits searchParamsBillingCredits) {
        return billingCreditsRepository.getAll(searchParamsBillingCredits.getCreditCodeVal() != null
                ? searchParamsBillingCredits.getCreditCodeVal() : "", searchParamsBillingCredits.getGardenId() != null
                ? searchParamsBillingCredits.getGardenId() : "", searchParamsBillingCredits.getCalendarMonth() != null
                ? searchParamsBillingCredits.getCalendarMonth() : "", pageable);
    }

    @Override
    public List<BillingCredits> findAll() {
        return billingCreditsRepository.findAll();
    }

    @Override
    public BillingCredits findByPremiseNoAndMonthAndGardenSrc(String premiseNo, String month, String gardenId) {
        return billingCreditsRepository.findByPremiseNoAndMonthAndGardenSrc(premiseNo, month, gardenId);
    }

    @Override
    public List<BillingCredits> findAllByPremiseNoAndMonthAndGardenSrc(List<String> premiseNos, String month, List<String> gardenIds) {
        return billingCreditsRepository.findByPremiseNoInAndMonthInAndGardenSrcIn(premiseNos, month, gardenIds);
    }

    @Async
    @Override
    public void dataConversionForBillingCredits(Long jobId) {
        List<BillingCredits> finalBillingCredits = new ArrayList<>();
        List<BillingCreditsTempStage> billingCreditsTempStageList = billingCreditsTempStageRepository.findAll();
        List<String> premiseNoList = billingCreditsTempStageList.stream().map(BillingCreditsTempStage::getCreditCodeVal).distinct().collect(Collectors.toList());
        List<String> gardenSrcList = billingCreditsTempStageList.stream().map(BillingCreditsTempStage::getGardenId).distinct().collect(Collectors.toList());
        String month = billingCreditsTempStageList.stream().map(BillingCreditsTempStage::getCalendarMonth).findFirst().get();
        List<BillingCredits> existingCreditList = findAllByPremiseNoAndMonthAndGardenSrc(premiseNoList, month, gardenSrcList);
        List<BillingCreditsTempStage> filteredBillingCreditsPMDTO = filterBillingCreditsPMDTO(existingCreditList, billingCreditsTempStageList);

        filteredBillingCreditsPMDTO.stream().forEach(tempCredit -> {
            Long creditId = null;
            Optional<BillingCredits> foundCreditItem = existingCreditList.stream()
                    .filter(billingCredit ->
                            Boolean.TRUE.equals(billingCredit.getImported())
                                    && tempCredit.getGardenId().equalsIgnoreCase(billingCredit.getGardenId())
                                    && month.equalsIgnoreCase(billingCredit.getCalendarMonth())
                                    && tempCredit.getCreditCodeVal().equalsIgnoreCase(billingCredit.getCreditCodeVal()))
                    .findFirst();
            if (foundCreditItem.isPresent()) {
                creditId = foundCreditItem.get().getId();
            }
            String subsId = customerSubscriptionRepository.findSubsByGardenSRCAndPremiseNo(tempCredit.getGardenId(), tempCredit.getCreditCodeVal());
            finalBillingCredits.add(BillingCredits.builder().id(creditId).tariffRate(0d).subscriptionCode(subsId)
                    .mpa(tempCredit.getMpa()).jobId(jobId).importType(AppConstants.CSV).gardenId(tempCredit.getGardenId())
                    .creditValue(0d).creditCodeVal(tempCredit.getCreditCodeVal()).creditCodeType(AppConstants.CREDIT_CODE_TYPE_S).calendarMonth(month).imported(false).build());

        });
        if (finalBillingCredits.size() > 0) {
            billingCreditsRepository.saveAll(finalBillingCredits);
            billingCreditsTempStageRepository.deleteAll();
        }
    }

    private List<BillingCreditsTempStage> filterBillingCreditsPMDTO(List<BillingCredits> existingCreditList, List<BillingCreditsTempStage> billingCreditsTempStageList) {
        return billingCreditsTempStageList.stream()
                .filter(billingCreditPM -> {
                    return existingCreditList.stream()
                            .noneMatch(billingCredit ->
                                    (billingCredit.getImported() == null || !billingCredit.getImported())
                                            && billingCredit.getGardenId().equals(billingCreditPM.getGardenId())
                                            && billingCredit.getCalendarMonth().equals(billingCreditPM.getCalendarMonth())
                                            && billingCredit.getCreditCodeVal().equals(billingCreditPM.getCreditCodeVal()));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BillingCredits> manageBillingCreditsForProjection(List<CustomerSubscription> customerSubscriptions, List<String> months, Long jobId) {
        List<BillingCredits> finalBillingCredits = new ArrayList<>();
        List<String> subsIds = customerSubscriptions.stream().map(CustomerSubscription::getExtSubsId).distinct().collect(Collectors.toList());
        List<BillingCreditsPMDTO> billingCreditsPMDTO = dailyRepository.findMonthlySummedDataForBillingCredits(months, subsIds);
        List<BillingCredits> existingBillingCredits = billingCreditsRepository.findBillingCreditsBySubsIdAndPeriod(subsIds, months);

        billingCreditsPMDTO.stream().forEach(reading -> {

            months.stream().forEach(month -> {
                Long creditId = null;
                Optional<BillingCredits> foundCreditItem = existingBillingCredits.stream()
                        .filter(billingCredit ->
                                reading.getSubsId().equalsIgnoreCase(billingCredit.getSubscriptionCode())
                                        && month.equalsIgnoreCase(billingCredit.getCalendarMonth()))
                        .findFirst();
                if (foundCreditItem.isPresent()) {
                    creditId = foundCreditItem.get().getId();
                }
                finalBillingCredits.add(BillingCredits.builder().id(creditId).tariffRate(0d).subscriptionCode(reading.getSubsId())
                        .jobId(jobId).mpa(reading.getMpa()).importType(AppConstants.PROJECTION).gardenId(reading.getGardenSrc())
                        .creditValue(0d).creditCodeType(AppConstants.CREDIT_CODE_TYPE_S).calendarMonth(month).imported(true).build());
            });
        });
        Map<String, BillingCredits> uniqueEntries = new HashMap<>();
        for (BillingCredits credit : finalBillingCredits) {
            String key = credit.getCalendarMonth() + credit.getSubscriptionCode();
            if (!uniqueEntries.containsKey(key) || credit.getId() != null) {
                uniqueEntries.put(key, credit);
            }
        }

        List<BillingCredits> distinctCredits = new ArrayList<>(uniqueEntries.values());
        return billingCreditsRepository.saveAll(distinctCredits);
    }
}
