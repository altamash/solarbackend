package com.solar.api.tenant.service.extended.partner;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.mapper.extended.partner.PartnerMapper;
import com.solar.api.tenant.model.extended.partner.PartnerDetail;
import com.solar.api.tenant.model.extended.partner.PartnerHead;
import com.solar.api.tenant.repository.PartnerDetailRepository;
import com.solar.api.tenant.repository.PartnerHeadRepository;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class PartnerHeadServiceImpl implements PartnerHeadService {

    @Autowired
    private PartnerHeadRepository repository;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private PartnerDetailRepository partnerDetailRepository;

    @Override
    public PartnerHead save(PartnerHead partnerHead) {
        PartnerHead partnerHeadSaved = repository.save(partnerHead);
        List<PartnerDetail> partnerDetailsUpd;
        if(partnerHeadSaved.getPartnerDetails().size()!=0){
            partnerDetailsUpd = new ArrayList<>();
            partnerHeadSaved.getPartnerDetails().forEach(partnerDetail -> {
                if(partnerDetail.getMeasureCodeId()!=null){
                    MeasureDefinitionTenantDTO measureDefinitionTenantDb =
                            measureDefinitionOverrideService.findById(partnerDetail.getMeasureCodeId());
                    partnerDetail.setPartnerHead(partnerHeadSaved);
                    partnerDetail.setMeasureCodeId(partnerDetail.getMeasureCodeId());
                    partnerDetail.setMeasure(partnerDetail.getMeasure());
                    partnerDetail.setMeasureDefinitionTenant(measureDefinitionTenantDb);
                    partnerDetailsUpd.add(partnerDetail);
                }
            });
            if(partnerDetailsUpd.size()!=0){
                partnerDetailRepository.saveAll(partnerDetailsUpd);
            }
        }
        return partnerHeadSaved;
    }

    @Override
    public PartnerHead update(PartnerHead partnerHead) {
        PartnerHead partnerHeadDb = findById(partnerHead.getId());
        partnerHead = PartnerMapper.toUpdatedPartnerHead(partnerHeadDb, partnerHead);
        return repository.save(partnerHead);
    }

    @Override
    public PartnerHead findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(PartnerHead.class, id));
    }

    @Override
    public List<PartnerHead> findAll() {
        List<PartnerHead> partnerHeads = repository.findAll();
        partnerHeads.forEach(partnerHead-> {
            if (partnerHead.getPartnerDetails().size()!=0) {
                partnerHead.getPartnerDetails().forEach(detail -> {
                    MeasureDefinitionTenantDTO measureDefinitionTenantDb = measureDefinitionOverrideService.findById(detail.getMeasureCodeId());
                    detail.setMeasureCodeId(detail.getMeasureCodeId());
                    detail.setMeasureDefinitionTenant(measureDefinitionTenantDb);
                    detail.setMeasure(measureDefinitionTenantDb.getMeasure());
                });
            }
        });
        return partnerHeads;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public List<PartnerHead> findAllByRegisterId(Long registerId) {
        return repository.findAllByRegisterId(registerId);
    }
}
