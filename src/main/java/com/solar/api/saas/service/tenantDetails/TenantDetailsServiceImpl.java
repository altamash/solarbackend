package com.solar.api.saas.service.tenantDetails;

import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//@Component("tenantDetailsService")
@Service("tenantDetailsService")
public class TenantDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MasterTenantRepository masterTenantRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        MasterTenant user = masterTenantRepository.findByUserNameFetchTenantPermissions(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return TenantDetailsImpl.build(user);
    }

}
