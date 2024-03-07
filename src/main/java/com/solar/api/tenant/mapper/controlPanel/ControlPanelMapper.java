package com.solar.api.tenant.mapper.controlPanel;

import com.solar.api.tenant.model.controlPanel.ControlPanelStaticData;
import com.solar.api.tenant.model.controlPanel.ControlPanelTransactionalData;

import java.util.List;
import java.util.stream.Collectors;

public class ControlPanelMapper {

    public static ControlPanelStaticData toControlPanelStaticData(ControlPanelStaticDataDTO controlPanelStaticDataDTO) {
        if (controlPanelStaticDataDTO == null) {
            return null;
        }
        return ControlPanelStaticData.builder()
                .id(controlPanelStaticDataDTO.getId())
                .variantName(controlPanelStaticDataDTO.getVariantName())
                .variantSize(controlPanelStaticDataDTO.getVariantSize())
                .noOfSites(controlPanelStaticDataDTO.getNoOfSites())
                .variantOccupancy(controlPanelStaticDataDTO.getVariantOccupancy())
                .NoOfInverters(controlPanelStaticDataDTO.getNoOfInverters())
                .srcNo(controlPanelStaticDataDTO.getSrcNo())
                .variantId(controlPanelStaticDataDTO.getVariantId())
                .variantType(controlPanelStaticDataDTO.getVariantType())
                .orgId(controlPanelStaticDataDTO.getOrgId())
                .productCategory(controlPanelStaticDataDTO.getProductCategory())
                .environmentalContribution(controlPanelStaticDataDTO.getEnvironmentalContribution())
                .premiseNumber(controlPanelStaticDataDTO.getPremiseNumber())
                .premiseAllocation(controlPanelStaticDataDTO.getPremiseAllocation())
                .premiseEntityId(controlPanelStaticDataDTO.getPremiseEntityId())
                .LocId(controlPanelStaticDataDTO.getLocId())
                .build();
    }

    public static ControlPanelStaticDataDTO toControlPanelStaticDataDTO(ControlPanelStaticData controlPanelStaticData) {
        if (controlPanelStaticData == null) {
            return null;
        }
        return ControlPanelStaticDataDTO.builder()
                .id(controlPanelStaticData.getId())
                .variantSize(controlPanelStaticData.getVariantSize())
                .variantName(controlPanelStaticData.getVariantName())
                .noOfSites(controlPanelStaticData.getNoOfSites())
                .variantOccupancy(controlPanelStaticData.getVariantOccupancy())
                .NoOfInverters(controlPanelStaticData.getNoOfInverters())
                .srcNo(controlPanelStaticData.getSrcNo())
                .variantId(controlPanelStaticData.getVariantId())
                .variantType(controlPanelStaticData.getVariantType())
                .orgId(controlPanelStaticData.getOrgId())
                .productCategory(controlPanelStaticData.getProductCategory())
                .environmentalContribution(controlPanelStaticData.getEnvironmentalContribution())
                .premiseNumber(controlPanelStaticData.getPremiseNumber())
                .premiseAllocation(controlPanelStaticData.getPremiseAllocation())
                .premiseEntityId(controlPanelStaticData.getPremiseEntityId())
                .LocId(controlPanelStaticData.getLocId())
                .build();
    }

    public static ControlPanelStaticData toUpdatedControlPanelStaticData(ControlPanelStaticData controlPanelStaticData, ControlPanelStaticData controlPanelStaticDataUpdate) {
        controlPanelStaticData.setVariantSize(controlPanelStaticDataUpdate.getVariantSize() == null ? controlPanelStaticData.getVariantSize() : controlPanelStaticDataUpdate.getVariantSize());
        controlPanelStaticData.setNoOfSites(controlPanelStaticDataUpdate.getNoOfSites() == null ? controlPanelStaticData.getNoOfSites() : controlPanelStaticDataUpdate.getNoOfSites());
        controlPanelStaticData.setVariantOccupancy(controlPanelStaticDataUpdate.getVariantOccupancy() == null ? controlPanelStaticData.getVariantOccupancy() : controlPanelStaticDataUpdate.getVariantOccupancy());
        controlPanelStaticData.setNoOfInverters(controlPanelStaticDataUpdate.getNoOfInverters() == null ? controlPanelStaticData.getNoOfInverters() : controlPanelStaticDataUpdate.getNoOfInverters());
        controlPanelStaticData.setSrcNo(controlPanelStaticDataUpdate.getSrcNo() == null ? controlPanelStaticData.getSrcNo() : controlPanelStaticDataUpdate.getSrcNo());
        controlPanelStaticData.setVariantId(controlPanelStaticDataUpdate.getVariantId() == null ? controlPanelStaticData.getVariantId() : controlPanelStaticDataUpdate.getVariantId());
        controlPanelStaticData.setVariantType(controlPanelStaticDataUpdate.getVariantType() == null ? controlPanelStaticData.getVariantType() : controlPanelStaticDataUpdate.getVariantType());
        controlPanelStaticData.setOrgId(controlPanelStaticDataUpdate.getOrgId() == null ? controlPanelStaticData.getOrgId() : controlPanelStaticDataUpdate.getOrgId());
        controlPanelStaticData.setProductCategory(controlPanelStaticDataUpdate.getProductCategory() == null ? controlPanelStaticData.getProductCategory() : controlPanelStaticDataUpdate.getProductCategory());
        controlPanelStaticData.setEnvironmentalContribution(controlPanelStaticDataUpdate.getEnvironmentalContribution() == null ?
                controlPanelStaticData.getEnvironmentalContribution() : controlPanelStaticDataUpdate.getEnvironmentalContribution());
        controlPanelStaticData.setPremiseNumber(controlPanelStaticDataUpdate.getPremiseNumber() == null ? controlPanelStaticData.getPremiseNumber() : controlPanelStaticDataUpdate.getPremiseNumber());
        controlPanelStaticData.setPremiseAllocation(controlPanelStaticDataUpdate.getPremiseAllocation() == null ? controlPanelStaticData.getPremiseAllocation() : controlPanelStaticDataUpdate.getPremiseAllocation());
        controlPanelStaticData.setPremiseEntityId(controlPanelStaticDataUpdate.getPremiseEntityId() == null ? controlPanelStaticData.getPremiseEntityId() : controlPanelStaticDataUpdate.getPremiseEntityId());
        controlPanelStaticData.setLocId(controlPanelStaticDataUpdate.getLocId() == null ? controlPanelStaticData.getLocId() : controlPanelStaticDataUpdate.getLocId());
        return controlPanelStaticData;
    }

    public static List<ControlPanelStaticData> toControlPanelStaticDatas(List<ControlPanelStaticDataDTO> controlPanelStaticDataDTOS) {
        return controlPanelStaticDataDTOS.stream().map(c -> toControlPanelStaticData(c)).collect(Collectors.toList());
    }

    public static List<ControlPanelStaticDataDTO> toControlPanelStaticDataDTOs(List<ControlPanelStaticData> controlPanelStaticDataList) {
        return controlPanelStaticDataList.stream().map(c -> toControlPanelStaticDataDTO(c)).collect(Collectors.toList());
    }

    public static ControlPanelTransactionalData toControlPanelTransactionalData(ControlPanelTransactionalDataDTO controlPanelTransactionalDataDTO) {
        if (controlPanelTransactionalDataDTO == null) {
            return null;
        }
        return ControlPanelTransactionalData.builder()
                .id(controlPanelTransactionalDataDTO.getId())
                .inverterStatus(controlPanelTransactionalDataDTO.getInverterStatus())
                .dailyProduction(controlPanelTransactionalDataDTO.getDailyProduction())
                .inverterHealth(controlPanelTransactionalDataDTO.getInverterHealth())
                .variantHealth(controlPanelTransactionalDataDTO.getVariantHealth())
                .errors(controlPanelTransactionalDataDTO.getErrors())
                .faults(controlPanelTransactionalDataDTO.getFaults())
                .alerts(controlPanelTransactionalDataDTO.getAlerts())
                .currentTemp(controlPanelTransactionalDataDTO.getCurrentTemp())
                .humidity(controlPanelTransactionalDataDTO.getHumidity())
                .percipitation(controlPanelTransactionalDataDTO.getPercipitation())
                .inverterId(controlPanelTransactionalDataDTO.getInverterId())
                .variantId(controlPanelTransactionalDataDTO.getVariantId())
                .locId(controlPanelTransactionalDataDTO.getLocId())
                .build();
    }

    public static ControlPanelTransactionalDataDTO toControlPanelTransactionalDataDTO(ControlPanelTransactionalData controlPanelTransactionalData) {
        if (controlPanelTransactionalData == null) {
            return null;
        }
        return ControlPanelTransactionalDataDTO.builder()
                .id(controlPanelTransactionalData.getId())
                .inverterStatus(controlPanelTransactionalData.getInverterStatus())
                .dailyProduction(controlPanelTransactionalData.getDailyProduction())
                .inverterHealth(controlPanelTransactionalData.getInverterHealth())
                .variantHealth(controlPanelTransactionalData.getVariantHealth())
                .errors(controlPanelTransactionalData.getErrors())
                .faults(controlPanelTransactionalData.getFaults())
                .alerts(controlPanelTransactionalData.getAlerts())
                .currentTemp(controlPanelTransactionalData.getCurrentTemp())
                .humidity(controlPanelTransactionalData.getHumidity())
                .percipitation(controlPanelTransactionalData.getPercipitation())
                .inverterId(controlPanelTransactionalData.getInverterId())
                .variantId(controlPanelTransactionalData.getVariantId())
                .locId(controlPanelTransactionalData.getLocId())
                .build();
    }

    public static ControlPanelTransactionalData toUpdatedControlPanelTransactionalData(ControlPanelTransactionalData controlPanelTransactionalData, ControlPanelTransactionalData controlPanelTransactionalDataUpdate) {
        controlPanelTransactionalData.setInverterStatus(controlPanelTransactionalDataUpdate.getInverterStatus() == null ?
                controlPanelTransactionalData.getInverterStatus() : controlPanelTransactionalDataUpdate.getInverterStatus());
        controlPanelTransactionalData.setDailyProduction(controlPanelTransactionalDataUpdate.getDailyProduction() == null ?
                controlPanelTransactionalData.getDailyProduction() : controlPanelTransactionalDataUpdate.getDailyProduction());
        controlPanelTransactionalData.setInverterHealth(controlPanelTransactionalDataUpdate.getInverterHealth() == null ?
                controlPanelTransactionalData.getInverterHealth() : controlPanelTransactionalDataUpdate.getInverterHealth());
        controlPanelTransactionalData.setVariantHealth(controlPanelTransactionalDataUpdate.getVariantHealth() == null ?
                controlPanelTransactionalData.getVariantHealth() : controlPanelTransactionalDataUpdate.getVariantHealth());
        controlPanelTransactionalData.setErrors(controlPanelTransactionalDataUpdate.getErrors() == null ?
                controlPanelTransactionalData.getErrors() : controlPanelTransactionalDataUpdate.getErrors());
        controlPanelTransactionalData.setFaults(controlPanelTransactionalDataUpdate.getFaults() == null ?
                controlPanelTransactionalData.getFaults() : controlPanelTransactionalDataUpdate.getFaults());
        controlPanelTransactionalData.setAlerts(controlPanelTransactionalDataUpdate.getAlerts() == null ?
                controlPanelTransactionalData.getAlerts() : controlPanelTransactionalDataUpdate.getAlerts());
        controlPanelTransactionalData.setCurrentTemp(controlPanelTransactionalDataUpdate.getCurrentTemp() == null ?
                controlPanelTransactionalData.getCurrentTemp() : controlPanelTransactionalDataUpdate.getCurrentTemp());
        controlPanelTransactionalData.setHumidity(controlPanelTransactionalDataUpdate.getHumidity() == null ?
                controlPanelTransactionalData.getHumidity() : controlPanelTransactionalDataUpdate.getHumidity());
        controlPanelTransactionalData.setPercipitation(controlPanelTransactionalDataUpdate.getPercipitation() == null ?
                controlPanelTransactionalData.getPercipitation() : controlPanelTransactionalDataUpdate.getPercipitation());
        controlPanelTransactionalData.setInverterId(controlPanelTransactionalDataUpdate.getInverterId() == null ?
                controlPanelTransactionalData.getInverterId() : controlPanelTransactionalDataUpdate.getInverterId());
        controlPanelTransactionalData.setVariantId(controlPanelTransactionalDataUpdate.getVariantId() == null ?
                controlPanelTransactionalData.getVariantId() : controlPanelTransactionalDataUpdate.getVariantId());
        controlPanelTransactionalData.setLocId(controlPanelTransactionalDataUpdate.getLocId() == null ?
                controlPanelTransactionalData.getLocId() : controlPanelTransactionalDataUpdate.getLocId());
        return controlPanelTransactionalData;
    }

    public static List<ControlPanelTransactionalData> toControlPanelTransactionalDatas(List<ControlPanelTransactionalDataDTO> controlPanelTransactionalDataDTOS) {
        return controlPanelTransactionalDataDTOS.stream().map(c -> toControlPanelTransactionalData(c)).collect(Collectors.toList());
    }

    public static List<ControlPanelTransactionalDataDTO> toControlPanelTransactionalDataDTOs(List<ControlPanelTransactionalData> controlPanelTransactionalDataList) {
        return controlPanelTransactionalDataList.stream().map(c -> toControlPanelTransactionalDataDTO(c)).collect(Collectors.toList());
    }
}
