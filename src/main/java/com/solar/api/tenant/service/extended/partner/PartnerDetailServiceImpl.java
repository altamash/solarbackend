package com.solar.api.tenant.service.extended.partner;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.partner.PartnerMapper;
import com.solar.api.tenant.model.extended.partner.PartnerDetail;
import com.solar.api.tenant.repository.PartnerDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class PartnerDetailServiceImpl implements PartnerDetailService {

    @Autowired
    private PartnerDetailRepository repository;

    @Override
    public PartnerDetail save(PartnerDetail partnerDetail) {
        return repository.save(partnerDetail);
    }

    @Override
    public List<PartnerDetail> saveAll(List<PartnerDetail> partnerDetails) {
        return repository.saveAll(partnerDetails);
    }

    @Override
    public PartnerDetail update(PartnerDetail partnerDetail) {
        PartnerDetail partnerDetailDb = findById(partnerDetail.getId());
        partnerDetail = PartnerMapper.toUpdatedPartnerDetail(partnerDetailDb, partnerDetail);
        return repository.save(partnerDetail);
    }

    @Override
    public List<PartnerDetail> updateAll(List<PartnerDetail> partnerDetails) {
        List<PartnerDetail> partnerDetailsUpd = new ArrayList<>();
        partnerDetails.forEach(partnerDetail -> {
            PartnerDetail partnerDetailDb = findById(partnerDetail.getId());
            partnerDetailDb = PartnerMapper.toUpdatedPartnerDetail(partnerDetailDb, partnerDetail);
            partnerDetailsUpd.add(partnerDetailDb);
        });
        return repository.saveAll(partnerDetailsUpd);
    }

    @Override
    public PartnerDetail findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(PartnerDetail.class, id));
    }

    @Override
    public List<PartnerDetail> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
