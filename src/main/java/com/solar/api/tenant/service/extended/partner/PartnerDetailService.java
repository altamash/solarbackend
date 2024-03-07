package com.solar.api.tenant.service.extended.partner;

import com.solar.api.tenant.model.extended.partner.PartnerDetail;

import java.util.List;

public interface PartnerDetailService {

    PartnerDetail save(PartnerDetail partnerDetail);

    List<PartnerDetail> saveAll(List<PartnerDetail> partnerDetails);

    PartnerDetail update(PartnerDetail partnerDetail);

    List<PartnerDetail> updateAll(List<PartnerDetail> partnerDetails);

    PartnerDetail findById(Long id);

    List<PartnerDetail> findAll();

    void delete(Long id);

    void deleteAll();
}
