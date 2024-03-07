package com.solar.api.tenant.service.extended.resources;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.mapper.extended.resources.HRMapper;
import com.solar.api.tenant.model.extended.resources.HRDetail;
import com.solar.api.tenant.model.extended.resources.HRHead;
import com.solar.api.tenant.repository.HRDetailRepository;
import com.solar.api.tenant.repository.HRHeadRepository;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@Transactional("tenantTransactionManager")
public class HRHeadServiceImpl implements HRHeadService {

    @Autowired
    private HRHeadRepository hrHeadRepository;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private HRDetailRepository hrDetailRepository;

    @Override
    public HRHead save(HRHead hrHead) {
        HRHead hrHeadSaved = hrHeadRepository.save(hrHead);
        List<HRDetail> hrDetails;
        if(hrHeadSaved.getHrDetails().size()!=0){
            hrDetails = new ArrayList<>();
            hrHeadSaved.getHrDetails().forEach(hrDetail -> {
                if(hrDetail.getMeasureCodeId()!=null){
                    MeasureDefinitionTenantDTO measureDefinitionTenantDb = measureDefinitionOverrideService.findById(hrDetail.getMeasureCodeId());
                    hrDetail.setMeasure(measureDefinitionTenantDb.getMeasure());
                    hrDetail.setMeasureCodeId(hrDetail.getMeasureCodeId());
                    hrDetail.setMeasureDefinitionTenant(measureDefinitionTenantDb);
                }
                hrDetail.setHrHead(hrHeadSaved);
                hrDetails.add(hrDetail);
            });
            if(hrDetails.size()!=0){
                hrDetailRepository.saveAll(hrDetails);
            }
        }
        return hrHeadSaved;
    }

    @Override
    public HRHead update(HRHead hrHead) {
        HRHead hrHeadDb = findById(hrHead.getId());
        hrHead = HRMapper.toUpdatedHRHead(hrHeadDb, hrHead);
        hrHeadRepository.save(hrHead);
        return hrHead;
    }

    @Override
    public HRHead findById(Long id) {
        HRHead hrHead = hrHeadRepository.findById(id).orElseThrow(() -> new NotFoundException(HRHead.class, id));
        hrHead.getHrDetails().forEach(detail -> {
            MeasureDefinitionTenantDTO measureDefinitionTenantDb = measureDefinitionOverrideService.findById(detail.getMeasureCodeId());
            detail.setMeasure(measureDefinitionTenantDb.getMeasure());
            detail.setMeasureDefinitionTenant(measureDefinitionTenantDb);
        });
        return hrHead;
    }

    @Override
    public List<HRHead> findAll() {
        return hrHeadRepository.findAll();
    }

    @Override
    public List<HRHead> findAllByRegisterId(Long registerId) {
        List<HRHead> hrHeads = hrHeadRepository.findAllByRegisterId(registerId);
        hrHeads.forEach(hrHead-> {
            if (hrHead.getHrDetails().size()!=0) {
                hrHead.getHrDetails().forEach(detail -> {
                    MeasureDefinitionTenantDTO measureDefinitionTenantDb = measureDefinitionOverrideService.findById(detail.getMeasureCodeId());
                    detail.setMeasureCodeId(detail.getMeasureCodeId());
                    detail.setMeasureDefinitionTenant(measureDefinitionTenantDb);
                    detail.setMeasure(measureDefinitionTenantDb.getMeasure());
                });
            }
        });
        return hrHeads;
    }

    @Override
    public HRHead findByExternalReferenceId(String externalReferenceId) {
        return hrHeadRepository.findByExternalReferenceId(externalReferenceId);
    }

    @Override
    public HRHead findByLoginUser(Long userId) {
        return hrHeadRepository.findByLoginUser(userId);
    }

    @Override
    public void delete(Long id) {
        hrHeadRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        hrHeadRepository.deleteAll();
    }

    @Override
    public List<HRHead> findAllByIdIn(List<Long> ids) {
        List<HRHead> hrHeads = hrHeadRepository.findAllByIdIn(ids);
        if (hrHeads.size() != 0) {
            hrHeads.stream().map(hr -> getName(hr)).collect(Collectors.toList());
        }
        return hrHeads;
    }

    private HRHead getName(HRHead hrHead) {
        StringBuilder resName = new StringBuilder().append(hrHead.getFirstName() != null ? hrHead.getFirstName() : "")
                .append(hrHead.getMiddleName() != null ? hrHead.getMiddleName() : "")
                .append(hrHead.getLastName() != null ? hrHead.getLastName() : "");
        hrHead.setName(resName.toString());
        return hrHead;
    }
}
