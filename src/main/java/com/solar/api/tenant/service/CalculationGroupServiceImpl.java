package com.solar.api.tenant.service;

import com.mchange.util.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.CalculationGroup;
import com.solar.api.tenant.repository.CalculationGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.CalculationGroupMapper.toUpdatedCalculationGroup;

@Service
public class CalculationGroupServiceImpl implements CalculationGroupService {

    @Autowired
    CalculationGroupRepository calculationGroupRepository;

    @Override
    public CalculationGroup addOrUpdate(CalculationGroup calculationGroup) throws AlreadyExistsException {
        if (calculationGroup.getId() != null) {
            CalculationGroup calculationGroupData = findById(calculationGroup.getId());
            if (calculationGroupData == null) {
                throw new NotFoundException(CalculationGroup.class, calculationGroup.getId());
            }
            return calculationGroupRepository.save(toUpdatedCalculationGroup(calculationGroupData,
                    calculationGroup));
        }
//        List<SubscriptionRateMatrixDetail> subscriptionRateMatrixDetails =
//                subscriptionService.findByDefaultValue("[[" + subscriptionRatesDerived.getCalcGroup() + "]]");
//        if (subscriptionRateMatrixDetails == null) {
        return calculationGroupRepository.save(calculationGroup);
//        } else {
//            throw new com.solar.api.exception.AlreadyExistsException(subscriptionRatesDerived.getCalcGroup());
//        }
    }

    @Override
    public CalculationGroup findById(Long id) {
        return calculationGroupRepository.findById(id).orElseThrow(() -> new NotFoundException(CalculationGroup.class, id));
    }

    @Override
    public List<CalculationGroup> findAll() {
        return calculationGroupRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        calculationGroupRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        calculationGroupRepository.deleteAll();
    }
}
