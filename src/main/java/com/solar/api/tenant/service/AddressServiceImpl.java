package com.solar.api.tenant.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.user.address.AddressMapper;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserService userService;

    @Override
    public Address saveOrUpdate(Address address) {
        if (address.getId() != null) {
            Address a = findById(address.getId());
            a = AddressMapper.toUpdatedAddress(a, address);
            if (address.getAcctId() != null) {
                User account = userService.findById(address.getAcctId());
                address.setUserAccount(account);
            }
            return addressRepository.save(a);
        }
        if (address.getAcctId() != null) {
            User account = userService.findById(address.getAcctId());
            address.setUserAccount(account);
        }
        return addressRepository.save(address);
    }

    @Override
    public List<Address> save(List<Address> addresses) {
        return addressRepository.saveAll(addresses);
    }

    @Override
    public Address findById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new NotFoundException(Address.class, id));
    }

    @Override
    public Address findByIdNoThrow(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    public List<Address> findAddressByUserAccount(Long userId) {
        User user = userService.findById(userId);
        return addressRepository.findByUserAccount(user);
    }

    @Override
    public Address findByUserAccountAndAlias(User userAccount, String alias) {
        return addressRepository.findByUserAccountAndAlias(userAccount, alias);
    }

    @Override
    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    @Override
    public List<Address> findSiteAddressWithAlias(String siteCode, Long customerSubscriptionId, Long accountId) {
        return addressRepository.findSiteAddressWithAlias(siteCode, customerSubscriptionId, accountId);
    }

    @Override
    public void delete(Long id) {
        addressRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        addressRepository.deleteAll();
    }
}
