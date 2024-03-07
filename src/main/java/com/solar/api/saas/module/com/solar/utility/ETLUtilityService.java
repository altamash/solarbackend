package com.solar.api.saas.module.com.solar.utility;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.model.externalFile.ExternalFile;
import org.springframework.web.multipart.MultipartFile;


public interface ETLUtilityService {

    ObjectNode etl(ExternalFile externalFile, MultipartFile file, String compKey, String mongoSubId);
}
