package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.register.RegisterHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegisterHeadRepository extends JpaRepository<RegisterHead, Long> {

    @Query("select rh from RegisterHead rh where rh.id in (:ids)")
    List<RegisterHead> findByIdsIn(List<Long> ids);

    @Query("select rh from RegisterHead rh LEFT JOIN FETCH rh.registerDetails where rh.refName=:refName")
    RegisterHead findByRefName(String refName);

    //List<RegisterHead> findByRegisterCodeAndStatus(String registerCode, String status);

    // List<RegisterHead> findByRegisterCode(String registerCode);

    List<RegisterHead> findAllByRegModuleId(Long regModuleId);

}
