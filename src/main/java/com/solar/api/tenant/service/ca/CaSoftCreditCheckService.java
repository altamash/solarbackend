package com.solar.api.tenant.service.ca;

import com.solar.api.saas.service.CrudService;
import com.solar.api.tenant.mapper.ca.CaSoftCreditCheckDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.ca.CaSoftCreditCheck;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.user.User;

public interface CaSoftCreditCheckService<T>  extends CrudService<T>  {

    CaSoftCreditCheck saveV2(CaSoftCreditCheckDTO caSoftCreditCheckDTO, UserDTO userDTO);

    CaSoftCreditCheck getByEntity(Entity entity);
    CaSoftCreditCheck save(CaSoftCreditCheckDTO caSoftCreditCheckDTO, User user);

    }
