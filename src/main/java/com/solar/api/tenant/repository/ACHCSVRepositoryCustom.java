package com.solar.api.tenant.repository;


import com.solar.api.tenant.mapper.billing.billingHead.ACHFileDTO;

import java.util.List;

public interface ACHCSVRepositoryCustom {

    List<ACHFileDTO> getACHCSV();
}
