package com.solar.api.tenant.service.extended.measure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.configuration.DataSourceBasedMultiTenantConnectionProviderImpl;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.assetHead.AssetHead;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTemplateDTO;
import com.solar.api.tenant.model.extended.register.RegisterDetail;
import com.solar.api.tenant.model.extended.register.RegisterHead;
import com.solar.api.tenant.model.extended.resources.MeasureBlockHead;
import com.solar.api.tenant.repository.AssetHeadRepository;
import com.solar.api.tenant.repository.MeasureBlockHeadRepository;
import com.solar.api.tenant.repository.RegisterDetailRepository;
import com.solar.api.tenant.service.extended.register.RegisterHeadService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeasureBlockServiceImpl implements MeasureBlockService{

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private MeasureBlockHeadRepository measureBlockHeadRepository;
//    @Autowired
//    private RegisterDetailService registerDetailService;
    @Autowired
    private RegisterDetailRepository repository;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private RegisterHeadService registerHeadService;
    @Autowired
    private AssetHeadRepository assetHeadRepository;

    @Override
    public MeasureBlockHead save(MeasureBlockHead measureBlockHead) {
        return measureBlockHeadRepository.save(measureBlockHead);
    }

    @Override
    public MeasureBlockHead updateMeasureBlockHead(MeasureBlockHead measureBlockHead) {
        return measureBlockHeadRepository.save(measureBlockHead);
    }

    @Override
    public MeasureBlockHead findById(Long id) {
        return measureBlockHeadRepository.findById(id).orElseThrow(() -> new NotFoundException(MeasureBlockHead.class, id));
    }

    @Override
    public List<MeasureBlockHead> findAllByRegModuleId(Long regModuleId) {
        return measureBlockHeadRepository.findAllByRegModuleIdOrderByIdDesc(regModuleId);
    }

    @Override
    public List<MeasureBlockHead> findAll() {
        return measureBlockHeadRepository.findAll();
    }

    @Override
    public List<MeasureBlockHead> findAllByIdInOrderByIdAsc(List<Long> ids) {
        return measureBlockHeadRepository.findAllByIdInOrderByIdAsc(ids);
    }

    @Override
    public String getBlockHeaderAndFormat(Long registerId,Long blockId) {
        RegisterHead registerHead = registerHeadService.findById(registerId);
        List<RegisterDetail> registerDetails = repository.findAllByRegisterHeadAndMeasureBlockId(registerHead,blockId);
        JSONArray headerAndFormatArray= new JSONArray();

        if (registerDetails.size()!=0) {
            List<Long> measureIds = registerDetails.stream().map(RegisterDetail::getMeasureCodeId).collect(Collectors.toList());
            MeasureDefinitionTemplateDTO headerAndFormat = measureDefinitionOverrideService.getAllHeaderAndFormat(measureIds);
            String registerDetailElement = null;
            try {
                registerDetails.forEach(detail->{ detail.setRegisterHead(null); });
                ObjectMapper o = new ObjectMapper().registerModule(new JavaTimeModule());
                registerDetailElement = o.writerWithDefaultPrettyPrinter().writeValueAsString(registerDetails);
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getMessage(), e);
            }
            if(headerAndFormat!=null){
                headerAndFormatArray.put(new JSONArray().put(headerAndFormat.getMeasureIds().concat(",")));
                headerAndFormatArray.put(new JSONArray().put(headerAndFormat.getMeasureNames().concat(",Pallet Number")));
                headerAndFormatArray.put(new JSONArray().put(headerAndFormat.getFormats().concat(",TEXT")));
                headerAndFormatArray.put(new JSONArray().put(registerDetailElement));
            }
        }
        return headerAndFormatArray.toString();
    }

    @Override
    public void delete(Long id) {
        measureBlockHeadRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        measureBlockHeadRepository.deleteAll();
    }

    @Override
    public List<MeasureDefinitionTenantDTO> getSerialHeaderForCSV(Long assetId) {

        AssetHead assetHead = assetHeadRepository.findById(assetId).get();
        RegisterHead registerHead = registerHeadService.findById(assetHead.getRegisterId());
        List<RegisterDetail> registerDetails = repository.findAllByRegisterHeadAndMeasureBlockId(registerHead,1l);

        List<MeasureDefinitionTenantDTO> measureDefinitionTenantDTOS = null;
        if (registerDetails.size()!=0) {
            List<Long> measureIds = registerDetails.stream().map(RegisterDetail::getMeasureCodeId).collect(Collectors.toList());
            measureDefinitionTenantDTOS = measureDefinitionOverrideService.findByIds(measureIds);

        }
        return measureDefinitionTenantDTOS;
    }

}
