package com.solar.api.saas.service.widget;

import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.chart.views.NPVCalculationView;
import com.solar.api.saas.repository.BillingDetailViewRepository;
import com.solar.api.saas.repository.NPVCalculationViewRepository;
import com.solar.api.saas.service.process.calculation.RateFunctions;
import com.solar.api.tenant.model.AnalyticalCalculation;
import com.solar.api.tenant.model.AnalyticalCalculationArchive;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRatesDerived;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.SubscriptionRatesDerivedRepository;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.service.AnalyticalCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.solar.api.tenant.mapper.AnalyticalCalculationMapper.toAnalyticalCalculationArchives;
import static com.solar.api.tenant.mapper.AnalyticalCalculationMapper.toUpdatedAnalyticalCalculationTemp;

@Service
//@Transactional("masterTransactionManager")
public class InfoServiceImpl implements InfoService {

    private final UserRepository userRepository;
    private final BillingHeadRepository billingHeadRepository;

    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private SubscriptionRatesDerivedRepository ratesDerivedRepository;
    @Autowired
    private BillingDetailViewRepository billingDetailViewRepository;
    @Autowired
    private NPVCalculationViewRepository npvCalculationViewRepository;
    @Autowired
    private RateFunctions rateFunctions;
    @Autowired
    private Utility utility;
    @Autowired
    private AnalyticalCalculationService analyticalCalculationService;

    private Double estimatedProduction = null;
    private Double cumulativeSavings = null;
    private Double currentCSI = null;

    InfoServiceImpl(UserRepository userRepository, BillingHeadRepository billingHeadRepository) {
        this.userRepository = userRepository;
        this.billingHeadRepository = billingHeadRepository;
    }

    @Override
    public Long getCustomersCountByStatus(String status) {
        return userRepository.getCustomersCountByStatus(status);
    }

    @Override
    public Double getReceivableAggregate(String billStatus) {
        Double total = billingHeadRepository.getReceivableAggregate(billStatus);
        return total == null ? 0 : total;
    }

    @Override
    public Double findLifeTimeSum(String billingCode, Long accountId, Long subscriptionId) {
        return billingDetailViewRepository.lifeTimeSum(billingCode, accountId, subscriptionId);
    }

    @Override
    public void analyze() {
        List<AnalyticalCalculation> analyticalCalculationList = analyticalCalculationService.findAll();
        analyticalCalculationService.saveAllArchives(toAnalyticalCalculationArchives(analyticalCalculationList));
        analyticalCalculationService.deleteAll();
    }


    @Override
    public void npv() {
        List<Map<String, Object>> mappings = new ArrayList<>();
        List<AnalyticalCalculationArchive> analyticalCalculationArchiveList = new ArrayList<>();
        List<CustomerSubscription> customerSubscriptions = subscriptionRepository.findBySubscriptionStatus("ACTIVE");
        if (!customerSubscriptions.isEmpty()) {
            customerSubscriptions.forEach(customerSubscription -> {
                Map<String, Object> currentIteration = calculateNPV(customerSubscription.getUserAccount().getAcctId(), customerSubscription.getId());
                mappings.add(currentIteration);
                analyticalCalculationArchiveList.add(AnalyticalCalculationArchive.builder()
                        .accountId(customerSubscription.getUserAccount().getAcctId())
                        .subscriptionId(customerSubscription.getId())
                        .analysis("NPV")
                        .currentValue((Double) currentIteration.get("NPV"))
                        .scope(customerSubscription.getSubscriptionType())
                        .lastUpdatedDatetime(new Date())
                        .build());
            });

            mapOldValue(analyticalCalculationArchiveList);
        }
    }

    @Override
    public void absav() {
        List<AnalyticalCalculationArchive> analyticalCalculationArchiveList = new ArrayList<>();
        List<CustomerSubscription> customerSubscriptions = subscriptionRepository.findBySubscriptionStatus("ACTIVE");
        if (!customerSubscriptions.isEmpty()) {
            customerSubscriptions.forEach(customerSubscription -> {
                analyticalCalculationArchiveList.add(calculateLifetimeSavings(customerSubscription.getUserAccount().getAcctId(),
                        customerSubscription.getId(), customerSubscription.getSubscriptionType()));

            });
        }
        mapOldValue(analyticalCalculationArchiveList);
    }

    @Override
    public void tpf() {
        List<AnalyticalCalculationArchive> analyticalCalculationArchiveList = new ArrayList<>();
        List<CustomerSubscription> customerSubscriptions = subscriptionRepository.findBySubscriptionStatus("ACTIVE");
        if (!customerSubscriptions.isEmpty()) {
            customerSubscriptions.forEach(customerSubscription -> {
                analyticalCalculationArchiveList.add(calculateTreesPlantingFactor(customerSubscription.getUserAccount().getAcctId(),
                        customerSubscription.getId(), customerSubscription.getSubscriptionType()));

            });
        }
        mapOldValue(analyticalCalculationArchiveList);
    }

    @Override
    public void mpa() {
        List<AnalyticalCalculationArchive> analyticalCalculationArchiveList = new ArrayList<>();
        List<CustomerSubscription> customerSubscriptions = subscriptionRepository.findBySubscriptionStatus("ACTIVE");
        if (!customerSubscriptions.isEmpty()) {
            for (int i = 0; i <= 100; i++) {
                analyticalCalculationArchiveList.add(calculateLifetimeProduction(customerSubscriptions.get(i).getUserAccount().getAcctId(),
                        customerSubscriptions.get(i).getId(), customerSubscriptions.get(i).getSubscriptionType()));
            }
//            customerSubscriptions.forEach(customerSubscription -> {
//                analyticalCalculationArchiveList.add(calculateLifetimeProduction(customerSubscription.getUserAccount().getAcctId(),
//                        customerSubscription.getId(), customerSubscription.getSubscriptionType()));
//
//            });
        }
        mapOldValue(analyticalCalculationArchiveList);
    }

    @Override
    public void lifeTimeSumBatch() {

        List<AnalyticalCalculationArchive> analyticalCalculationArchiveList = new ArrayList<>();
        List<CustomerSubscription> customerSubscriptions = subscriptionRepository.findBySubscriptionStatus("ACTIVE");
        if (!customerSubscriptions.isEmpty()) {
            customerSubscriptions.forEach(customerSubscription -> {
                analyticalCalculationArchiveList.add(calculateLifetimeSavings(customerSubscription.getUserAccount().getAcctId(),
                        customerSubscription.getId(), customerSubscription.getSubscriptionType()));
                analyticalCalculationArchiveList.add(calculateLifetimeProduction(customerSubscription.getUserAccount().getAcctId(),
                        customerSubscription.getId(), customerSubscription.getSubscriptionType()));
                analyticalCalculationArchiveList.add(calculateTreesPlantingFactor(customerSubscription.getUserAccount().getAcctId(),
                        customerSubscription.getId(), customerSubscription.getSubscriptionType()));
            });
        }
        mapOldValue(analyticalCalculationArchiveList);
    }

    private void mapOldValue(List<AnalyticalCalculationArchive> analyticalCalculationArchiveList) {

        AtomicReference<AnalyticalCalculation> analyticalCalculation1 = new AtomicReference<>(new AnalyticalCalculation());
        for (AnalyticalCalculationArchive analyticalCalculationArchive : analyticalCalculationArchiveList) {
            AnalyticalCalculation analyticalCalculation =
                    analyticalCalculationService.findByAccountIdAndSubscriptionIdAndAnalysis(analyticalCalculationArchive.getAccountId(),
                            analyticalCalculationArchive.getSubscriptionId(), analyticalCalculationArchive.getAnalysis());
            if (analyticalCalculation == null) {
                analyticalCalculation1.set(AnalyticalCalculation.builder()
                        .accountId(analyticalCalculationArchive.getAccountId())
                        .subscriptionId(analyticalCalculationArchive.getSubscriptionId())
                        .analysis(analyticalCalculationArchive.getAnalysis())
                        .currentValue(analyticalCalculationArchive.getCurrentValue())
                        .scope(analyticalCalculationArchive.getScope())
                        .lastUpdatedDatetime(new Date())
                        .build());
                analyticalCalculationService.saveOrUpdate(analyticalCalculation1.get());
            } else {
                Double tempVal = analyticalCalculation.getCurrentValue();
                analyticalCalculationArchive.setOldValue(tempVal);
                System.out.println("Old value: " + analyticalCalculationArchive.getOldValue());
                System.out.println("Current value: " + analyticalCalculationArchive.getCurrentValue());
                analyticalCalculationService.saveOrUpdate(toUpdatedAnalyticalCalculationTemp(analyticalCalculation, analyticalCalculationArchive));
            }
        }
    }

    /**
     * Used in AnalyticalCalculationBatch() to calculate treesPlantingFactorValue
     *
     * @param accountId
     * @param subscriptionId
     * @param subscriptionType
     */
    private AnalyticalCalculationArchive calculateTreesPlantingFactor(Long accountId, Long subscriptionId, String subscriptionType) {
        AnalyticalCalculationArchive analyticalCalculationArchive = new AnalyticalCalculationArchive();
        Double currentValue = billingDetailViewRepository.lifeTimeSum(AppConstants.LIFETIME_PRODUCTION_CODE, accountId, subscriptionId);
        if (currentValue != null) {
            Double TPFValue = currentValue * AppConstants.TPLMULTIPLIER;
            analyticalCalculationArchive = AnalyticalCalculationArchive.builder()
                    .accountId(accountId)
                    .subscriptionId(subscriptionId)
                    .analysis(AppConstants.TPF)
                    .currentValue(TPFValue)
                    .scope(subscriptionType)
                    .lastUpdatedDatetime(new Date())
                    .build();
        }
        return analyticalCalculationArchive;
    }

    /**
     * Used in AnalyticalCalculationBatch() to calculate lifeTimeProductionValue
     *
     * @param accountId
     * @param subscriptionId
     * @param subscriptionType
     */
    private AnalyticalCalculationArchive calculateLifetimeProduction(Long accountId, Long subscriptionId, String subscriptionType) {
        AnalyticalCalculationArchive analyticalCalculationArchive = new AnalyticalCalculationArchive();
        Double currentValue = billingDetailViewRepository.lifeTimeSum(AppConstants.LIFETIME_PRODUCTION_CODE, accountId, subscriptionId);

        if (currentValue != null) {

            analyticalCalculationArchive = AnalyticalCalculationArchive.builder()
                    .accountId(accountId)
                    .subscriptionId(subscriptionId)
                    .analysis(AppConstants.LIFETIME_PRODUCTION_CODE)
                    .currentValue(currentValue)
                    .scope(subscriptionType)
                    .lastUpdatedDatetime(new Date())
                    .build();
        }
        return analyticalCalculationArchive;
    }

    /**
     * Used in AnalyticalCalculationBatch() to calculate lifeTimeSavingsValue
     *
     * @param accountId
     * @param subscriptionId
     * @param subscriptionType
     */
    private AnalyticalCalculationArchive calculateLifetimeSavings(Long accountId, Long subscriptionId, String subscriptionType) {

        Double currentValue = billingDetailViewRepository.lifeTimeSum(AppConstants.LIFETIME_SAVINGS_CODE, accountId, subscriptionId);
        AnalyticalCalculationArchive analyticalCalculationArchive = new AnalyticalCalculationArchive();
        if (currentValue != null) {
            analyticalCalculationArchive = AnalyticalCalculationArchive.builder()
                    .accountId(accountId)
                    .subscriptionId(subscriptionId)
                    .analysis(AppConstants.LIFETIME_SAVINGS_CODE)
                    .currentValue(currentValue)
                    .scope(subscriptionType)
                    .lastUpdatedDatetime(new Date())
                    .build();
        }
        return analyticalCalculationArchive;
    }

    /**
     * @param accountId
     * @param subscriptionId
     * @return
     */
    @Override
    public Map<String, Object> calculateNPV(Long accountId, Long subscriptionId) {

        Map<String, Object> map = new HashMap<>();

        Double netNPV = 0.0;
        List<CustomerSubscription> subscriptionAndMatrix = new ArrayList<>();
        CustomerSubscription subscriptionAndMatrixBySubscription = new CustomerSubscription();

        if (subscriptionId != null) {
            subscriptionAndMatrixBySubscription = subscriptionRepository.findById(subscriptionId).orElse(null);
        } else {
            subscriptionAndMatrix = subscriptionRepository.findCustomerSubscriptionByUserAccount(userRepository.findById(accountId).orElse(null));
        }

        for (int i = 0; subscriptionId == null ? i < subscriptionAndMatrix.size() : i < 1; i++) {
            List<NPVCalculationView> view = npvCalculationViewRepository.getRateCodes(
                    subscriptionId == null ? subscriptionAndMatrix.get(i).getId() : subscriptionId,
                    subscriptionId == null ? subscriptionAndMatrix.get(i).getSubscriptionRateMatrixId() :
                            subscriptionAndMatrixBySubscription.getSubscriptionRateMatrixId());
            SubscriptionRatesDerived rate =
                    ratesDerivedRepository.findByConditionExprAndSubscriptionCodeAndCalcGroup("CCLAS=General Service",
                            "CSGF", "G250");

            if (view.size() == 7) {

                if (view.get(0).getValue() != null) {
                    int yearsLeft = rateFunctions.monthsDifference(view.get(0).getValue());
                    int tenr = Math.toIntExact(Long.parseLong(view.get(5).getValue()) - yearsLeft);
                    Double rateValue = rate.getValue();

                    for (int j = 1; j <= tenr; j++) {
                        //TODO: log rate codes
                        try {
                            Double estimatedSavings = null;
                            if (j == 1) {
                                estimatedProduction =
                                        Double.parseDouble(view.get(2).getValue()) * Double.parseDouble(view.get(6).getValue());
                                estimatedSavings =
                                        rateValue * estimatedProduction * Double.parseDouble(view.get(3).getValue());
                                cumulativeSavings = estimatedSavings;
                                currentCSI = estimatedSavings;
                                System.out.println("Rate: " + rateValue);
                                System.out.println("Estimated Production: " + estimatedProduction);
                                System.out.println("Estimated Savings: " + estimatedSavings);
                                System.out.println("Cumulative Savings: " + cumulativeSavings);
                                System.out.println("Cumulative Savings + I: " + currentCSI);
                            } else {
                                rateValue = rateValue * 1.03;
                                //DEP is 1 - 0.005
                                estimatedProduction =
                                        estimatedProduction * (1 - Double.parseDouble(view.get(1).getValue()));
                                //DSCP is 0.1
                                estimatedSavings =
                                        rateValue * estimatedProduction * Double.parseDouble(view.get(3).getValue());
                                cumulativeSavings = cumulativeSavings + estimatedSavings;
                                currentCSI =
                                        currentCSI * (1 + Double.parseDouble(view.get(4).getValue())) + estimatedSavings;
                                System.out.println("Rate: " + rateValue);
                                System.out.println("Estimated Production: " + estimatedProduction);
                                System.out.println("Estimated Savings: " + estimatedSavings);
                                System.out.println("Cumulative Savings: " + cumulativeSavings);
                                System.out.println("Cumulative Savings + I: " + currentCSI);
                            }
                        } catch (Exception e) {
                            if (view.get(0).getValue() == null) {
                                map.put("CSGDT", view.get(0).getValue());
                            } else if (view.get(1).getValue() == null) {
                                map.put("DEP", view.get(1).getValue());
                            } else if (view.get(2).getValue() == null) {
                                map.put("KWDC", view.get(2).getValue());
                            } else if (view.get(3).getValue() == null) {
                                map.put("NDSC", view.get(3).getValue());
                            } else if (view.get(4).getValue() == null) {
                                map.put("PMESC", view.get(4).getValue());
                            } else if (view.get(5).getValue() == null) {
                                map.put("TENR", view.get(5).getValue());
                            } else if (view.get(6).getValue() == null) {
                                map.put("YLD", view.get(6).getValue());
                            }
                            map.put("MatrixId", subscriptionId == null ? subscriptionAndMatrix.get(i).getSubscriptionRateMatrixId() :
                                    subscriptionAndMatrixBySubscription.getSubscriptionRateMatrixId());
                            map.put("SubscriptionId", subscriptionId);
                            map.put("AccountId", accountId);
                        }
                    }
                }
            } else {
                map.put("subscriptionId", subscriptionId);
                map.put("accountId", accountId);
//                throw new NotFoundException(CustomerSubscription.class, Long.valueOf(subscriptionId));
            }
            netNPV += currentCSI;
        }
        map.put("NPV", utility.round(netNPV, utility.getCompanyPreference().getRounding()));
        return map;
    }
}
