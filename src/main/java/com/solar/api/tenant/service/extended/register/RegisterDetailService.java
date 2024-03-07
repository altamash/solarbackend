package com.solar.api.tenant.service.extended.register;

import com.solar.api.tenant.model.extended.register.RegisterDetail;
import com.solar.api.tenant.model.extended.register.RegisterHead;

import java.util.List;

public interface RegisterDetailService {

    RegisterDetail save(RegisterDetail registerDetail);

    List<RegisterDetail> saveAll(List<RegisterDetail> registerDetails);

    List<RegisterDetail> update(List<RegisterDetail> registerDetails, RegisterHead registerHeadUpd);

    RegisterDetail findById(Long id);

    List<RegisterDetail> findByRegister(RegisterHead registerHead);

    List<RegisterDetail> findByRegisterIdAndBlockIdNotNull(Long registerId);

    List<RegisterDetail> findAll();

    // aka SubscriptionRateMatrixDetail
//    List<SubscriptionRateMatrixDetail> findBySubscriptionRateMatrixId(Long id);

    //    SubscriptionRateMatrixDetail findBySubscriptionRateMatrixIdAndRateCode(Long id, String code); // Partial
    RegisterDetail findByMeasureCode(String measureCode); // Partial

    //    List<String> findRateCodesBySubscriptionRateMatrixIdAndVaryByCustomer(Long subscriptionRateMatrixId,
    //    Boolean varyByCustomer);
    List<String> findMeasureCodesByVariableByDetail(String variableByDetail); // Partial

    List<RegisterDetail> findAllByRegisterAndBlockId(RegisterHead registerHead, Long blockId);

    // aka SubscriptionRateMatrixDetail
    void delete(Long id);

    void deleteAll();

    void deleteAll(List<RegisterDetail> registerDetails);

}
