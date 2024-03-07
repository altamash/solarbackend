package com.solar.api.tenant.service;

import com.mchange.util.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRatesDerived;
import com.solar.api.tenant.repository.SubscriptionRatesDerivedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.SubscriptionRateTypeMatrixMapper.toUpdatedSubscriptionRatesDerived;

@Service
public class RateManagementServiceImpl implements RateManagementService {

    @Autowired
    SubscriptionRatesDerivedRepository subscriptionRatesDerivedRepository;
    @Autowired
    SubscriptionService subscriptionService;

    @Override
    public SubscriptionRatesDerived addOrUpdate(SubscriptionRatesDerived subscriptionRatesDerived) throws AlreadyExistsException {
        if (subscriptionRatesDerived.getId() != null) {
            SubscriptionRatesDerived subscriptionRatesDerivedData = findById(subscriptionRatesDerived.getId());
            if (subscriptionRatesDerivedData == null) {
                throw new NotFoundException(SubscriptionRatesDerived.class, subscriptionRatesDerived.getId());
            }
            return subscriptionRatesDerivedRepository.save(toUpdatedSubscriptionRatesDerived(subscriptionRatesDerivedData,
                    subscriptionRatesDerived));
        }
//        List<SubscriptionRateMatrixDetail> subscriptionRateMatrixDetails =
//                subscriptionService.findByDefaultValue("[[" + subscriptionRatesDerived.getCalcGroup() + "]]");
//        if (subscriptionRateMatrixDetails == null) {
            return subscriptionRatesDerivedRepository.save(subscriptionRatesDerived);
//        } else {
//            throw new com.solar.api.exception.AlreadyExistsException(subscriptionRatesDerived.getCalcGroup());
//        }
    }

    @Override
    public SubscriptionRatesDerived findById(Long id) {
        return subscriptionRatesDerivedRepository.findById(id).orElseThrow(() -> new NotFoundException(SubscriptionRatesDerived.class, id));
    }

    @Override
    public List<SubscriptionRatesDerived> findByCalculationGroup(String calculationGroup) {
        return subscriptionRatesDerivedRepository.findByCalcGroup(calculationGroup);
    }

    @Override
    public List<SubscriptionRatesDerived> findAll() {
        return subscriptionRatesDerivedRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        subscriptionRatesDerivedRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        subscriptionRatesDerivedRepository.deleteAll();
    }
}
