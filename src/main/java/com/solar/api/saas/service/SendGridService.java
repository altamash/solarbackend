package com.solar.api.saas.service;

import com.solar.api.saas.mapper.sendgridDTO.TemplateDTO;
import com.solar.api.saas.mapper.sendgridDTO.VersionDTO;

public interface SendGridService {

    TemplateDTO addTemplate(TemplateDTO templateDTO);

    VersionDTO addTemplateAndVersion(TemplateDTO templateDTO);

    VersionDTO addVersion(VersionDTO versionDTO);
}
