package com.solar.api.tenant.service.extended.resources;

import com.solar.api.tenant.model.extended.resources.HRDetail;

import java.util.List;

public interface HRDetailService {

    HRDetail save(HRDetail hrDetail);

    List<HRDetail> saveAll(List<HRDetail> hrDetails);

    List<HRDetail> update(List<HRDetail> hrDetails);

    HRDetail findById(Long id);

    List<HRDetail> findAll();

    void delete(Long id);

    void deleteAll();
}
