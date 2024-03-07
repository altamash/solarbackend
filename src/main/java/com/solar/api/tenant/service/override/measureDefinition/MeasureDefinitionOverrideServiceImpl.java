package com.solar.api.tenant.service.override.measureDefinition;

import com.solar.api.saas.model.extended.MeasureDefinitionSAAS;
import com.solar.api.saas.service.extended.measureDefinition.MeasureDefinitionSAASService;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantMapper;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTemplate;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTemplateDTO;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTenant;
import com.solar.api.tenant.repository.MeasureDefinitionTenantRepository;
import com.solar.api.tenant.repository.RegisterDetailRepository;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
//@Transactional("tenantTransactionManager")
public class MeasureDefinitionOverrideServiceImpl implements MeasureDefinitionOverrideService {

    @Autowired
    private MeasureDefinitionTenantRepository repository;
    @Autowired
    private RegisterDetailRepository registerDetailRepository;
    @Autowired
    private MeasureDefinitionTenantGetterService measureDefinitionTenantGetterService;
    @Autowired
    private MeasureDefinitionSAASService measureDefinitionSAASService;
    @Autowired
    private PortalAttributeOverrideService portalAttributeOverrideService;

    @Override
    public MeasureDefinitionTenantDTO findById(Long id) {
        MeasureDefinitionTenant measureDefinitionTenant = measureDefinitionTenantGetterService.findById(id);
        if (measureDefinitionTenant != null) {
            return MeasureDefinitionTenantMapper.toMeasureDefinitionDTO(measureDefinitionTenant);
        }
        MeasureDefinitionSAAS measureDefinitionSAAS = measureDefinitionSAASService.findById(id);
        MeasureDefinitionTenantDTO measureDefinitionTenantDTO = MeasureDefinitionTenantMapper.toMeasureDefinitionDTO(measureDefinitionSAAS);
        return setPortalAttributeValue(measureDefinitionTenantDTO);
    }

    @Override
    public MeasureDefinitionTenantDTO findByIdOrderByIdAsc(Long id) {
        MeasureDefinitionTenant measureDefinitionTenant = measureDefinitionTenantGetterService.findByIdOrderByIdAsc(id);
        if (measureDefinitionTenant != null) {
            return MeasureDefinitionTenantMapper.toMeasureDefinitionDTO(measureDefinitionTenant);
        }
        MeasureDefinitionSAAS measureDefinitionSAAS = measureDefinitionSAASService.findByIdOrderByIdAsc(id);
        MeasureDefinitionTenantDTO measureDefinitionTenantDTO = MeasureDefinitionTenantMapper.toMeasureDefinitionDTO(measureDefinitionSAAS);
        return setPortalAttributeValue(measureDefinitionTenantDTO);
    }

    /**
     * Combination of saas and tenant schema measure definitions
     * @return measureDefinitionSAASDTOs
     */
    @Override
    public List<MeasureDefinitionTenantDTO> findAll() {
        List<MeasureDefinitionTenant> measureDefinitionsTenant = measureDefinitionTenantGetterService.findAll();
        List<MeasureDefinitionSAAS> measureDefinitionsSAASAAS;
        if (measureDefinitionsTenant.isEmpty()) {
            measureDefinitionsSAASAAS = measureDefinitionSAASService.findAll();
        } else {
            measureDefinitionsSAASAAS = measureDefinitionSAASService
                    .findAllIdsNotIn(measureDefinitionsTenant.stream().map(md -> md.getId()).collect(Collectors.toList()));
        }
        List<MeasureDefinitionTenantDTO> measureDefinitionTenantDTOs = MeasureDefinitionTenantMapper.toMeasureDefinitionDTOs(measureDefinitionsTenant);
        List<MeasureDefinitionTenantDTO> measureDefinitionSAASDTOs = MeasureDefinitionTenantMapper.toMeasureDefinitionDTOsFromSAAS(measureDefinitionsSAASAAS);
        measureDefinitionSAASDTOs.addAll(measureDefinitionTenantDTOs);
        measureDefinitionSAASDTOs.sort(Comparator.comparing(MeasureDefinitionTenantDTO::getId));
        return setPortalAttributeValues(measureDefinitionSAASDTOs);
    }

    @Override
    public MeasureDefinitionTenantDTO findMeasureDefinitionByCode(String code) {
        MeasureDefinitionTenant measureDefinitionTenant = measureDefinitionTenantGetterService.findMeasureDefinitionByCode(code);
        if (measureDefinitionTenant != null) {
            return MeasureDefinitionTenantMapper.toMeasureDefinitionDTO(measureDefinitionTenant);
        }
        MeasureDefinitionSAAS measureDefinitionSAAS = measureDefinitionSAASService.findMeasureDefinitionByCode(code);
        MeasureDefinitionTenantDTO measureDefinitionTenantDTO = MeasureDefinitionTenantMapper.toMeasureDefinitionDTO(measureDefinitionSAAS);
        return setPortalAttributeValue(measureDefinitionTenantDTO);
    }

    @Override
    public List<MeasureDefinitionTenantDTO> findByCodes(Set<String> codes) {
        List<MeasureDefinitionTenant> measureDefinitionsTenant = measureDefinitionTenantGetterService.findByCodes(codes);
        List<String> tenantMeasureCodes = measureDefinitionsTenant.stream().map(m -> m.getCode()).collect(Collectors.toList());
        codes.removeIf(c -> tenantMeasureCodes.contains(c));
        List<MeasureDefinitionSAAS> measureDefinitionsSAAS = measureDefinitionSAASService.findByCodes(codes);
        List<MeasureDefinitionTenantDTO> measureDefinitionTenantDTOs = MeasureDefinitionTenantMapper.toMeasureDefinitionDTOs(measureDefinitionsTenant);
        List<MeasureDefinitionTenantDTO> measureDefinitionSAASDTOs = MeasureDefinitionTenantMapper.toMeasureDefinitionDTOsFromSAAS(measureDefinitionsSAAS);
        measureDefinitionSAASDTOs.addAll(measureDefinitionTenantDTOs);
        measureDefinitionSAASDTOs.sort(Comparator.comparing(MeasureDefinitionTenantDTO::getId));
        return setPortalAttributeValues(measureDefinitionSAASDTOs);
    }

    @Override
    public List<MeasureDefinitionTenantDTO> findByIds(List<Long> ids) {
        List<MeasureDefinitionTenant> measureDefinitionsTenant = measureDefinitionTenantGetterService.findByIds(ids);
        List<Long> tenantMeasureIds = measureDefinitionsTenant.stream().map(m -> m.getId()).collect(Collectors.toList());
        ids.removeIf(i -> tenantMeasureIds.contains(i));
        List<MeasureDefinitionSAAS> measureDefinitionsSAAS = measureDefinitionSAASService.findByIds(ids);
        List<MeasureDefinitionTenantDTO> measureDefinitionTenantDTOs = MeasureDefinitionTenantMapper.toMeasureDefinitionDTOs(measureDefinitionsTenant);
        List<MeasureDefinitionTenantDTO> measureDefinitionSAASDTOs = MeasureDefinitionTenantMapper.toMeasureDefinitionDTOsFromSAAS(measureDefinitionsSAAS);
        measureDefinitionSAASDTOs.addAll(measureDefinitionTenantDTOs);
        measureDefinitionSAASDTOs.sort(Comparator.comparing(MeasureDefinitionTenantDTO::getId));
        return setPortalAttributeValues(measureDefinitionSAASDTOs);
    }

    @Override
    public List<MeasureDefinitionTenantDTO> findByRegModuleId(Long regModuleId) {
        List<MeasureDefinitionTenant> measureDefinitionsTenant = measureDefinitionTenantGetterService.findByRegModuleId(regModuleId);
        List<String> measures = measureDefinitionsTenant.stream().map(m -> m.getMeasure()).collect(Collectors.toList());
        List<MeasureDefinitionSAAS> measureDefinitionsSAASAAS = null;
        if (measures.size()!=0) {
            measureDefinitionsSAASAAS = measureDefinitionSAASService.findByRegModuleIdMeasuresNotIn(regModuleId, measures);
        }else{
            measureDefinitionsSAASAAS = measureDefinitionSAASService.findByRegModuleId(regModuleId);
        }
        List<MeasureDefinitionTenantDTO> measureDefinitionDTOs = MeasureDefinitionTenantMapper.toMeasureDefinitionDTOsFromSAAS(measureDefinitionsSAASAAS);
        List<MeasureDefinitionTenantDTO> measureDefinitionTenantDTOs = MeasureDefinitionTenantMapper.toMeasureDefinitionDTOs(measureDefinitionsTenant);
        measureDefinitionDTOs.addAll(measureDefinitionTenantDTOs);
        measureDefinitionDTOs.sort(Comparator.comparing(MeasureDefinitionTenantDTO::getId));
        return setPortalAttributeValues(measureDefinitionDTOs);
    }

    private MeasureDefinitionTenantDTO setPortalAttributeValue(MeasureDefinitionTenantDTO measureDefinitionTenantDTO) {
        if (measureDefinitionTenantDTO.getAttributeIdRefId() != null) {
            List<PortalAttributeValueTenantDTO> portalAttributeValueDTOs = portalAttributeOverrideService
                    .findByPortalAttributeId(measureDefinitionTenantDTO.getAttributeIdRefId());
            measureDefinitionTenantDTO.setPortalAttributeValueDTOs(portalAttributeValueDTOs);
            measureDefinitionTenantDTO.setPortalAttributeValues(portalAttributeValueDTOs.stream().map(pa -> pa.getAttributeValue()).collect(Collectors.toList()));
        } else {
            measureDefinitionTenantDTO.setPortalAttributeValueDTOs(Collections.emptyList());
            measureDefinitionTenantDTO.setPortalAttributeValues(Collections.emptyList());
        }
        return measureDefinitionTenantDTO;
    }

    private List<MeasureDefinitionTenantDTO> setPortalAttributeValues(List<MeasureDefinitionTenantDTO> measureDefinitionTenantDTOs) {
        measureDefinitionTenantDTOs.stream().collect(Collectors.toList()).forEach(measure -> {
            if (measure.getAttributeIdRefId() != null) {
                List<PortalAttributeValueTenantDTO> portalAttributeValueDTOs = portalAttributeOverrideService
                        .findByPortalAttributeId(measure.getAttributeIdRefId());
                measure.setPortalAttributeValueDTOs(portalAttributeValueDTOs);
                measure.setPortalAttributeValues(portalAttributeValueDTOs.stream().map(pa -> pa.getAttributeValue()).collect(Collectors.toList()));
            } else {
                measure.setPortalAttributeValueDTOs(Collections.emptyList());
                measure.setPortalAttributeValues(Collections.emptyList());
            }
        });
        return measureDefinitionTenantDTOs;
    }

    @Override
    public String checkMeasureLinkWithRegister(Long measureCodeId) {
        Long count = registerDetailRepository.getCountByMeasureCodeId(measureCodeId);
        String response = "This measure has been marked for deletion.";

        if (count!=0) {
            return response = "This measure is associated with Register.";
        }

        if (measureCodeId != null) {
            MeasureDefinitionTenant measureDefinitionTenant = measureDefinitionTenantGetterService.findById(measureCodeId);
            if (measureDefinitionTenant != null) {
                measureDefinitionTenantGetterService.deleteById(measureCodeId);
            }else{
                //MeasureDefinitionSAAS measureDefinitionSAAS = measureDefinitionSAASService.findById(measureCodeId);
                measureDefinitionSAASService.delete(measureCodeId);
            }
        }
        return response;
    }

    @Override
    public MeasureDefinitionTemplateDTO getAllHeaderAndFormat(List<Long> measureIds) {
        MeasureDefinitionTemplate measureDefinitionsTenant = measureDefinitionTenantGetterService.getAllHeaderAndFormat(measureIds);
        if(measureDefinitionsTenant.getMeasureIds()!=null){
            List<Long> tenantMeasureIds = Arrays.stream(measureDefinitionsTenant.getMeasureIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
            measureIds.removeIf(i -> tenantMeasureIds.contains(i));
        }
        MeasureDefinitionTemplate measureDefinitionsSAAS = measureDefinitionSAASService.getAllHeaderAndFormat(measureIds);
        StringJoiner measureIdsJoiner = new StringJoiner(",");
        StringJoiner measureNamesJoiner = new StringJoiner(",");
        StringJoiner formatsJoiner = new StringJoiner(",");
        if(measureDefinitionsTenant.getMeasureIds()!=null){
            measureNamesJoiner.add(measureDefinitionsTenant.getMeasureNames());
            measureIdsJoiner.add(measureDefinitionsTenant.getMeasureIds());
            formatsJoiner.add(measureDefinitionsTenant.getFormats());
        }
        if(measureDefinitionsSAAS.getMeasureIds()!=null){
            measureNamesJoiner.add(measureDefinitionsSAAS.getMeasureNames());
            measureIdsJoiner.add(measureDefinitionsSAAS.getMeasureIds());
            formatsJoiner.add(measureDefinitionsSAAS.getFormats());
        }
        return MeasureDefinitionTemplateDTO.builder()
                .measureIds(measureIdsJoiner.toString())
                .measureNames(measureNamesJoiner.toString())
                .formats(formatsJoiner.toString()).build();
    }

//    @Override
//    public void delete(Long id) {
//        repository.deleteById(id);
//    }

//    @Override
//    public void deleteAll() {
//        repository.deleteAll();
//    }
}
