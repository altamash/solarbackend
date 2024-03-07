package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.util.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.stage.billing.ExtDataStageDefinitionBilling;
import com.solar.api.tenant.repository.ExtDataStageDefinitionBillingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.billing.calculation.ExtDataStageDefinitionBillingMapper.toUpdatedExtDataStageDefinitionBilling;

@Service
public class ExtDataStageDefinitionBillingServiceImpl implements ExtDataStageDefinitionBillingService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${app.mongoBaseUrl}")
    private String MONGO_BASE_URL;

    @Autowired
    private ExtDataStageDefinitionBillingRepository extDataStageDefinitionBillingRepository;

    @Override
    public ExtDataStageDefinitionBilling findBySubsId(String subId) {
        return extDataStageDefinitionBillingRepository.findBySubsId(subId);
    }

    @Override
    public List<ExtDataStageDefinitionBilling> findAllBySubIds(List<String> subIds) {
        return extDataStageDefinitionBillingRepository.findAllBySubsIdIn(subIds);
    }

    @Override
    public List<String> findVariantIdsBySrcNo(List<String> srcNos) {
        List<ExtDataStageDefinitionBilling> billingItems = extDataStageDefinitionBillingRepository.findAll();
        List<String> result = new ArrayList<>();
        billingItems.stream().forEach(billingItem -> {
            try {
                Boolean srcPresent = false;
                Map<String, String> subsMeasures = new ObjectMapper().readValue(billingItem.getBillingJson(), Map.class);
                if (subsMeasures.get("SCSGN") != null) {
                    srcPresent = srcNos.stream().anyMatch(srcNo -> (srcNo.equalsIgnoreCase(subsMeasures.get("SCSGN").toString())));
                    if (srcPresent) {
                        result.add(billingItem.getRefId());
                    }
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }

        });
        return result.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<ExtDataStageDefinitionBilling> findBySubStatus(String status) {
        return extDataStageDefinitionBillingRepository.findSubsByStatus(status);
    }

    @Override
    public Long findLocIdByVariantId(String variantId) {
        return extDataStageDefinitionBillingRepository.findLocIdByVariantId(variantId);
    }

    @Override
    public String findVariantAliasByVariantId(String variantId) {
        return extDataStageDefinitionBillingRepository.findVariantAliasByVariantId(variantId);
    }

    @Override
    public List<ExtDataStageDefinitionBilling> findAllByRefId(String refId) {
        return extDataStageDefinitionBillingRepository.findAllByRefId(refId);
    }

    @Override
    public ExtDataStageDefinitionBilling addOrUpdate(ExtDataStageDefinitionBilling extDataStageDefinitionBilling) throws AlreadyExistsException {
        if (extDataStageDefinitionBilling.getId() != null) {
            Optional<ExtDataStageDefinitionBilling> extDataStageDefinitionBillingData = extDataStageDefinitionBillingRepository.findById(extDataStageDefinitionBilling.getId());
            if (extDataStageDefinitionBillingData == null) {
                throw new NotFoundException(ExtDataStageDefinitionBilling.class, extDataStageDefinitionBilling.getId());
            }
            return extDataStageDefinitionBillingRepository.save(toUpdatedExtDataStageDefinitionBilling(extDataStageDefinitionBillingData.get(),
                    extDataStageDefinitionBilling));
        }
        return extDataStageDefinitionBillingRepository.save(extDataStageDefinitionBilling);
    }
}