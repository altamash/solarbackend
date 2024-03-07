package com.solar.api.tenant.service.userGroup;

import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupDTO;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface DefaultUserGroupNewService {
    ResponseEntity<Object> saveOrUpdate(List<DefaultUserGroupDTO> defaultUserGroupDTOList);
}
