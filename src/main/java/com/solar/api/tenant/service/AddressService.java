package com.solar.api.tenant.service;

import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;

import java.util.List;

public interface AddressService {

    Address saveOrUpdate(Address address);

    List<Address> save(List<Address> addresses);

    Address findById(Long id);

    Address findByIdNoThrow(Long id);

    List<Address> findAddressByUserAccount(Long userId);

    Address findByUserAccountAndAlias(User userAccount, String alias);

    List<Address> findAll();

    List<Address> findSiteAddressWithAlias(String siteCode, Long customerSubscriptionId, Long accountId);

    void delete(Long id);

    void deleteAll();
}
