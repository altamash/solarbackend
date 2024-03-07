package com.solar.api.tenant.service.controlPanel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.controlPanel.*;
import com.solar.api.tenant.mapper.subscription.customerSubscription.mongo.MongoCustomerSubscriptionDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.mongo.VariantGroupDTO;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.controlPanel.ControlPanelStaticData;
import com.solar.api.tenant.model.controlPanel.ControlPanelTransactionalData;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixDetail;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.repository.ControlPanelStaticDataRepository;
import com.solar.api.tenant.repository.ControlPanelTransactionalDataRepository;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.contract.ContractService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper.toPhysicalLocationDTO;


@Service
public class ControlPanelServiceImpl implements ControlPanelService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    ControlPanelStaticDataRepository staticDataRepository;
    @Autowired
    ControlPanelTransactionalDataRepository transactionalDataRepository;
    @Autowired
    private ContractService contractService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    PortalAttributeOverrideService portalAttributeOverrideService;

    @Autowired
    SubscriptionService subscriptionService;

    @Override
    public ControlPanelStaticData addOrUpdateStaticData(ControlPanelStaticData controlPanelStaticData) {
        if (controlPanelStaticData.getId() != null) {
            ControlPanelStaticData controlPanelStaticDataData = findStaticById(controlPanelStaticData.getId());
            if (controlPanelStaticDataData == null) {
                throw new NotFoundException(ControlPanelStaticData.class, controlPanelStaticData.getId());
            }
            controlPanelStaticDataData = ControlPanelMapper.toUpdatedControlPanelStaticData(controlPanelStaticDataData,
                    controlPanelStaticData);
            return staticDataRepository.save(controlPanelStaticDataData);
        }
        return staticDataRepository.save(controlPanelStaticData);
    }

    /**
     * Static
     */
    @Override
    public ControlPanelStaticData findStaticById(Long id) {
        return staticDataRepository.findById(id).orElseThrow(() -> new NotFoundException(ControlPanelStaticData.class, id));
    }

    @Override
    public ControlPanelStaticData findStaticByVariantId(String variantId) {
        return staticDataRepository.findControlPanelStaticDataByVariantId(variantId);
    }

    @Override
    public List<ControlPanelStaticData> getAllStatic() {
        return staticDataRepository.findAll();
    }

    /**
     * Transactional
     */
    @Override
    public ControlPanelTransactionalData addOrUpdateTransactionalData(ControlPanelTransactionalData controlPanelTransactionalData) {
        if (controlPanelTransactionalData.getId() != null) {
            ControlPanelTransactionalData ControlPanelTransactionalDataData = findTransactionalById(controlPanelTransactionalData.getId());
            if (ControlPanelTransactionalDataData == null) {
                throw new NotFoundException(ControlPanelStaticData.class, controlPanelTransactionalData.getId());
            }
            ControlPanelTransactionalDataData = ControlPanelMapper.toUpdatedControlPanelTransactionalData(ControlPanelTransactionalDataData,
                    controlPanelTransactionalData);
            return transactionalDataRepository.save(ControlPanelTransactionalDataData);
        }
        return transactionalDataRepository.save(controlPanelTransactionalData);
    }

    @Override
    public ControlPanelTransactionalData findTransactionalById(Long id) {
        return transactionalDataRepository.findById(id).orElseThrow(() -> new NotFoundException(ControlPanelTransactionalData.class, id));
    }

    @Override
    public List<ControlPanelTransactionalData> getAllTransactional() {
        return transactionalDataRepository.findAll();
    }

    @Override
    public ControlPanelTransactionalData findTransactionalByVariantId(Long variantId) {
        return transactionalDataRepository.findControlPanelTransactionalDataByVariantId(variantId);
    }

    @Override
    public CPRestructureDTO getRestructureData() {
        List<SubscriptionRateMatrixHead> subscriptionRateMatrixHeads = subscriptionService.findAllSubscriptionRateMatrixHeads();
        List<SolarGarden> solarGardenListARR = new ArrayList<>();
        List<SolarGarden> solarGardenListVOS = new ArrayList<>();
        List<ProductType> productTypes = new ArrayList<>();

        subscriptionRateMatrixHeads.forEach(sh -> {

            List<SubscriptionRateMatrixDetail> subscriptionRateMatrixDetails = subscriptionService.findBySubscriptionRateMatrixId(sh.getId());
            SubscriptionRateMatrixDetail GNSIZE = subscriptionRateMatrixDetails.stream()
                    .filter(matrix -> "GNSIZE".equals(matrix.getRateCode())).findFirst().orElse(null);
            String activeSubscriptions = subscriptionService.findCumulativeKWDCofActiveSubs(sh.getId());
            Double active = activeSubscriptions == null ? null : Double.parseDouble(activeSubscriptions);
            String inactiveSubscriptions = subscriptionService.findCumulativeKWDCofInactiveSubs(sh.getId());
            Double inactive = inactiveSubscriptions == null ? null : Double.parseDouble(inactiveSubscriptions);
            String invalidSubscriptions = subscriptionService.findCumulativeKWDCofInvalidSubs(sh.getId());
            Double invalid = invalidSubscriptions == null ? null : Double.parseDouble(invalidSubscriptions);
            double reserved = 0.0;

            if (inactiveSubscriptions != null && invalidSubscriptions != null) {
                reserved = Double.parseDouble(inactiveSubscriptions) +
                        Double.parseDouble(invalidSubscriptions);
            }

            if (sh.getSubscriptionCode().equals("CSGF")) {
                solarGardenListARR.add(SolarGarden.builder()
                        .name(sh.getSubscriptionTemplate())
                        .active(active)
                        .inactive(inactive)
                        .unallocated(invalid)
                        .reserved(reserved)
                        .size(GNSIZE == null ? null : Double.parseDouble(GNSIZE.getDefaultValue()))
                        .build());
            }

            if (sh.getSubscriptionCode().equals("CSGR")) {
                solarGardenListVOS.add(SolarGarden.builder()
                        .name(sh.getSubscriptionTemplate())
                        .active(active)
                        .inactive(inactive)
                        .unallocated(invalid)
                        .reserved(reserved)
                        .size(GNSIZE == null ? null : Double.parseDouble(GNSIZE.getDefaultValue()))
                        .build());
            }
        });
        productTypes.add(ProductType.builder()
                .name("ARR")
                .solarGardens(solarGardenListARR)
                .count((long) solarGardenListARR.size())
                .build());
        productTypes.add(ProductType.builder()
                .name("VOS")
                .solarGardens(solarGardenListVOS)
                .count((long) solarGardenListVOS.size())
                .build());
        return CPRestructureDTO.builder()
                .count((long) productTypes.size())
                .productTypes(productTypes)
                .build();
    }

    @Override
    public List<CPRestructureDTO> getRestructureDataCategories() {
        /*List<PortalAttributeValueTenantDTO> portalAttributeValueTenantDTOS1 = portalAttributeOverrideService.findByPortalAttributeId(5L);
        List<PortalAttributeValueTenantDTO> portalAttributeValueTenantDTOS2 = portalAttributeOverrideService.findByPortalAttributeId(35L);
        List<SubscriptionRateMatrixHead> subscriptionRateMatrixHeads = subscriptionService.findAllSubscriptionRateMatrixHeads();
        List<ProductType> productTypes = new ArrayList<>();
        portalAttributeValueTenantDTOS2.forEach(pav -> {
            productTypes.add(ProductType.builder()
                    .name(pav.getAttributeName())
                    .count(subscriptionRateMatrixHeads.stream().filter(c -> c.getSubscriptionCode().equals(pav.getAttributeValue())).count())
                    .build());
        });

//        long listArr = subscriptionRateMatrixHeads
//                .stream()
//                .filter(c -> c.getSubscriptionCode().equals(portalAttributeValueTenantDTOS2.stream()
//                        .filter(pav1 -> "CSGF".equals(pav1.getAttributeValue())).findFirst().get().getAttributeValue()))
//                .count();
//        long listVos = subscriptionRateMatrixHeads
//                .stream()
//                .filter(c -> c.getSubscriptionCode().equals(portalAttributeValueTenantDTOS2.stream()
//                        .filter(pav2 -> "CSGR".equals(pav2.getAttributeValue())).findFirst().get().getAttributeValue()))
//                .count();
//        productTypes.add(ProductType.builder()
//                .name("ARR")
//                .count(listArr)
//                .build());
//        productTypes.add(ProductType.builder()
//                .name("VOS")
//                .count(listVos)
//                .build());

        List<CPRestructureDTO> cpRestructureDTOS = new ArrayList<>();
        portalAttributeValueTenantDTOS1.forEach(pav -> {
            if (pav.getAttributeValue().equals("CSG")) {
                cpRestructureDTOS.add(CPRestructureDTO.builder()
                        .name(pav.getDescription())
                        .count((long) productTypes.size())
                        .productTypes(productTypes)
                        .build());
            } else {
                cpRestructureDTOS.add(CPRestructureDTO.builder()
                        .name(pav.getDescription())
                        .build());
            }
        });

        return cpRestructureDTOS;*/
        return Collections.EMPTY_LIST;
    }

    @Override
    public ProductType getProduct(String product) {
        List<SubscriptionRateMatrixHead> subscriptionRateMatrixHeads = subscriptionService.findAllSubscriptionRateMatrixHeads();
        List<SolarGarden> solarGardenList = new ArrayList<>();
        List<ProductType> productTypes = new ArrayList<>();

        subscriptionRateMatrixHeads.forEach(sh -> {

            List<SubscriptionRateMatrixDetail> subscriptionRateMatrixDetails = subscriptionService.findBySubscriptionRateMatrixId(sh.getId());
            SubscriptionRateMatrixDetail GNSIZE = subscriptionRateMatrixDetails.stream()
                    .filter(matrix -> "GNSIZE".equals(matrix.getRateCode())).findFirst().orElse(null);
            String activeSubscriptions = subscriptionService.findCumulativeKWDCofActiveSubs(sh.getId());
            Double active = activeSubscriptions == null ? null : Double.parseDouble(activeSubscriptions);
            String inactiveSubscriptions = subscriptionService.findCumulativeKWDCofInactiveSubs(sh.getId());
            Double inactive = inactiveSubscriptions == null ? null : Double.parseDouble(inactiveSubscriptions);
            String invalidSubscriptions = subscriptionService.findCumulativeKWDCofInvalidSubs(sh.getId());
            Double invalid = invalidSubscriptions == null ? null : Double.parseDouble(invalidSubscriptions);
            double reserved = 0.0;

            if (inactiveSubscriptions != null && invalidSubscriptions != null) {
                reserved = Double.parseDouble(inactiveSubscriptions) +
                        Double.parseDouble(invalidSubscriptions);
            }

            if (sh.getSubscriptionCode().equals(product)) {
                solarGardenList.add(SolarGarden.builder()
                        .id(sh.getId())
                        .name(sh.getSubscriptionTemplate())
                        .active(active)
                        .inactive(inactive)
                        .unallocated(invalid)
                        .reserved(reserved)
                        .size(GNSIZE == null ? null : Double.parseDouble(GNSIZE.getDefaultValue()))
                        .build());
            }
        });
        return ProductType.builder()
                .name(product)
                .solarGardens(solarGardenList)
                .count((long) solarGardenList.size())
                .build();
    }

    @Override
    public SolarGarden getGarden(Long gardenId) {
        SubscriptionRateMatrixHead subscriptionRateMatrixHead = subscriptionService.findSubscriptionRateMatrixHeadById(gardenId);
        List<SubscriptionRateMatrixDetail> subscriptionRateMatrixDetails = subscriptionService.findBySubscriptionRateMatrixId(subscriptionRateMatrixHead.getId());
        List<CustomerSubscription> customerSubscriptions = subscriptionService.findActiveBySubscriptionRateMatrixId(subscriptionRateMatrixHead.getId());
        SubscriptionRateMatrixDetail GNSIZE = subscriptionRateMatrixDetails.stream()
                .filter(matrix -> "GNSIZE".equals(matrix.getRateCode())).findFirst().orElse(null);
        String activeSubscriptions = subscriptionService.findCumulativeKWDCofActiveSubs(subscriptionRateMatrixHead.getId());
        Double active = activeSubscriptions == null ? null : Double.parseDouble(activeSubscriptions);
        String inactiveSubscriptions = subscriptionService.findCumulativeKWDCofInactiveSubs(subscriptionRateMatrixHead.getId());
        Double inactive = inactiveSubscriptions == null ? null : Double.parseDouble(inactiveSubscriptions);
        String invalidSubscriptions = subscriptionService.findCumulativeKWDCofInvalidSubs(subscriptionRateMatrixHead.getId());
        Double invalid = invalidSubscriptions == null ? null : Double.parseDouble(invalidSubscriptions);
        double reserved = 0.0;

        return SolarGarden.builder()
                .name(subscriptionRateMatrixHead.getSubscriptionTemplate())
                .noOfSubscriptions(customerSubscriptions == null ? null : (long) customerSubscriptions.size())
                .active(active)
                .inactive(inactive)
                .unallocated(invalid)
                .reserved(reserved)
                .size(GNSIZE == null ? null : Double.parseDouble(GNSIZE.getDefaultValue()))
                .build();
    }

    @Override
    public List<ControlPanelStaticDataDTO> getStaticByLocAndVariantSize() {
        return staticDataRepository.findAllControlPanelStaticDataByVariantSize();
    }

    @Override
    public ControlPanelStaticDataDTO findControlPanelStaticDataByVariantIdIn(String variantId) {
        return staticDataRepository.findControlPanelStaticDataByVariantIdIn(variantId);
    }


    @Override
    public List<MongoCustomerSubscriptionDTO> formatSubscriptions(List<MongoCustomerSubscriptionDTO> subscriptions) {
        subscriptions.stream().forEach(sub->{
            Entity entity = null;
            Contract contract = null;
            if(sub.getContractId()!= null) {
            }
        });
        return subscriptions;
    }

    @Override
    public List<ControlPanelStaticData> getAllStaticDataByIds(List<String> variantIds) {
        return staticDataRepository.findControlPanelStaticDataByVariantIdIn(variantIds);
    }

    @Override
    public List<String> formatSubscriptionMappings(String subscription) throws JsonProcessingException {
        List<String> subscriptions = mapper.readValue(subscription, List.class);
        List<String> temp = subscriptions;
        subscriptions.stream().forEach(subMapping -> {
            JSONObject jsonObject = new JSONObject(subMapping);
            String variantGroup = jsonObject.get("variant_group").toString();
            try {
                VariantGroupDTO variantGroupDTO = mapper.readValue(variantGroup, VariantGroupDTO.class);
                ControlPanelStaticDataDTO controlPanelStaticData = findControlPanelStaticDataByVariantIdIn(variantGroupDTO.get$id());
                if (controlPanelStaticData != null) {
                    jsonObject.put("variantSize", controlPanelStaticData.getVariantSize());
                    jsonObject.put("availableCapacity", controlPanelStaticData.getAvailableCapacity());
                    jsonObject.put("variantName", controlPanelStaticData.getVariantName());
                    jsonObject.put("physicalLocationDTO", toPhysicalLocationDTO(controlPanelStaticData.getPhysicalLocation()).toString());
                    temp.set(temp.indexOf(subMapping), jsonObject.toString());
                }
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        return subscriptions;
    }

}
