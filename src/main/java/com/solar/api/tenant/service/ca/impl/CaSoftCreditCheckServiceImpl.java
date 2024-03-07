package com.solar.api.tenant.service.ca.impl;

import com.solar.api.exception.SolarApiException;
import com.solar.api.tenant.mapper.ca.CaSoftCreditCheckDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.ca.CaSoftCreditCheck;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.ca.CaSoftCreditCheckRepository;
import com.solar.api.tenant.service.ca.CaSoftCreditCheckService;
import com.solar.api.tenant.service.contract.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.solar.api.tenant.mapper.ca.CaSoftCreditCheckMapper.toCaSoftCreditCheck;
import static com.solar.api.tenant.mapper.ca.CaSoftCreditCheckMapper.toCaSoftCreditCheckV2;

@Service
public class CaSoftCreditCheckServiceImpl implements CaSoftCreditCheckService {
    @Autowired
    private EntityService entityService;
    @Autowired
    private CaSoftCreditCheckRepository caSoftCreditCheckRepository;

    @Override
    public CaSoftCreditCheck save(CaSoftCreditCheckDTO caSoftCreditCheckDTO, User user) {
        Entity entity = entityService.findEntityByUserId(user.getAcctId());
        if(entity == null) throw new SolarApiException("can not find entity with entity id="+caSoftCreditCheckDTO.getCustomerNo().getId());
        CaSoftCreditCheck caSoftCreditCheck = toCaSoftCreditCheck(caSoftCreditCheckDTO);
        caSoftCreditCheck.setEntity(entity);
        return caSoftCreditCheckRepository.save(caSoftCreditCheck);
    }
    @Override
    public CaSoftCreditCheck saveV2(CaSoftCreditCheckDTO caSoftCreditCheckDTO, UserDTO userDTO) {
        Entity entity = entityService.findEntityByUserId(userDTO.getAcctId());
        if(entity == null) throw new SolarApiException("can not find entity with entity id="+caSoftCreditCheckDTO.getCustomerNo().getId());
        CaSoftCreditCheck caSoftCreditCheck = toCaSoftCreditCheckV2(caSoftCreditCheckDTO);
        caSoftCreditCheck.setEntity(entity);
        return caSoftCreditCheckRepository.save(caSoftCreditCheck);
    }

    @Override
    public Object save(Object obj) {
        return null;
    }

    @Override
    public Object update(Object obj) {
        return null;
    }

    @Override
    public String delete(Long id) {
        caSoftCreditCheckRepository.deleteById(id);
        return "deleted";
    }

    @Override
    public List<CaSoftCreditCheck> getAll() {
        return caSoftCreditCheckRepository.findAll();
    }

    @Override
    public CaSoftCreditCheck getById(Long id) {
        return caSoftCreditCheckRepository.findById(id).orElseThrow(() -> new RuntimeException(" SoftCreditCheck id  not found: " + id));
    }


    @Override
    public CaSoftCreditCheck getByEntity(Entity entity) {
        return caSoftCreditCheckRepository.findByEntity(entity);
    }
}
