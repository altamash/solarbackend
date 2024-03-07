// ----------------------------------------------------------------------------
// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.
// ----------------------------------------------------------------------------

package com.solar.api.tenant.model.powerBI;

import com.solar.api.tenant.controller.v1.PowerBIEmbedController;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties for embedding the report
 */
public class ReportConfig {
    static final Logger logger = LoggerFactory.getLogger(PowerBIEmbedController.class);

    public String reportId = "";

    public String embedUrl = "";

    public String reportName = "";

    public Boolean isEffectiveIdentityRolesRequired = false;

    public Boolean isEffectiveIdentityRequired = false;

    public Boolean enableRLS = false;

    public String username;

    public String roles;

    public JSONObject getJSONObject() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("reportId", reportId);
            jsonObj.put("embedUrl", embedUrl);
            jsonObj.put("reportName", reportName);
        } catch (JSONException e) {
            logger.error("DefaultListItem.toString JSONException: " + e.getMessage());
        }
        return jsonObj;
    }
}
