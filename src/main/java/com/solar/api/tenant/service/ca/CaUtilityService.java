package com.solar.api.tenant.service.ca;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.ca.CaSoftCreditCheckDTO;
import com.solar.api.tenant.mapper.ca.CaUtilityDTO;
import com.solar.api.tenant.model.BaseResponse;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CaUtilityService<T>{
    CaUtility save(CaUtilityDTO caUtilityDTO, User user, List<MultipartFile> multipartFiles);

    List<CaUtility> saveAll(List<CaUtility> caUtilities);
    CaUtility save(CaUtilityDTO caUtilityDTO);
    CaUtility update(CaUtility obj);
    String delete(Long id);
    List<CaUtility> getAll();
    CaUtility getById(Long id);
    List<CaUtility> getByEntity(Entity entity);
    ObjectNode markUtilityAsPrimary(Long entityId, Long utilityId);

}
