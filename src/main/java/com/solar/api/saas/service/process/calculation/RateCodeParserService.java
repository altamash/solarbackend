package com.solar.api.saas.service.process.calculation;

import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.repository.CustomerSubscriptionMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RateCodeParserService extends RateCodeParser {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private CustomerSubscriptionMappingRepository customerSubscriptionMappingRepository;

    public Map<String, Object> generateHashMap(CustomerSubscription subscription, Long subscriptionMatrixHeadId,
                                               Long jobId) {
        Map<String, Object> valuesHashMap = new HashMap<>();
        List<CustomerSubscriptionMapping> staticMappings =
                customerSubscriptionMappingRepository.getMappingsWithStaticValues(subscription, subscriptionMatrixHeadId);
        List<CustomerSubscriptionMapping> calculationMappings =
                customerSubscriptionMappingRepository.getMappingsForCalculationOrderedBySequence(subscription, subscriptionMatrixHeadId);
        LOGGER.info("\tStarted storing values in HashMap");
        LOGGER.info("\t\tStatic values:");
        staticMappings.stream()
                .forEach(mapping -> {
                    valuesHashMap.put(mapping.getRateCode(), mapping.getValue());
                    LOGGER.info("\t\t\t" + mapping.getRateCode() + " = " + mapping.getValue());
                });
        // Add parsed dynamic values in map
        parseAndUpdateValues(calculationMappings, valuesHashMap, subscription.getId(), jobId);
        LOGGER.info("\tCompleted  storing values in HashMap");
        return valuesHashMap;
    }
}
