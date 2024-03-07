package com.solar.api.tenant.service.alerts;

import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.model.TenantConfig;

import java.util.Map;

public interface AlertService {

    BaseResponse superSendEmailTrigger(TenantConfig tenantConfig, String subject, String emailToList,
                                       String emailCCList, String emailBCCList, Map<String, String> json);

    BaseResponse superSendEmailTrigger(TenantConfig tenantConfig, String subject, String emailToList,
                                       String emailCCList, String emailBCCList, String placeHolderJSON);
}
