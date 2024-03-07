package com.solar.api.tenant.service.controlPanel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.tenant.mapper.controlPanel.CPRestructureDTO;
import com.solar.api.tenant.mapper.controlPanel.ControlPanelStaticDataDTO;
import com.solar.api.tenant.mapper.controlPanel.ProductType;
import com.solar.api.tenant.mapper.controlPanel.SolarGarden;
import com.solar.api.tenant.mapper.subscription.customerSubscription.mongo.MongoCustomerSubscriptionDTO;
import com.solar.api.tenant.model.controlPanel.ControlPanelStaticData;
import com.solar.api.tenant.model.controlPanel.ControlPanelTransactionalData;

import java.util.List;

public interface ControlPanelService {

    ControlPanelStaticData addOrUpdateStaticData(ControlPanelStaticData controlPanelStaticData);
    ControlPanelStaticData findStaticById(Long id);

    List<ControlPanelStaticData> getAllStatic();

    ControlPanelTransactionalData addOrUpdateTransactionalData(ControlPanelTransactionalData controlPanelTransactionalData);

    ControlPanelStaticData findStaticByVariantId(String variantId);

    ControlPanelTransactionalData findTransactionalById(Long id);

    List<ControlPanelTransactionalData> getAllTransactional();

    ControlPanelTransactionalData findTransactionalByVariantId(Long variantId);

    CPRestructureDTO getRestructureData();

    List<CPRestructureDTO>  getRestructureDataCategories();

    ProductType getProduct(String product);

    SolarGarden getGarden(Long gardenId);

    List<ControlPanelStaticDataDTO>  getStaticByLocAndVariantSize();

    ControlPanelStaticDataDTO findControlPanelStaticDataByVariantIdIn(String variantId);

    List<MongoCustomerSubscriptionDTO> formatSubscriptions(List<MongoCustomerSubscriptionDTO> subscriptions);

    List<ControlPanelStaticData> getAllStaticDataByIds(List<String> variantIds);

    List<String> formatSubscriptionMappings(String subscriptions) throws JsonProcessingException;



    }
