package com.solar.api.tenant.service.userDetails;

import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//@Component("userDetailsService")
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
//    @Autowired
//    private PermissionSetRepository permissionSetRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//        User user = userRepository.findByUserNameFetchRoles(username);
        User user = userRepository.findByUserNameFetchPermissions(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        /*List<PermissionSet> permissions = permissionSetRepository.getPermissionSetsFetchComponentLibrary(user.getRoles()
                .stream().flatMap(r -> r.getPermissionSets().stream()).collect(Collectors.toSet()));*/
        /*Set<String> libraryPermissions = permissions.stream().flatMap(p -> p.getComponentLibraries().stream())
                .map(c -> c.getComponentName()).collect(Collectors.toSet());*/
        return UserDetailsImpl.build(user);
    }

}
