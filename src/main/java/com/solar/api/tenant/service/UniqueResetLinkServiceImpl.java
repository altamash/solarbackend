package com.solar.api.tenant.service;

import com.solar.api.tenant.model.user.UniqueResetLink;
import com.solar.api.tenant.repository.UniqueResetLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class UniqueResetLinkServiceImpl implements UniqueResetLinkService {

    @Autowired
    UniqueResetLinkRepository uniqueResetLinkRepository;

    @Override
    public UniqueResetLink save(UniqueResetLink uniqueResetLink) {
        return uniqueResetLinkRepository.save(uniqueResetLink);
    }

    @Override
    public UniqueResetLink findByUniqueText(String uniqueText) {
        return uniqueResetLinkRepository.findByUniqueText(uniqueText);
    }

    @Override
    public UniqueResetLink findByAdminAccount(Long adminAccount) {
        return uniqueResetLinkRepository.findByAdminAccount(adminAccount);
    }

    @Override
    public List<UniqueResetLink> findAll() {
        return uniqueResetLinkRepository.findAll();
    }

}
