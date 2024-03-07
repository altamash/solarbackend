package com.solar.api.tenant.service.projectListing;

import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.ca.CaReferralInfoDTO;
import com.solar.api.tenant.mapper.ca.CaSoftCreditCheckDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.ca.CaUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectListingService {
    ResponseEntity<?> getAllManagers();

}
