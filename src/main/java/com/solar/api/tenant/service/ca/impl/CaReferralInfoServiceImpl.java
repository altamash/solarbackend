package com.solar.api.tenant.service.ca.impl;

import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.tenant.mapper.ca.CaReferralInfoDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.ca.CaReferralInfo;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.ca.CaReferralInfoRepository;
import com.solar.api.tenant.repository.contract.EntityRepository;
import com.solar.api.tenant.service.ca.CaReferralInfoService;
import com.solar.api.tenant.service.contract.EntityService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.solar.api.tenant.mapper.ca.CaReferralInfoMapper.*;

@Service
@AllArgsConstructor
public class CaReferralInfoServiceImpl implements CaReferralInfoService<CaReferralInfo> {

    @Autowired
    private final CaReferralInfoRepository caReferralInfoRepository;
    @Autowired
    private EntityService entityService;
    @Autowired
    private EntityRepository entityRepository;

    @Override
    public CaReferralInfo save(CaReferralInfoDTO caReferralInfoDto, User user) {
        Entity entity = entityService.findEntityByUserId(user.getAcctId());
        if (entity == null)
            throw new SolarApiException("can not find entity with entity id=" + caReferralInfoDto.getEntityId());
        CaReferralInfo caReferralInfo = toCaReferralInfo(caReferralInfoDto);
        caReferralInfo.setEntity(entity);
        return caReferralInfoRepository.save(caReferralInfo);
    }
    @Override
    public CaReferralInfo saveV2(CaReferralInfoDTO caReferralInfoDto, UserDTO userDTO) {
        Entity entity = entityService.findEntityByUserId(userDTO.getAcctId());
        if (entity == null)
            throw new SolarApiException("can not find entity with entity id=" + caReferralInfoDto.getEntityId());
        CaReferralInfo caReferralInfo = toCaReferralInfoV2(caReferralInfoDto);
        caReferralInfo.setEntity(entity);
        return caReferralInfoRepository.save(caReferralInfo);
    }


    @Override
    public CaReferralInfo getByEntity(Entity entity) {
        return caReferralInfoRepository.findByEntity(entity);
    }

    @Override
    public CaReferralInfo save(CaReferralInfo obj) {
        return null;
    }

    @Override
    public CaReferralInfo update(CaReferralInfo obj) {
        CaReferralInfo caReferralInfoData = caReferralInfoRepository.findById(obj.getId())
                .orElseThrow(() -> new NotFoundException(" Referral Info id not found: " + obj.getId()));
        return caReferralInfoRepository.save(toUpdateCaReferralInfo(caReferralInfoData, obj));
    }

    @Override
    public String delete(Long id) {
        caReferralInfoRepository.deleteById(id);
        return "deleted";
    }

    @Override
    public List<CaReferralInfo> getAll() {
        return caReferralInfoRepository.findAll();
    }

    @Override
    public CaReferralInfo getById(Long id) {
        return caReferralInfoRepository.findById(id).orElseThrow(() -> new RuntimeException(" Referral Info id not found: " + id));
    }

    @Override
    public List<CaReferralInfo> getAllByEntityIds(List<Long> entityIds) {
        return caReferralInfoRepository.findAllByEntityIds(entityIds);
    }
    @Override
    public List<CaReferralInfo> saveAll(List<CaReferralInfo> caReferralInfoList) {
        return caReferralInfoRepository.saveAll(caReferralInfoList);
    }

}
