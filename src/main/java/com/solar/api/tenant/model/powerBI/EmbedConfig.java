// ----------------------------------------------------------------------------
// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.
// ----------------------------------------------------------------------------

package com.solar.api.tenant.model.powerBI;

import java.util.List;

/**
 * Properties for embedding the report
 */
public class EmbedConfig {
    public List<ReportConfig> embedReports;

    public EmbedToken embedToken;

    public String errorMessage;
}
