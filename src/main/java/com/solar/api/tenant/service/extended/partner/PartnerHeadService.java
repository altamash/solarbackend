package com.solar.api.tenant.service.extended.partner;

import com.solar.api.tenant.model.extended.partner.PartnerHead;

import java.util.List;

public interface PartnerHeadService {

    PartnerHead save(PartnerHead partnerHead);

    PartnerHead update(PartnerHead partnerHead);

    PartnerHead findById(Long id);

    List<PartnerHead> findAll();

    void delete(Long id);

    void deleteAll();

    List<PartnerHead> findAllByRegisterId(Long registerId);
}
