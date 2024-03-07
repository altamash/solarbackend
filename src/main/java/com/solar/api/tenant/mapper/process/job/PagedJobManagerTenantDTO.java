package com.solar.api.tenant.mapper.process.job;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PagedJobManagerTenantDTO {
    long totalItems;
    List<JobManagerTenantDTO> jobs;
}
