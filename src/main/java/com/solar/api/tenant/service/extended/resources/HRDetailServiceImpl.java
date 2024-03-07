package com.solar.api.tenant.service.extended.resources;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.resources.HRMapper;
import com.solar.api.tenant.model.extended.resources.HRDetail;
import com.solar.api.tenant.repository.HRDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HRDetailServiceImpl implements HRDetailService {

    @Autowired
    private HRDetailRepository hrDetailRepository;

    @Override
    public HRDetail save(HRDetail hrDetail) {
        return hrDetailRepository.save(hrDetail);
    }

    @Override
    public List<HRDetail> saveAll(List<HRDetail> hrDetails) {
        return hrDetailRepository.saveAll(hrDetails);
    }

    @Override
    public List<HRDetail> update(List<HRDetail> hrDetails) {
        List<HRDetail> addHrDetail = new ArrayList<>();
        hrDetails.forEach(hrDetail -> {
            HRDetail hrDetailDb = hrDetailRepository.findById(hrDetail.getId()).get();
            hrDetail = HRMapper.toUpdatedHRDetail(hrDetailDb, hrDetail);
            addHrDetail.add(hrDetail);
        });

       /*
        for (HRDetail hrDetail : hrDetails) {
            HRDetail hrDetailDb = hrDetailRepository.findByHrHeadIdAndMeasure(hrDetail.getHrHeadId(),
                    hrDetail.getMeasure());
            if (hrDetailDb == null) {
                hrDetailDb = new HRDetail();
                hrDetailDb.setHrHeadId(hrDetail.getHrHeadId());
            }
            hrDetail = HRMapper.toUpdatedHRDetail(hrDetailDb, hrDetail);
            addHrDetail.add(hrDetail);
        }*/
        return hrDetailRepository.saveAll(addHrDetail);
    }

    @Override
    public HRDetail findById(Long id) {
        return hrDetailRepository.findById(id).orElseThrow(() -> new NotFoundException(HRDetail.class, id));
    }

    @Override
    public List<HRDetail> findAll() {
        return hrDetailRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        hrDetailRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        hrDetailRepository.deleteAll();
    }
}
