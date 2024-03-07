package com.solar.api.tenant.service.process.permission;

import java.util.List;
import java.util.Set;

public interface UserPermissions {

    Set<String> getUserPermissions(String compReference);

    String getPPK2();

    List<String> getUserPermissionsEncrypted(String compReference);
}
