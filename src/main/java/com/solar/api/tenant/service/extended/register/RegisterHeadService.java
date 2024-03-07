package com.solar.api.tenant.service.extended.register;

import com.solar.api.tenant.model.extended.register.RegisterHead;

import java.util.List;

public interface RegisterHeadService {

    RegisterHead save(RegisterHead registerHead);

    RegisterHead update(RegisterHead registerHead);

    RegisterHead findById(Long id);

    List<RegisterHead> findAll();

    // aka SubscriptionRateMatrixHead
    RegisterHead findByRefName(String refName);

    RegisterHead findMeasureByRegisterId(Long registerHeadId);

    //List<RegisterHead> findByRegisterCodeAndStatus(String registerCode, String status);

    List<RegisterHead> findByRegisterIdsIn(List<Long> ids);
    // aka SubscriptionRateMatrixHead

    //List<RegisterHead> findByRegisterCode(String registerCode);

    List<RegisterHead> findAllByRegModuleId(Long regModuleId);

    void delete(Long id);

    void deleteAll();
}
