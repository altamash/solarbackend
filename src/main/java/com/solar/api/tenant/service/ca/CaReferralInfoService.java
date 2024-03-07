package com.solar.api.tenant.service.ca;

import com.solar.api.saas.service.CrudService;
import com.solar.api.tenant.mapper.ca.CaReferralInfoDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.ca.CaReferralInfo;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.user.User;

import java.util.List;

public interface CaReferralInfoService<T>  extends CrudService<T>  {

    CaReferralInfo save(CaReferralInfoDTO caReferralInfoDto, User user);

    CaReferralInfo saveV2(CaReferralInfoDTO caReferralInfoDto, UserDTO userDTO);

    CaReferralInfo getByEntity(Entity entity);
    List<CaReferralInfo> getAllByEntityIds(List<Long> entityIds);
    List<CaReferralInfo> saveAll(List<CaReferralInfo> caReferralInfoList);
    }
